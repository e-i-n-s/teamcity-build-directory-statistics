package ch.mstuderus.disksize;

public class BuildDirectoryStatisticsConstants {
    public static final String PLUGIN_NAME_LONG = "Build Directory Statistics";
    public static final String PLUGIN_TITLE = "Build Directory";
    public static final String PLUGIN_CODE = "build-directory-statistics";
    public static final String JSON_FILES_DIRECTORY = ".teamcity/" + PLUGIN_CODE + "/";
    public static final String JSON_FILES_FILE_NAME = "files.json.gzip";
    public static final String JSON_FILES = JSON_FILES_DIRECTORY + JSON_FILES_FILE_NAME;

    private BuildDirectoryStatisticsConstants() {
    }
}
