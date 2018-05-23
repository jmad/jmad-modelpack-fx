/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.dialogs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.jmad.modelpack.gui.domain.JMadModelSelection;
import org.springframework.beans.factory.annotation.Lookup;

import javafx.application.Platform;
import javafx.scene.control.Dialog;

public class JMadDialogFactory {

    public Dialog<JMadModelSelection> selectionDialog() {
        AtomicReference<Dialog<JMadModelSelection>> dialog = new AtomicReference<>();
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

    @Lookup
    protected Dialog<JMadModelSelection> jmadModelSelectionDialog() {
        return null;
    }

}
