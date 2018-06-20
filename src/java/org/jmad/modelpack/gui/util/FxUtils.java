package org.jmad.modelpack.gui.util;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class FxUtils {

    private static final AtomicBoolean FX_INITIALIZED = new AtomicBoolean(false);

    public static void ensureFxInitialized() {
        if (!FX_INITIALIZED.getAndSet(true)) {
            @SuppressWarnings("unused")
            JFXPanel jfxPanel = new JFXPanel();
            Platform.setImplicitExit(false);
        }
    }

    public static void setFontWeight(TitledPane definitionPane, FontWeight weight) {
        Font currentFont = definitionPane.getFont();
        Font boldFont = Font.font(currentFont.getFamily(), weight, currentFont.getSize());
        definitionPane.setFont(boldFont);
    }

    public static Region createVerticalFiller() {
        VBox verticalSpacer = new VBox();
        VBox.setVgrow(verticalSpacer, Priority.ALWAYS);
        return verticalSpacer;
    }

    public static void runSyncOnFxThread(Runnable task) {
        ensureFxInitialized();

        if(Platform.isFxApplicationThread()) {
            task.run();
            return;
        }

        CountDownLatch sync = new CountDownLatch(1);
        Platform.runLater(() -> {
            task.run();
            sync.countDown();
        });

        try {
            sync.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while waiting Fx thread to run task", e);
        }
    }

    private Region createHorizontalFiller() {
        HBox verticalSpacer = new HBox();
        HBox.setHgrow(verticalSpacer, Priority.ALWAYS);
        return verticalSpacer;
    }

    public static AnchorPane wrapAndGlueToAnchorPane(Node node) {
        glueToAnchorPane(node);
        return new AnchorPane(node);
    }

    public static void glueToAnchorPane(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
    }

    public static void setPercentageWidth(TreeTableView<?> table, Map<TreeTableColumn<?, ?>, Double> percentages) {
        if (!table.getColumns().containsAll(percentages.keySet())) {
            throw new IllegalArgumentException("The percentages map must include all the columns of the table");
        }

        if (percentages.values().stream().mapToDouble(d -> d).sum() != 1.0) {
            throw new IllegalArgumentException("The sum of the percentages MUST be 1.0");
        }

        int widthMarginToPreventHorizontalScrollbar = table.getColumns().size() + 1;
        table.widthProperty().addListener(onChange(width -> percentages.forEach((column, percentage) -> {
            double processedWidth = width.doubleValue() - widthMarginToPreventHorizontalScrollbar;
            double columnWidth = Math.floor(processedWidth * percentage);
            column.setPrefWidth(columnWidth);
        })));
    }

    public static void setPercentageWidth(TableView<?> table, Map<TableColumn<?, ?>, Double> percentages) {
        if (!table.getColumns().containsAll(percentages.keySet())) {
            throw new IllegalArgumentException("The percentages map must include all the columns of the table");
        }

        if (percentages.values().stream().mapToDouble(d -> d).sum() != 1.0) {
            throw new IllegalArgumentException("The sum of the percentages MUST be 1.0");
        }

        int widthMarginToPreventHorizontalScrollbar = table.getColumns().size() + 1;
        table.widthProperty().addListener(onChange(width -> percentages.forEach((column, percentage) -> {
            double processedWidth = width.doubleValue() - widthMarginToPreventHorizontalScrollbar;
            double columnWidth = Math.floor(processedWidth * percentage);
            column.setPrefWidth(columnWidth);
        })));
    }

    public static <T> ChangeListener<T> onChange(Consumer<T> consumer) {
        return (obs, ov, nv) -> consumer.accept(nv);
    }

    private FxUtils() {
        /* static things */
    }
}
