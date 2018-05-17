/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import static com.google.common.base.Predicates.not;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.jmad.modelpack.gui.util.FxUtils.*;
import static org.jmad.modelpack.gui.util.GuiUtils.DEFAULT_SPACING;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.FontWeight;
import org.jmad.modelpack.domain.ModelPackage;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.ModelPackages;
import org.jmad.modelpack.domain.Variant;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.gitlab.domain.AbstractGitVariant;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

import freetimelabs.io.reactorfx.flux.FxFlux;
import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class JMadModelPackagesSelectionPane extends AnchorPane {

    private final JMadModelPackageService packageService;

    private final PackageFilterModel filterModel;
    private final TreeItem<PackageLine> packagesTableRoot;
    private final Button refreshButton;
    private final SetMultimap<ModelPackage, ModelPackageVariant> map;

    public JMadModelPackagesSelectionPane(JMadModelPackageService packageService, ModelPackSelectionState selectionModel) {
        this.packageService = requireNonNull(packageService, "packageService must not be null");

        map = TreeMultimap.create(comparing(ModelPackage::name), ModelPackages.packageVariantComparator());
        filterModel = new PackageFilterModel();
        packagesTableRoot = new TreeItem<>(new PackageLine());
        refreshButton = new Button("Refresh");

        TitledPane box = new TitledPane();
        box.setText("Model Packages");
        box.setCollapsible(false);
        setFontWeight(box, FontWeight.BOLD);

        TreeTableView<PackageLine> packagesTable = createPackagesSelectionTable(selectionModel);
        VBox packagesOptionsBox = createPackagesOptions();

        HBox content = new HBox(packagesOptionsBox, packagesTable);
        HBox.setHgrow(packagesTable, Priority.ALWAYS);
        content.setSpacing(DEFAULT_SPACING);
        content.setPadding(new Insets(DEFAULT_SPACING));
        content.setFillHeight(true);
        box.setContent(content);

        glueToAnchorPane(box);
        getChildren().add(box);

        update();

        // @formatter:off
        FxFlux.from(refreshButton)
                .subscribeOn(FxSchedulers.fxThread())
                .subscribe(o -> this.update());
        // @formatter:on

        this.filterModel.predicateProperty().addListener((p, oldVal, newVal) -> {
            refreshPackages();
        });
    }

    private VBox createPackagesOptions() {
        VBox packagesOptionsBox = new VBox();
        packagesOptionsBox.setSpacing(DEFAULT_SPACING);

        TitledPane variantFilters = new TitledPane("Filters", new PackageFilterPane(filterModel));
        variantFilters.setCollapsible(false);
        packagesOptionsBox.getChildren().add(variantFilters);

        packagesOptionsBox.getChildren().add(refreshButton);
        return packagesOptionsBox;
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

        setPercentageWith(packagesTable, ImmutableMap.of(packageColumn, 0.7, variantColumn, 0.3));
        return packagesTable;
    }

    private void update() {
        // this.refreshButton.setDisable(true);
        this.clearPackages();
        // @formatter:off
        this.packageService.availablePackages()
                .publishOn(FxSchedulers.fxThread())
                .subscribe(this::addPackage);
        // @formatter:on
    }

    private void addPackage(ModelPackageVariant line) {
        this.map.put(line.modelPackage(), line);
        refreshPackages();
    }

    private void clearPackages() {
        this.map.clear();
        this.packagesTableRoot.getChildren().clear();
    }

    private void refreshPackages() {
        List<TreeItem<PackageLine>> treeItems = treeItemsFor(this.map, filterModel.predicateProperty().get());
        this.packagesTableRoot.getChildren().setAll(treeItems);
    }

    private static List<TreeItem<PackageLine>> treeItemsFor(SetMultimap<ModelPackage, ModelPackageVariant> packages,
            Predicate<ModelPackageVariant> filter) {
        // @formatter:off
        return packages.keySet().stream().map(modelPackage -> {
            Set<ModelPackageVariant> packageVariants = packages.get(modelPackage);
            return packageVariants.stream().filter(filter).map(PackageLine::new).collect(toList());
        }).filter(not(List::isEmpty))
          .map(variants -> {
              TreeItem<PackageLine> item = new TreeItem<>(variants.get(0));
              List<TreeItem<PackageLine>> children = variants.subList(1, variants.size()).stream().map(TreeItem::new).collect(toList());
              item.getChildren().setAll(children);
              return item;
        })
        .collect(toList());
        // @formatter:on
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
            if (variant instanceof AbstractGitVariant) {
                return variant.getClass().getSimpleName().toLowerCase() + ": " + variant.name();
            }
            return variant.name();
        }

        private PackageLine(String packageName) {
            this.modelPackageVariant = null;
            this.packageName.setValue(packageName);
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
