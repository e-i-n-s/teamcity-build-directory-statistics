package ch.mstuderus.workdirectorystatistics;

import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.mstuderus.disksize.WorkDirectoryConstants.PLUGIN_CODE;
import static ch.mstuderus.disksize.WorkDirectoryConstants.PLUGIN_NAME_LONG;

public class WorkDirectoryStatisticsBuildFeature extends BuildFeature {
    public static final String FEATURE_SETTINGS = "workDirectoryStatisticsSettings.jsp";
    private final String featureSettingsUrl;

    public WorkDirectoryStatisticsBuildFeature(@NotNull final PluginDescriptor descriptor) {
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
        return properties -> {
            final List<InvalidProperty> invalids = new ArrayList<>();
            return invalids;
        };
    }
}