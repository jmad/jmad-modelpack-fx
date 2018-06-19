package org.jmad.modelpack.gui.domain;

/**
 * Identifies the type of selection. {@link #ALL} means that the GUI will allow you to select the, e.g., optics and will initizalize the startup configuration.
 * {@link #MODEL_DEFINITION_ONLY} means that the GUI will only propose to select the model definition and there will be no startup configuration
 */
public enum JMadModelSelectionType {
    MODEL_DEFINITION_ONLY,
    ALL;
}
