/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import static com.google.common.base.Predicates.not;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.jmad.modelpack.gui.panes.FxUtils.*;
import static org.jmad.modelpack.gui.panes.ModelPackGuiUtils.DEFAULT_SPACING;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import javafx.geometry.Insets;
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
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ModelPackagesPane extends TitledPane {

    private final JMadModelPackageService packageService;

    private final PackageFilterModel filterModel;
    private final TreeItem<PackageLine> packagesTableRoot;
    private final Button refreshButton;
    private final SetMultimap<ModelPackage, ModelPackageVariant> map;

    public ModelPackagesPane(JMadModelPackageService packageService, PackageSelectionModel selectionModel) {
        this.packageService = requireNonNull(packageService, "packageService must not be null");

        map = TreeMultimap.create(comparing(ModelPackage::name), ModelPackages.packageVariantComparator());
        filterModel = new PackageFilterModel();
        packagesTableRoot = new TreeItem<>(new PackageLine());
        refreshButton = new Button("Refresh");

        setText("Model Packages");
        setCollapsible(false);
        setFontWeight(this, FontWeight.BOLD);

        TreeTableView<PackageLine> packagesTable = createPackagesSelectionTable(selectionModel);
        VBox packagesOptionsBox = createPackagesOptions();

        HBox content = new HBox(packagesOptionsBox, packagesTable);
        content.setSpacing(DEFAULT_SPACING);
        content.setPadding(new Insets(DEFAULT_SPACING));
        setContent(content);

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

    private TreeTableView<PackageLine> createPackagesSelectionTable(PackageSelectionModel selectionModel) {
        TreeTableColumn<PackageLine, String> packageColumn = new TreeTableColumn<>("Package Name");
        packageColumn.setPrefWidth(250);
        packageColumn.setCellValueFactory(param -> param.getValue().getValue().packageNameProperty());

        TreeTableColumn<PackageLine, String> variantColumn = new TreeTableColumn<>("Variant");
        variantColumn.setPrefWidth(200);
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

        packagesTable.setMinHeight(500);
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
              if(variants.size() == 1) {
                  return new TreeItem<>(variants.get(0));
              }
              TreeItem<PackageLine> folderItem = new TreeItem<>(new PackageLine(variants.get(0).packageName.get()));
              List<TreeItem<PackageLine>> children = variants.stream().map(TreeItem::new).collect(toList());
              folderItem.getChildren().setAll(children);
              return folderItem;
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
