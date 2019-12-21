package ch.mstuderus.workdirectorystatistics;

import com.intellij.openapi.diagnostic.Logger;
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

import static ch.mstuderus.disksize.WorkDirectoryConstants.*;

public class WorkDirectoryStatisticsResultTab extends ViewLogTab implements CustomTab {
    private static final Logger LOG = Logger.getInstance(WorkDirectoryStatisticsResultTab.class.getName());
    private static final String BUILD_TAB = "workDirectoryStatisticsResultTab.jsp";

    public WorkDirectoryStatisticsResultTab(
            @NotNull final PagePlaces pagePlaces,
            @NotNull final SBuildServer server,
            @NotNull PluginDescriptor descriptor) {
        super(PLUGIN_TITLE, PLUGIN_CODE, pagePlaces, server);
        setIncludeUrl(descriptor.getPluginResourcesPath(BUILD_TAB));
    }

    private BuildArtifact getJsonFile(SBuild sBuild) {
        if (!isJsonFileAvailable(sBuild)) {
            throw new RuntimeException("Json file not available");
        }
        return sBuild.getArtifacts(BuildArtifactsViewMode.VIEW_ALL)
                .findArtifact(JSON_FILES)
                .getArtifact();
    }

    private boolean isJsonFileAvailable(SBuild sBuild) {
        return sBuild.getArtifacts(BuildArtifactsViewMode.VIEW_ALL)
                .findArtifact(JSON_FILES)
                .isAccessible();
    }

    @Override
    public boolean isAvailable(@NotNull final HttpServletRequest request) {
        final SBuild build = BuildDataExtensionUtil.retrieveBuild(request, myServer);
        return build.getBuildFeaturesOfType(PLUGIN_CODE).size() > 0
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
            } catch (IOException error) {
                LOG.error(error);
            }
        }
    }
}