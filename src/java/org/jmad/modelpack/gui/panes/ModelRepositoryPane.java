/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jmad.modelpack.domain.ModelPackageRepository;
import org.jmad.modelpack.service.ModelPackageRepositoryManager;
import org.jmad.modelpack.service.ModelPackageRepositoryManager.EnableState;

import com.google.common.collect.ImmutableList;

import freetimelabs.io.reactorfx.schedulers.FxSchedulers;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;

public class ModelRepositoryPane extends BorderPane {

    private final ModelPackageRepositoryManager manager;
    private final ObservableList<RepoLine> repos = FXCollections.observableArrayList();

    public ModelRepositoryPane(ModelPackageRepositoryManager manager) {
        this.manager = requireNonNull(manager, "manager must not be null");

        TableView<RepoLine> repositoryView = new TableView<>(repos);
        repositoryView.setEditable(true);

        TableColumn<RepoLine, Boolean> enabledCol = new TableColumn<>("Enabled");
        enabledCol.setCellFactory(CheckBoxTableCell.forTableColumn(enabledCol));
        enabledCol.setCellValueFactory(r -> r.getValue().enabled);
        enabledCol.setEditable(true);

        TableColumn<RepoLine, String> baseUrlCol = new TableColumn<>("Repository");
        baseUrlCol.setCellValueFactory(r -> r.getValue().stringRepresentation);
        baseUrlCol.setEditable(false);
        baseUrlCol.setResizable(true);

        baseUrlCol.prefWidthProperty().bind(repositoryView.widthProperty().subtract(enabledCol.widthProperty()));

        repositoryView.getColumns().addAll(ImmutableList.of(enabledCol, baseUrlCol));

        setCenter(repositoryView);

        // @formatter:off
        manager.state()
            .map(this::repoList)
            .publishOn(FxSchedulers.fxThread())
            .subscribe(repos::setAll);
        // @formatter:on

    }

    private List<RepoLine> repoList(Map<ModelPackageRepository, EnableState> m) {
        // @formatter:off
        return m.entrySet().stream()
                .map(e -> new RepoLine(e.getKey(), e.getValue().asBoolEnabled()))
                .sorted(Comparator.comparing(l -> l.stringRepresentation.getValue()))
                .collect(toList());
        // @formatter:on
    }

    private class RepoLine {

        private final BooleanProperty enabled = new SimpleBooleanProperty();
        private final StringProperty stringRepresentation = new SimpleStringProperty();

        private RepoLine(ModelPackageRepository repo, boolean enabled) {
            stringRepresentation.set(repo.toString());
            this.enabled.set(enabled);
            this.enabled.addListener((obs, o, n) -> {
                if (n) {
                    manager.enable(repo);
                } else {
                    manager.disable(repo);
                }
            });
        }

    }

}
