package ch.mstuderus.builddirectorystatistics;

import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import static ch.mstuderus.disksize.BuildDirectoryStatisticsConstants.PLUGIN_CODE;
import static ch.mstuderus.disksize.BuildDirectoryStatisticsConstants.PLUGIN_NAME_LONG;

public class BuildDirectoryStatisticsBuildFeature extends BuildFeature {
    public static final String FEATURE_SETTINGS = "buildDirectoryStatisticsSettings.jsp";
    private final String featureSettingsUrl;

    public BuildDirectoryStatisticsBuildFeature(@NotNull final PluginDescriptor descriptor) {
        featureSettingsUrl = descriptor.getPluginResourcesPath(FEATURE_SETTINGS);
    }

    @NotNull
    @Override
    public String getType() {
        return PLUGIN_CODE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return PLUGIN_NAME_LONG;
    }

    @Override
    public String getEditParametersUrl() {
        return featureSettingsUrl;
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
        return properties -> new ArrayList<>();
    }
}