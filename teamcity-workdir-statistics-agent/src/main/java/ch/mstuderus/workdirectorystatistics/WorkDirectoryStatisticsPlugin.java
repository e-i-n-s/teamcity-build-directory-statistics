package ch.mstuderus.workdirectorystatistics;

import ch.mstuderus.disksize.WorkDirectoryStatisticsFile;
import com.google.gson.Gson;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.mstuderus.disksize.WorkDirectoryConstants.*;

public class WorkDirectoryStatisticsPlugin extends AgentLifeCycleAdapter {

    private ArtifactsWatcher artifactsWatcher;


    public WorkDirectoryStatisticsPlugin(@NotNull EventDispatcher<AgentLifeCycleListener> agentDispatcher,
                                         @NotNull ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
        agentDispatcher.addListener(this);
    }

    private void logException(Exception exception, BuildProgressLogger logger) {
        logger.warning("Exception:");
        logger.warning(exception.getMessage());
    }

    @Override
    public void beforeBuildFinish(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
        String workDirectory = build.getCheckoutDirectory().getAbsolutePath();
        build.getBuildLogger().activityStarted(PLUGIN_NAME_LONG, PLUGIN_CODE);

        long size = 0;
        long count = 0;

        Path buildDirectory = FileSystems.getDefault().getPath(workDirectory);
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

        List<WorkDirectoryStatisticsFile> fileList = new ArrayList<>();

        try {
            for (Path path : Files.walk(buildDirectory).collect(Collectors.toCollection(ArrayList::new))) {
                if (!Files.isDirectory(path)) {
                    String relativePath = buildDirectory.toUri().relativize(path.toUri()).getPath();
                    fileList.add(new WorkDirectoryStatisticsFile(relativePath, Files.size(path)));

                    count++;
                    size += Files.size(path);
                }
            }
        } catch (IOException e) {
            logException(e, build.getBuildLogger());
        }

        try {
            File jsonFile = Paths.get(pluginBuildStorage.toString(), JSON_FILES_FILE_NAME).toFile();
            Writer writer = new FileWriter(jsonFile.getPath());
            new Gson().toJson(fileList, writer);
            writer.close();
            artifactsWatcher.addNewArtifactsPath(jsonFile.getPath() + " => " + JSON_FILES_DIRECTORY);
        } catch (IOException e) {
            logException(e, build.getBuildLogger());
        }

        build.getBuildLogger().message(String.format("Found %d file, total %.2f MB", count, size / 1024.0 / 1024.0));
        build.getBuildLogger().flush();
        build.getBuildLogger().activityFinished(PLUGIN_NAME_LONG, PLUGIN_CODE);
    }
}