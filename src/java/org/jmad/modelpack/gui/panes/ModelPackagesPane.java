/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.jmad.modelpack.domain.ModelPackage;
import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.Variant;
import org.jmad.modelpack.service.JMadModelPackageService;
import org.jmad.modelpack.service.gitlab.domain.Release;
import org.jmad.modelpack.service.gitlab.domain.Tag;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

import freetimelabs.io.reactorfx.flux.FxFlux;
import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;

public class ModelPackagesPane extends BorderPane {

    private final JMadModelPackageService packageService;
    private Button refreshButton;
    private TreeItem<PackageLine> root = new TreeItem<>(new PackageLine());

    private SetMultimap<ModelPackage, ModelPackageVariant> map = TreeMultimap
            .create(Comparator.comparing(ModelPackage::name), newComparator());

    public ModelPackagesPane(JMadModelPackageService packageService) {
        this.packageService = requireNonNull(packageService, "packageService must not be null");

        TreeTableColumn<PackageLine, String> packageColumn = new TreeTableColumn<>("package");
        packageColumn.setPrefWidth(300);
        packageColumn.setCellValueFactory(param -> param.getValue().getValue().packageNameProperty());

        TreeTableColumn<PackageLine, String> variantColumn = new TreeTableColumn<>("variant");
        variantColumn.setPrefWidth(300);
        variantColumn.setCellValueFactory(param -> param.getValue().getValue().variantProperty());

        TreeTableView<PackageLine> treeTableView = new TreeTableView<>(root);
        treeTableView.setShowRoot(false);
        treeTableView.getColumns().setAll(packageColumn, variantColumn);

        setCenter(treeTableView);

        refreshButton = new Button("refresh");
        setBottom(refreshButton);

        refresh();

        // @formatter:off
        FxFlux.from(refreshButton)
                .subscribeOn(FxSchedulers.fxThread())
                .subscribe(o -> this.refresh());
        // @formatter:on
    }

    private void refresh() {
        // this.refreshButton.setDisable(true);
        this.clear();
        // @formatter:off
        this.packageService.availablePackages()
                .publishOn(FxSchedulers.fxThread())
                .subscribe(l -> add(l));
        // @formatter:on
    }

    private void add(ModelPackageVariant line) {
        this.map.put(line.modelPackage(), line);
        List<TreeItem<PackageLine>> treeItems = treeItemsFor(this.map);
        this.root.getChildren().setAll(treeItems);
    }

    private List<TreeItem<PackageLine>> treeItemsFor(SetMultimap<ModelPackage, ModelPackageVariant> map2) {
        // @formatter:off
        return map2.keySet().stream().map(k -> {
            Set<ModelPackageVariant> packages = map2.get(k);
            List<PackageLine> itemsForPackage = packages.stream().map(PackageLine::new).collect(Collectors.toList());
            return itemsForPackage;
        }).map(l -> {
            TreeItem<PackageLine> toItem = new TreeItem<>(l.get(0));
            List<TreeItem<PackageLine>> children = l.subList(1, l.size()).stream().map(TreeItem::new).collect(toList());
            toItem.getChildren().setAll(children);
            return toItem;
        })
        .collect(Collectors.toList());
        // @formatter:on
    }

    private void clear() {
        this.map.clear();
        this.root.getChildren().clear();
    }

    private static class PackageLine {

        private final ModelPackageVariant modelPackageVariant;
        private final StringProperty packageName = new SimpleStringProperty();
        private final StringProperty variant = new SimpleStringProperty();

        private PackageLine(ModelPackageVariant variant) {
            this.modelPackageVariant = Objects.requireNonNull(variant, "modelPackageVariant must not be null");
            this.packageName.set(variant.modelPackage().name());
            this.variant.set(stringFor(variant.variant()));
        }

        private static String stringFor(Variant variant) {
            return variant.getClass().getSimpleName().toLowerCase() + ": " + variant.name();
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

    private static Comparator<ModelPackageVariant> newComparator() {
        return Comparator.<ModelPackageVariant, String> comparing(ti -> ti.modelPackage().name())
                .thenComparing(ti -> ti.variant(), variantComparator());
    }

    /*
     * TODO to be fixed!!!
     */
    private static Comparator<Variant> variantComparator() {
        return Comparator.<Variant, Class<?>> comparing(v -> v.getClass(), (c1, c2) -> {
            if (c1.isAssignableFrom(Release.class) && !c2.isAssignableFrom(Release.class)) {
                return -1;
            }
            if (c2.isAssignableFrom(Release.class) && !c1.isAssignableFrom(Release.class)) {
                return 1;
            }

            if (c1.isAssignableFrom(Tag.class) && !c2.isAssignableFrom(Tag.class)) {
                return -1;
            }
            if (c2.isAssignableFrom(Tag.class) && !c1.isAssignableFrom(Tag.class)) {
                return 1;
            }
            return 0;
        }).thenComparing(Comparator.comparing(Variant::name).reversed());
    }

}
