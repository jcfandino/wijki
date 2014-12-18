package io.github.wijki;

import static java.util.Arrays.asList;
import io.dropwizard.views.View;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class PageResource {
    private static final Logger LOG = LoggerFactory.getLogger(PageResource.class);

    private String repoPath;
    private Repository gitRepo;
    private String theme;

    public PageResource(String aRepoPath, Repository aGitRepo, String aTheme) {
        repoPath = aRepoPath;
        gitRepo = aGitRepo;
        theme = aTheme;
    }

    @GET
    public View view() {
        return view("", null);
    }

    @GET
    @Path("/{path:.+}")
    public View view(@PathParam("path") String path, @QueryParam("edit") String edit) {
        try {
            File file = new File(repoPath + "/" + path);
            if (file.exists() && file.isDirectory()) {
                return indexView(path, file);
            }
            return pageView(path, file, edit);
        } catch (IOException e) {
            LOG.error("Failed to retrieve page " + path, e);
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/{path:.+}")
    public void save(@PathParam("path") String path, Page page) {
        try {
            Boolean isNew = writeFile(path, page);
            commit(path, isNew);
        } catch (IOException e) {
            LOG.error("Failed to save page " + path, e);
            throw new RuntimeException(e);
        }
    }

    private View pageView(String path, File file, String edit) throws IOException {
        String content = getContent(file);
        Page page = new Page(content);
        if (edit != null) {
            return new EditView(path, page, theme);
        }
        return new PageView(path, page, theme);
    }

    private String getContent(File file) throws IOException {
        if (file.exists()) {
            List<String> lines = Files.readLines(file, Charsets.UTF_8);
            return Joiner.on("\n").join(lines);
        }
        return "Empty page";
    }

    private View indexView(String path, File file) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !".git".equals(name);
            }
        };
        List<String> subs = asList(file.list(filter));
        return new IndexView(path, subs, theme);
    }

    private void commit(String path, Boolean isNew) {
        try {
            String message = String.format("%s page %s", isNew ? "Created" : "Modified", path);
            new Git(gitRepo).add().addFilepattern(path).call();
            new Git(gitRepo).commit().setMessage(message).setAuthor("Wijki wiki", "").call();
        } catch (GitAPIException e) {
            LOG.error("Failed to commit page " + path, e);
            throw new RuntimeException(e);
        }
    }

    private Boolean writeFile(String path, Page page) throws IOException {
        File file = new File(repoPath + "/" + path);
        Boolean isNew = !file.exists();
        Files.createParentDirs(file);
        Files.write(page.getContent(), file, Charsets.UTF_8);
        return isNew;
    }
}
