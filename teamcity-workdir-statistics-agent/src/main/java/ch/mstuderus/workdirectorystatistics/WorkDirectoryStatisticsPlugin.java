package ch.mstuderus.workdirectorystatistics;

import ch.mstuderus.workdirectorystatistics.disksize.WorkDirectoryStatisticsFile;
import ch.mstuderus.workdirectorystatistics.disksize.WorkDirectoryStatisticsPath;
import com.google.gson.Gson;
import jetbrains.buildServer.ExtensionsProvider;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.duplicates.DuplicatesReporter;
import jetbrains.buildServer.agent.inspections.InspectionReporter;
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

public class WorkDirectoryStatisticsPlugin extends AgentLifeCycleAdapter {

    private ArtifactsWatcher artifactsWatcher;


    public WorkDirectoryStatisticsPlugin(@NotNull ExtensionsProvider extensionsProvider,
                                         @NotNull EventDispatcher<AgentLifeCycleListener> agentDispatcher,
                                         @NotNull InspectionReporter inspectionReporter,
                                         @NotNull DuplicatesReporter duplicatesReporter,
                                         @NotNull BuildAgentConfiguration configuration,
                                         @NotNull ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
        agentDispatcher.addListener(this);
    }

    @Override
    public void beforeBuildFinish(@NotNull AgentRunningBuild build, @NotNull BuildFinishedStatus buildStatus) {
        String workDirectory = build.getCheckoutDirectory().getAbsolutePath();
        build.getBuildLogger().activityStarted("Work Directory Statistics", "1111");
        build.getBuildLogger().message("Work Directory Statistics: Analyse " + workDirectory);

        long size = 0;
        long count = 0;

        Path buildDirectory = FileSystems.getDefault().getPath(workDirectory);

        List<WorkDirectoryStatisticsPath> paths = new ArrayList<>();

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
            /*
            Files.walk(start).forEach((file) -> {
                build.getBuildLogger().warning(file.toString());
            });

            */

            for(Object x : Files.walk(buildDirectory).toArray()){
                Path path = (Path)x;
                build.getBuildLogger().warning(path.toString());

                fileList.add(new WorkDirectoryStatisticsFile(path.toString(), Files.size(path)));

                WorkDirectoryStatisticsPath workDirectoryStatisticsPath = new WorkDirectoryStatisticsPath();
                workDirectoryStatisticsPath.name = path.getFileName().toString();
                workDirectoryStatisticsPath.path = path.toString();

                paths.add(workDirectoryStatisticsPath);

                count++;
                size += Files.size(path);
            }

        } catch (IOException e) {
            build.getBuildLogger().warning("Work Directory Statistics - Exception:");
            build.getBuildLogger().warning(e.getMessage());
        }

        try {
            File jsonFile = Paths.get(pluginBuildStorage.toString(),"files.json").toFile();
            Writer writer = new FileWriter(jsonFile.getPath());
            new Gson().toJson(fileList, writer);
            artifactsWatcher.addNewArtifactsPath(jsonFile.getPath() + " => .teamcity/work-directory-statistics/");
        } catch (IOException e) {
            build.getBuildLogger().warning("Work Directory Statistics - Exception:");
            build.getBuildLogger().warning(e.getMessage());
        }





        double size_mb = size / 1024.0 / 1024.0;
        build.getBuildLogger().message("Work Directory Statistics: Found " + count + " files, total " + size_mb + " MB");
        build.getBuildLogger().flush();

        build.getBuildLogger().activityFinished("Work Directory Statistics", "1111");
    }
}