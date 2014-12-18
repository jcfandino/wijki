package io.github.wijki;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.dropwizard.views.ViewRenderer;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class WijkiApp extends Application<WijkiConf> {
    private static final Logger LOG = LoggerFactory.getLogger(WijkiApp.class);

    public static void main(String[] args) throws Exception {
        String[] completeArgs = args;
        if (args.length < 2) {
            String baseDir = System.getProperty("app.home", "src/main/resources");
            completeArgs = new String[] { "server", baseDir + "/conf/config.yaml" };
        }
        new WijkiApp().run(completeArgs);
    }

    @Override
    public void initialize(Bootstrap<WijkiConf> bootstrap) {
        Iterable<ViewRenderer> renderer =
                Lists.<ViewRenderer> newArrayList(new WijkiMustacheViewRenderer());
        bootstrap.addBundle(new ViewBundle(renderer));
    }

    @Override
    public void run(WijkiConf conf, Environment environment) throws Exception {
        String repoPath =
                String.format("%s/%s", System.getProperty("app.home", "."),
                        checkNotNull(conf.getRepo(), "Git repo not provided"));
        checkRepoPath(repoPath);
        Repository gitRepo = initGitRepo(repoPath);
        environment.jersey().register(new PageResource(repoPath, gitRepo, conf.getTheme()));
        environment.servlets()
                .addFilter("AcceptLanguageServletFilter", new AcceptLanguageServletFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }

    private void checkRepoPath(String repoPath) {
        File file = new File(repoPath);
        if (file.exists()) {
            LOG.info("Git repo already exists.");
            checkArgument(file.isDirectory(), "Repo path must be a directory");
        } else {
            LOG.info("Creating new git repo.");
            checkArgument(file.mkdirs(), "Failed to create repository");
        }
    }

    private Repository initGitRepo(String repoPath) {
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            Repository repository = builder.setGitDir(new File(repoPath, ".git")).build();
            if (!repository.getObjectDatabase().exists()) {
                LOG.info("Initializing new Git repo");
                repository.create();
            }
            return repository;
        } catch (IOException e) {
            LOG.error("Failed to open Git repo in " + repoPath, e);
            throw new RuntimeException(e);
        }
    }
}
