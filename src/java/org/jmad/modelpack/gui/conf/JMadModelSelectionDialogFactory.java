/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.conf;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.jmad.modelpack.gui.domain.JMadModelSelection;
import org.jmad.modelpack.gui.util.FxUtils;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.control.Dialog;

@Component("jmadModelSelectionDialogFactory")
public class JMadModelSelectionDialogFactory {

    public Optional<JMadModelSelection> showAndWaitModelSelection() {
        Dialog<JMadModelSelection> selectionDialog = selectionDialog();
        selectionDialog.setWidth(1000);
        selectionDialog.setHeight(700);
        return selectionDialog.showAndWait();
    }

    public Dialog<JMadModelSelection> selectionDialog() {
        FxUtils.ensureFxInitialized();

        AtomicReference<Dialog<JMadModelSelection>> dialog = new AtomicReference<>();

        if (Platform.isFxApplicationThread()) {
            return jmadModelSelectionDialog();
        } else {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                dialog.set(jmadModelSelectionDialog());
                latch.countDown();
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException("waiting for latch was interrupted.");
            }
            return dialog.get();
        }
    }

    @Lookup
    protected Dialog<JMadModelSelection> jmadModelSelectionDialog() {
        return null;
    }

}