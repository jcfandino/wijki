package io.github.wijki;

import io.dropwizard.views.View;

public class PageView extends View {
    private final Page page;
    private final String path;
    private final String theme
    ;

    protected PageView(String thePath, Page thePage,String aTheme) {
        this("page.mustache", thePath, thePage,aTheme);
    }

    protected PageView(String template, String thePath, Page thePage, String aTheme) {
        super(template);
        path = thePath;
        page = thePage;
        theme =aTheme;
    }

    public Page getPage() {
        return page;
    }

    public String getPath() {
        return path;
    }

    public String getTheme() {
        return theme;
    }

}
