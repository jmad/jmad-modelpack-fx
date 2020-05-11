/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.gui.conf;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.jmad.modelpack.gui.dialogs.JMadModelSelectionDialog;
import org.jmad.modelpack.gui.domain.JMadModelSelection;
import org.jmad.modelpack.gui.domain.JMadModelSelectionType;
import org.jmad.modelpack.gui.util.FxUtils;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component("jmadModelSelectionDialogFactory")
@Lazy
public class JMadModelSelectionDialogFactory {

    private static final int MODELPACK_DIALOG_WIDTH = 1000;
    private static final int MODELPACK_DIALOG_HEIGHT = 700;

    public Optional<JMadModelSelection> showAndWaitModelSelection() {
        return showAndWaitModelSelection(JMadModelSelectionType.ALL);
    }

    public Optional<JMadModelSelection> showAndWaitModelSelection(JMadModelSelectionType selectionType) {
        AtomicReference<Optional<JMadModelSelection>> userSelection = new AtomicReference<>();

        FxUtils.runSyncOnFxThread(() -> {
            JMadModelSelectionDialog selectionDialog = jmadModelSelectionDialog();
            selectionDialog.setModelSelectionType(selectionType);
            selectionDialog.setWidth(MODELPACK_DIALOG_WIDTH);
            selectionDialog.setHeight(MODELPACK_DIALOG_HEIGHT);
            userSelection.set(selectionDialog.showAndWait());
        });

        return userSelection.get();
    }

    @Lookup
    protected JMadModelSelectionDialog jmadModelSelectionDialog() {
        return null;
    }

}
