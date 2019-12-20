package ch.mstuderus.workdirectorystatistics;

import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkDirectoryStatisticsBuildFeature extends BuildFeature {
    public static final String FEATURE_TYPE = "work-directory-statistics-plugin";
    public static final String FEATURE_NAME = "Work Directory Statistics";
    private final String myEditUrl;

    public WorkDirectoryStatisticsBuildFeature(@NotNull final PluginDescriptor descriptor) {
        myEditUrl = descriptor.getPluginResourcesPath("workDirectoryStatisticsSettings.jsp");
    }

    @NotNull
    @Override
    public String getType() {
        return FEATURE_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return FEATURE_NAME;
    }

    @Override
    public String getEditParametersUrl() {
        return myEditUrl;
    }

    @Override
    public boolean isMultipleFeaturesPerBuildTypeAllowed() {
        return true;
    }

    @Override
    public boolean isRequiresAgent() {
        return true;
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull final Map<String, String> params) {
        return ""; // No settings yet!
    }

    @Override
    public PropertiesProcessor getParametersProcessor() {
        return properties -> {
            final List<InvalidProperty> invalids = new ArrayList<>();
            return invalids;
        };
    }
}