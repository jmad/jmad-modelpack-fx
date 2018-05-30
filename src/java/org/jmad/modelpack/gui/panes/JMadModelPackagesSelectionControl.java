/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import static com.google.common.base.Predicates.not;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static javafx.scene.layout.HBox.setHgrow;
import static org.jmad.modelpack.gui.util.FxUtils.glueToAnchorPane;
import static org.jmad.modelpack.gui.util.FxUtils.onChange;
import static org.jmad.modelpack.gui.util.FxUtils.setFontWeight;
import static org.jmad.modelpack.gui.util.FxUtils.setPercentageWidth;
import static org.jmad.modelpack.gui.util.FxUtils.wrapAndGlueToAnchorPane;
import static org.jmad.modelpack.gui.util.GuiUtils.DEFAULT_SPACING;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.jmad.modelpack.domain.ModelPackage;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.ModelPackages;
import org.jmad.modelpack.domain.Variant;
import org.jmad.modelpack.gui.domain.ModelPackSelectionState;
import org.jmad.modelpack.gui.util.FxUtils;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

public class JMadModelPackagesSelectionControl extends AnchorPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(JMadModelPackagesSelectionControl.class);
    private static final Duration AVAILABLE_PACKAGES_REFRESH_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration CLEAR_CACHE_TIMEOUT = Duration.ofSeconds(30);
    private static final int REFRESH_ANIMATION_MS = 600;
    private static final int OPTIONS_PANE_PREF_WIDTH = 120;

    private final JMadModelPackageService packageService;

    private final VariantTypeFilterControl filterControl;
    private final TreeItem<PackageLine> packagesTableRoot;
    private final SetMultimap<ModelPackage, ModelPackageVariant> map;
    private final Region optionsPane;
    private final Region loadingPane;
    private final Animation loadingAnimation;

    public JMadModelPackagesSelectionControl(JMadModelPackageService packageService, ModelPackSelectionState state) {
        this.packageService = requireNonNull(packageService, "packageService must not be null");
        map = TreeMultimap.create(comparing(ModelPackage::name), ModelPackages.packageVariantComparator());
        filterControl = new VariantTypeFilterControl();
        packagesTableRoot = new TreeItem<>(new PackageLine());

        optionsPane = createPackagesOptionsPane(state);
        loadingPane = createLoadingPane();
        loadingAnimation = createRotateAnimation(loadingPane);

        init(state);

        updatePackages();

        this.filterControl.variantFilterProperty().addListener(onChange(v -> updatePackagesTableView()));
    }

    private void init(ModelPackSelectionState state) {
        TreeTableView<PackageLine> packagesTable = createPackagesSelectionTable(state);

        HBox box = new HBox(new StackPane(optionsPane, loadingPane), packagesTable);
        box.setSpacing(DEFAULT_SPACING);
        box.setPadding(new Insets(DEFAULT_SPACING));
        box.setFillHeight(true);
        setHgrow(packagesTable, Priority.ALWAYS);

        TitledPane titledBox = new TitledPane();
        titledBox.setText("Model Packages");
        titledBox.setCollapsible(false);
        titledBox.setContent(box);
        setFontWeight(titledBox, FontWeight.BOLD);

        glueToAnchorPane(titledBox);
        getChildren().add(titledBox);
    }

    private VBox createPackagesOptionsPane(ModelPackSelectionState state) {
        Button refreshButton = new Button("Refresh");
        Button clearCacheButton = new Button("Clear cache");

        ToggleSwitch onlineSwitch = new ToggleSwitch("Online");
        onlineSwitch.setSelected(state.onlineModeProperty().get());
        onlineSwitch.selectedProperty().addListener(onChange(state.onlineModeProperty()::set));
        onlineSwitch.selectedProperty().addListener(onChange(v -> updatePackages()));

        refreshButton.setOnAction(e -> updatePackages());
        clearCacheButton.setOnAction(e -> clearCache());

        TitledPane variantFilters = new TitledPane("Filters", filterControl);
        variantFilters.setCollapsible(false);
        FxUtils.setFontWeight(variantFilters, FontWeight.BOLD);

        VBox box = new VBox();
        box.setPrefWidth(OPTIONS_PANE_PREF_WIDTH);
        box.setSpacing(DEFAULT_SPACING);
        box.getChildren().add(variantFilters);
        box.getChildren().add(wrapAndGlueToAnchorPane(onlineSwitch));
        box.getChildren().add(wrapAndGlueToAnchorPane(refreshButton));
        box.getChildren().add(FxUtils.createVerticalFiller());
        box.getChildren().add(wrapAndGlueToAnchorPane(clearCacheButton));
        box.setFillWidth(true);

        return box;
    }

    private static Glyph createLoadingPane() {
        Glyph loadingPane = GlyphFontRegistry.font("FontAwesome").create(FontAwesome.Glyph.SPINNER);
        loadingPane.setCacheHint(CacheHint.ROTATE);
        loadingPane.setFontSize(OPTIONS_PANE_PREF_WIDTH * 0.2);
        loadingPane.setColor(Color.GRAY);
        return loadingPane;
    }

    private TreeTableView<PackageLine> createPackagesSelectionTable(ModelPackSelectionState selectionModel) {
        TreeTableColumn<PackageLine, String> packageColumn = new TreeTableColumn<>("Package Name");
        packageColumn.setCellValueFactory(param -> param.getValue().getValue().packageNameProperty());

        TreeTableColumn<PackageLine, String> variantColumn = new TreeTableColumn<>("Variant");
        variantColumn.setCellValueFactory(param -> param.getValue().getValue().variantProperty());

        TreeTableView<PackageLine> packagesTable = new TreeTableView<>(packagesTableRoot);
        packagesTable.setShowRoot(false);
        packagesTable.getColumns().setAll(packageColumn, variantColumn);

        selectionModel.selectedPackageProperty().bind(Bindings.createObjectBinding(() -> {
            TreeItem<PackageLine> treeItem = packagesTable.getSelectionModel().selectedItemProperty().get();
            if (treeItem == null) {
                return null;
            }
            return treeItem.getValue().modelPackageVariant;
        }, packagesTable.getSelectionModel().selectedItemProperty()));

        setPercentageWidth(packagesTable, ImmutableMap.of(packageColumn, 0.7, variantColumn, 0.3));
        return packagesTable;
    }

    private void clearCache() {
        packageService.clearCache().doOnSubscribe(s -> showLoading()).subscribeOn(FxSchedulers.fxThread())
                .publishOn(FxSchedulers.fxThread()).timeout(CLEAR_CACHE_TIMEOUT).doOnError(e -> {
                    LOGGER.error("Error while clearing the service cache", e);
                    hideLoading();
                }).doOnSuccess(v -> hideLoading()).subscribe();
    }

    private void updatePackages() {
        // this.refreshButton.setDisable(true);
        this.clearPackages();
        // @formatter:off
        this.packageService.availablePackages()
                .doOnSubscribe(s -> showLoading())
                .publishOn(FxSchedulers.fxThread())
                .subscribeOn(FxSchedulers.fxThread())
                .timeout(AVAILABLE_PACKAGES_REFRESH_TIMEOUT)
                .doOnComplete(this::hideLoading)
                .doOnError(e -> {
                    LOGGER.error("Error while retrieving available packages", e);
                    hideLoading();
                })
                .subscribe(this::addPackage);
        // @formatter:on
    }

    private void hideLoading() {
        loadingAnimation.stop();
        loadingPane.setVisible(false);
        optionsPane.setDisable(false);
    }

    private void showLoading() {
        optionsPane.setDisable(true);
        loadingPane.setVisible(true);
        loadingAnimation.playFromStart();
    }

    private void addPackage(ModelPackageVariant line) {
        this.map.put(line.modelPackage(), line);
        updatePackagesTableView();
    }

    private void clearPackages() {
        this.map.clear();
        this.packagesTableRoot.getChildren().clear();
    }

    private void updatePackagesTableView() {
        List<TreeItem<PackageLine>> treeItems = treeItemsFor(this.map, filterControl.variantFilterProperty().get());
        this.packagesTableRoot.getChildren().setAll(treeItems);
    }

    private static List<TreeItem<PackageLine>> treeItemsFor(SetMultimap<ModelPackage, ModelPackageVariant> packages,
            Predicate<ModelPackageVariant> filter) {
        // @formatter:off
        return packages.keySet().stream().map(modelPackage -> {
            Set<ModelPackageVariant> packageVariants = packages.get(modelPackage);
            return packageVariants.stream().filter(filter).map(PackageLine::new).collect(toList());
        }).filter(pkgLines -> !pkgLines.isEmpty())
          .map(variants -> {
              TreeItem<PackageLine> item = new TreeItem<>(variants.get(0));
              List<TreeItem<PackageLine>> children = variants.subList(1, variants.size()).stream().map(TreeItem::new).collect(toList());
              item.getChildren().setAll(children);
              return item;
        })
        .collect(toList());
        // @formatter:on
    }

    private static Animation createRotateAnimation(Node graphics) {
        RotateTransition transition = new RotateTransition(javafx.util.Duration.millis(REFRESH_ANIMATION_MS), graphics);
        transition.setFromAngle(0);
        transition.setToAngle(360);
        transition.setCycleCount(Timeline.INDEFINITE);
        transition.setInterpolator(Interpolator.LINEAR);
        return transition;
    }

    private static class PackageLine {

        private final ModelPackageVariant modelPackageVariant;
        private final StringProperty packageName = new SimpleStringProperty();
        private final StringProperty variant = new SimpleStringProperty();

        private PackageLine(ModelPackageVariant variant) {
            this.modelPackageVariant = requireNonNull(variant, "modelPackageVariant must not be null");
            this.packageName.set(variant.modelPackage().name());
            this.variant.set(stringFor(variant.variant()));
        }

        private static String stringFor(Variant variant) {
            return variant.name() + " [" + variant.type().serializedName() + "]";
        }

        private PackageLine() {
            this.modelPackageVariant = null;
            /* empty strings */
        }

        private StringProperty packageNameProperty() {
            return this.packageName;
        }

        private StringProperty variantProperty() {
            return this.variant;
        }

    }

}
