package ch.mstuderus.workdirectorystatistics;

import jetbrains.buildServer.controllers.BuildDataExtensionUtil;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifact;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode;
import jetbrains.buildServer.web.openapi.CustomTab;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.ViewLogTab;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

public class WorkDirectoryStatisticsResultTab extends ViewLogTab implements CustomTab {
    public static final String JSON = ".teamcity/work-directory-statistics/files.json";

    public WorkDirectoryStatisticsResultTab(
            @NotNull final PagePlaces pagePlaces,
            @NotNull final SBuildServer server,
            @NotNull PluginDescriptor descriptor) {
        super("Work Directory Statistics", "", pagePlaces, server);
        setIncludeUrl(descriptor.getPluginResourcesPath("workDirectoryStatisticsResultTab.jsp"));
    }

    private BuildArtifact getJsonFile(SBuild sBuild) {
        if (!isJsonFileAvailable(sBuild)) {
            throw new RuntimeException("Json file not available");
        }
        return sBuild.getArtifacts(BuildArtifactsViewMode.VIEW_ALL).findArtifact(JSON).getArtifact();
    }

    private boolean isJsonFileAvailable(SBuild sBuild) {
        return sBuild.getArtifacts(BuildArtifactsViewMode.VIEW_ALL).findArtifact(JSON).isAvailable();
    }

    @Override
    public boolean isAvailable(@NotNull final HttpServletRequest request) {
        final SBuild build = BuildDataExtensionUtil.retrieveBuild(request, myServer);
        return build.getBuildFeaturesOfType(WorkDirectoryStatisticsBuildFeature.FEATURE_TYPE).size() > 0
                && isJsonFileAvailable(build)
                && super.isAvailable(request);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> map, @NotNull HttpServletRequest httpServletRequest,
                             @NotNull SBuild sBuild) {
        if (isJsonFileAvailable(sBuild)) {
            try {
                map.put("data",
                        StringEscapeUtils.escapeEcmaScript(IOUtils.toString(getJsonFile(sBuild).getInputStream())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}