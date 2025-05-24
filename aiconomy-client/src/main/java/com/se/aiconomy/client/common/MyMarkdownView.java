package com.se.aiconomy.client.common;

import one.jpro.platform.mdfx.MarkdownView;

import java.net.URL;
import java.util.List;
import java.util.Objects;

public class MyMarkdownView extends MarkdownView {
    /**
     * Returns the default stylesheets for the Markdown view.
     * <p>
     * This method overrides the default implementation to provide a custom CSS
     * stylesheet for rendering markdown content. The stylesheet is loaded from
     * the resource path <code>/css/markdown.css</code>.
     * </p>
     *
     * @return a list containing the URL of the default markdown stylesheet
     * @throws NullPointerException if the stylesheet resource cannot be found
     */
    @Override
    protected List<String> getDefaultStylesheets() {
        return List.of(Objects.requireNonNull(getClass().getResource("/css/markdown.css")).toExternalForm());
    }
}