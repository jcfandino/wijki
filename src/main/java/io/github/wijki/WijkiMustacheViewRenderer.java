package io.github.wijki;

import io.dropwizard.views.View;
import io.dropwizard.views.mustache.MustacheViewRenderer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheException;
import com.google.common.base.Charsets;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * An extension of the {@link MustacheViewRenderer} that does not escape Html content.
 * @author jcfandino
 */
public class WijkiMustacheViewRenderer extends MustacheViewRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(WijkiMustacheViewRenderer.class);

    @Override
    public void render(View view, Locale locale, OutputStream output) throws IOException,
            WebApplicationException {
        try {
            final Mustache template =
                    new UnescapedHtmlMustacheFactory(view.getClass()).compile(view
                            .getTemplateName());
            final Charset charset = view.getCharset().or(Charsets.UTF_8);
            try (OutputStreamWriter writer = new OutputStreamWriter(output, charset)) {
                template.execute(writer, view);
            }
        } catch (UncheckedExecutionException | MustacheException ignored) {
            throw new FileNotFoundException("Template " + view.getTemplateName() + " not found.");
        }
    }

    protected class UnescapedHtmlMustacheFactory extends DefaultMustacheFactory {
        private final Class<? extends View> klass;

        protected UnescapedHtmlMustacheFactory(Class<? extends View> aklass) {
            klass = aklass;
        }

        @Override
        public Reader getReader(String resourceName) {
            final InputStream is = klass.getResourceAsStream(resourceName);
            if (is == null) {
                throw new MustacheException("Template " + resourceName + " not found");
            }
            return new BufferedReader(new InputStreamReader(is, Charsets.UTF_8));
        }

        @Override
        public void encode(String value, Writer writer) {
            try {
                writer.write(value);
            } catch (IOException e) {
                LOG.error("Failed to write value", e);
                throw new RuntimeException(e);
            }
        }
    }
}
