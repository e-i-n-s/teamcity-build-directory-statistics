package ch.mstuderus.builddirectorystatistics;

import ch.mstuderus.disksize.BuildDirectoryStatisticsFile;
import com.google.gson.Gson;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.util.EventDispatcher;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import static ch.mstuderus.disksize.BuildDirectoryStatisticsConstants.*;

public class BuildDirectoryStatisticsPlugin extends AgentLifeCycleAdapter {

    private ArtifactsWatcher artifactsWatcher;


    public BuildDirectoryStatisticsPlugin(@NotNull EventDispatcher<AgentLifeCycleListener> agentDispatcher,
                                          @NotNull ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
        agentDispatcher.addListener(this);
    }

    private void logException(Exception exception, BuildProgressLogger logger) {
        logger.warning("Exception:");
        logger.warning(exception.getMessage());
        logger.message(ExceptionUtils.getStackTrace(exception));
    }

    @Override
    public void beforeBuildFinish(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
        Collection<AgentBuildFeature> features = build.getBuildFeaturesOfType(PLUGIN_CODE);
        if (features.isEmpty()) {
            return;
        }

        build.getBuildLogger().activityStarted(PLUGIN_NAME_LONG, PLUGIN_CODE);

        Path buildDirectory = FileSystems.getDefault().getPath(build.getCheckoutDirectory().getAbsolutePath());
        Path pluginBuildStorage = Paths.get(
                build.getAgentTempDirectory().getPath(),
                "work-directory-statistics"
        );

        try {
            Files.deleteIfExists(pluginBuildStorage);
            Files.createDirectory(pluginBuildStorage);
        } catch (IOException e) {
            logException(e, build.getBuildLogger());
        }

        List<BuildDirectoryStatisticsFile> fileList = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(buildDirectory)) {
            for (Path path : stream.collect(Collectors.toCollection(ArrayList::new))) {
                try {
                    if (path.toFile().isFile() && !Files.isSymbolicLink(path)) {
                        String relativePath = buildDirectory.toUri().relativize(path.toUri()).getPath();
                        fileList.add(new BuildDirectoryStatisticsFile(relativePath, Files.size(path)));
                    }
                } catch (Exception e) {
                    logException(e, build.getBuildLogger());
                }
            }
        } catch (IOException e) {
            logException(e, build.getBuildLogger());
        }

        File jsonFile = Paths.get(pluginBuildStorage.toString(), JSON_FILES_FILE_NAME).toFile();

        Writer writer = null;
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(jsonFile))) {
            writer = new BufferedWriter(new OutputStreamWriter(gzipOutputStream, StandardCharsets.UTF_8));
            new Gson().toJson(fileList, writer);
            writer.close();
        } catch (IOException e) {
            logException(e, build.getBuildLogger());
        } finally {
            IOUtils.closeQuietly(writer);
        }

        artifactsWatcher.addNewArtifactsPath(jsonFile.getPath() + " => " + JSON_FILES_DIRECTORY);

        long size = 0;
        for (BuildDirectoryStatisticsFile file : fileList) {
            size += file.getBytes();
        }

        build.getBuildLogger().message(
                String.format("Found %d file, total %.2f MB", fileList.size(), size / 1024.0 / 1024.0)
        );
        build.getBuildLogger().flush();
        build.getBuildLogger().activityFinished(PLUGIN_NAME_LONG, PLUGIN_CODE);
    }
}
