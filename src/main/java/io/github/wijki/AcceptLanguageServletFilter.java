package io.github.wijki;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Filter that overrides the Accept-Language header with "en-US,en".<br>
 * See: https://java.net/jira/browse/JERSEY-2464
 * @author jcfandino
 *
 */
public class AcceptLanguageServletFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpRequest) {
            public Enumeration<String> getHeaders(String name) {
                if ("Accept-Language".equals(name)) {
                    return new Vector<String>(asList("en-US,en")).elements();
                } else {
                    return super.getHeaders(name);
                }
            }
        };
        chain.doFilter(wrapper, response);
    }

    @Override
    public void destroy() {
    }
}
