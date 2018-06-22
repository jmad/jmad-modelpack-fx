package org.jmad.modelpack.gui.panes;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import static org.jmad.modelpack.gui.util.FxUtils.glueToAnchorPane;
import static org.jmad.modelpack.gui.util.FxUtils.onChange;
import static org.jmad.modelpack.gui.util.FxUtils.setFontWeight;

/**
 * Pane that represents a section of the GUI. It has a title and it can have a loading state that can be controlled
 * by {@link #loadingOverlayProperty()}
 */
public class SectionPane extends AnchorPane {

    private static final double LOADING_GLYPH_SIZE = 24;
    private static final int REFRESH_ANIMATION_MS = 600;

    private final Region loadingPane;
    private final Animation loadingAnimation;
    private final TitledPane titledContentPane;
    private final BooleanProperty loadingOverlayProperty = new SimpleBooleanProperty(false);

    public SectionPane() {
        this("");
    }

    public SectionPane(String title) {
        titledContentPane = new TitledPane();
        titledContentPane.setText(title);
        titledContentPane.setCollapsible(false);
        setFontWeight(titledContentPane, FontWeight.BOLD);

        loadingPane = createLoadingPane();
        loadingAnimation = createRotateAnimation(loadingPane);

        glueToAnchorPane(titledContentPane);
        glueToAnchorPane(loadingPane);
        getChildren().addAll(titledContentPane, loadingPane);

        loadingOverlayProperty.addListener(onChange(enabled -> {
            if(enabled) showLoading();
            else hideLoading();
        }));
        hideLoading();
    }

    public void setTitle(String title) {
        titledContentPane.setText(title);
    }

    public void setContent(Node content) {
        titledContentPane.setContent(content);
    }

    public void hideLoading() {
        loadingAnimation.stop();
        loadingPane.setVisible(false);
        titledContentPane.setDisable(false);
    }

    public void showLoading() {
        titledContentPane.setDisable(true);
        loadingPane.setVisible(true);
        loadingAnimation.playFromStart();
    }

    public BooleanProperty loadingOverlayProperty() {
        return loadingOverlayProperty;
    }

    public StringProperty textProperty() {
        return titledContentPane.textProperty();
    }

    private static Region createLoadingPane() {
        Glyph loadingPane = GlyphFontRegistry.font("FontAwesome").create(FontAwesome.Glyph.SPINNER);
        loadingPane.setCacheHint(CacheHint.ROTATE);
        loadingPane.setFontSize(LOADING_GLYPH_SIZE);
        loadingPane.setColor(Color.GRAY);
        return new StackPane(loadingPane);
    }

    private static Animation createRotateAnimation(Node graphics) {
        RotateTransition transition = new RotateTransition(javafx.util.Duration.millis(REFRESH_ANIMATION_MS), graphics);
        transition.setFromAngle(0);
        transition.setToAngle(360);
        transition.setCycleCount(Timeline.INDEFINITE);
        transition.setInterpolator(Interpolator.LINEAR);
        return transition;
    }
}
