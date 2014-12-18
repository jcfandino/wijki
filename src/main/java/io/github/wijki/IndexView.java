package io.github.wijki;

import io.dropwizard.views.View;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class IndexView extends View {
    private static final Function<String, String> ENCODE_PATHS = new Function<String, String>() {
        public String apply(String path) {
            try {
                return URLEncoder.encode(path, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private final String path;
    private final List<String> subPaths;
    private final String theme;

    protected IndexView(final String thePath, List<String> theSubPaths, String aTheme) {
        super("index.mustache");
        path = thePath;
        theme = aTheme;
        subPaths = Lists.transform(theSubPaths, ENCODE_PATHS);
    }

    public String getPath() {
        return path;
    }

    public List<String> getSubPaths() {
        return subPaths;
    }

    public String getTheme() {
        return theme;
    }

}
