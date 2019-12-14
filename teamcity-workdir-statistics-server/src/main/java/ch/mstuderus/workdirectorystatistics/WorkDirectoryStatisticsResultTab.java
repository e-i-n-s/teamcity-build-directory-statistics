package ch.mstuderus.workdirectorystatistics;

import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.*;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class WorkDirectoryStatisticsResultTab extends ViewLogTab implements CustomTab {

    private SBuildServer server;

    public WorkDirectoryStatisticsResultTab(
            @NotNull final PagePlaces pagePlaces,
            @NotNull final SBuildServer server,
            @NotNull PluginDescriptor descriptor) {
        super("", "", pagePlaces, server);
        setIncludeUrl(descriptor.getPluginResourcesPath("workDirectoryStatisticsResultTab.jsp"));
        this.server = server;
    }


    @Override
    public boolean isAvailable(@NotNull final HttpServletRequest request) {
        return super.isAvailable(request);
    }

    @NotNull
    @Override
    public String getTabTitle() {
        return "Work Directory Statistics";
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> map, @NotNull HttpServletRequest httpServletRequest, @NotNull SBuild sBuild) {
        map.put("myValue", "Test Value");
    }
}