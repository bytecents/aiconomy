package com.se.aiconomy.client.common;

import one.jpro.platform.mdfx.MarkdownView;

import java.net.URL;
import java.util.List;
import java.util.Objects;

public class MyMarkdownView extends MarkdownView {
    @Override
    protected List<String> getDefaultStylesheets() {
//        URL url = getClass().getResource("/style/markdown.css");
//        System.out.println("Resolved URL: " + url); // 如果是 null 就路径错了
        return List.of(Objects.requireNonNull(getClass().getResource("/css/markdown.css")).toExternalForm());
    }
}
