package ch.mstuderus.workdirectorystatistics;

import ch.mstuderus.disksize.WorkDirectoryStatisticsFile;
import com.google.gson.Gson;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildFinishedStatus;
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

public class WorkDirectoryStatisticsPlugin extends AgentLifeCycleAdapter {

    private ArtifactsWatcher artifactsWatcher;


    public WorkDirectoryStatisticsPlugin(@NotNull EventDispatcher<AgentLifeCycleListener> agentDispatcher,
                                         @NotNull ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
        agentDispatcher.addListener(this);
    }

    @Override
    public void beforeBuildFinish(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
        String workDirectory = build.getCheckoutDirectory().getAbsolutePath();
        build.getBuildLogger().activityStarted("Work Directory Statistics", "statistics");
        build.getBuildLogger().message("Work Directory Statistics: Analyse " + workDirectory);

        long size = 0;
        long count = 0;

        Path buildDirectory = FileSystems.getDefault().getPath(workDirectory);
        Path pluginBuildStorage = Paths.get(build.getAgentTempDirectory().getPath(), "work-directory-statistics");

        try {
            Files.deleteIfExists(pluginBuildStorage);
            Files.createDirectory(pluginBuildStorage);
        } catch (IOException e) {
            build.getBuildLogger().warning("Work Directory Statistics - Exception:");
            build.getBuildLogger().warning(e.getMessage());
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
            build.getBuildLogger().warning("Work Directory Statistics - Exception:");
            build.getBuildLogger().warning(e.getMessage());
        }

        try {
            File jsonFile = Paths.get(pluginBuildStorage.toString(), "files.json").toFile();
            Writer writer = new FileWriter(jsonFile.getPath());
            new Gson().toJson(fileList, writer);
            writer.close();
            artifactsWatcher.addNewArtifactsPath(jsonFile.getPath() + " => .teamcity/work-directory-statistics/");
        } catch (IOException e) {
            build.getBuildLogger().warning("Work Directory Statistics - Exception:");
            build.getBuildLogger().warning(e.getMessage());
        }

        double size_mb = size / 1024.0 / 1024.0;
        build.getBuildLogger().message("Work Directory Statistics: Found " + count + " files, total " + size_mb + " MB");
        build.getBuildLogger().flush();
        build.getBuildLogger().activityFinished("Work Directory Statistics", "statistics");
    }
}