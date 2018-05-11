package org.jmad.modelpack.gui.panes;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

public final class FxUtils {

    public static void setFontWeight(TitledPane definitionPane, FontWeight weight) {
        Font currentFont = definitionPane.getFont();
        Font boldFont = Font.font(currentFont.getFamily(), weight, currentFont.getSize());
        definitionPane.setFont(boldFont);
    }

    public static <T> ChangeListener<T> onChange(Consumer<T> consumer) {
        return (obs, ov, nv) -> consumer.accept(nv);
    }

    private FxUtils() {
        /* static things*/
    }
}
