package io.github.wijki;

import io.dropwizard.Configuration;

public class WijkiConf extends Configuration {
    private String repo;
    private String theme;

    public String getRepo() {
        return repo;
    }

    public void setRepo(String aRepo) {
        repo = aRepo;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String aTheme) {
        theme = aTheme;
    }



}
