/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.panes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import org.jmad.modelpack.domain.JMadModelPackageRepository;
import org.jmad.modelpack.gui.util.FxUtils;
import org.jmad.modelpack.service.JMadModelPackageRepositoryManager;
import org.jmad.modelpack.service.JMadModelPackageRepositoryManager.EnableState;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class JMadModelRepositorySelectionControl extends BorderPane {

    private final JMadModelPackageRepositoryManager manager;

    public JMadModelRepositorySelectionControl(JMadModelPackageRepositoryManager manager) {
        this.manager = requireNonNull(manager, "manager must not be null");

        ObservableList<RepoLine> repos = FXCollections.observableArrayList();
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

        repositoryView.getColumns().addAll(ImmutableList.of(enabledCol, baseUrlCol));

        FxUtils.setPercentageWidth(repositoryView, ImmutableMap.of(enabledCol, 0.1, baseUrlCol, 0.9));

        setCenter(repositoryView);

        // @formatter:off
        manager.state()
            .map(this::repoList)
            .publishOn(FxSchedulers.fxThread())
            .subscribe(repos::setAll);
        // @formatter:on

    }

    private List<RepoLine> repoList(Map<JMadModelPackageRepository, EnableState> m) {
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

        private RepoLine(JMadModelPackageRepository repo, boolean enabled) {
            stringRepresentation.set(repo.toString());
            this.enabled.set(enabled);
            this.enabled.addListener(FxUtils.onChange(n -> {
                if (n) {
                    manager.enable(repo);
                } else {
                    manager.disable(repo);
                }
            }));
        }

    }

}
