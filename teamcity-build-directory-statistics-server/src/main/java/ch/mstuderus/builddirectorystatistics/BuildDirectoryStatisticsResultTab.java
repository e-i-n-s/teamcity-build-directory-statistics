package ch.mstuderus.builddirectorystatistics;

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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static ch.mstuderus.disksize.BuildDirectoryStatisticsConstants.*;

public class BuildDirectoryStatisticsResultTab extends ViewLogTab implements CustomTab {
    private static final Logger LOG = Logger.getInstance(BuildDirectoryStatisticsResultTab.class.getName());
    private static final String BUILD_TAB = "buildDirectoryStatisticsResultTab.jsp";

    public BuildDirectoryStatisticsResultTab(
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
        return build != null
                && !build.getBuildFeaturesOfType(PLUGIN_CODE).isEmpty()
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

                GZIPInputStream gzipInputStream = new GZIPInputStream(getJsonFile(sBuild).getInputStream());
                Reader reader = new BufferedReader(new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8));
                map.put("data", StringEscapeUtils.escapeEcmaScript(IOUtils.toString(reader)));
            } catch (IOException error) {
                LOG.error(error);
            }
        }
    }
}