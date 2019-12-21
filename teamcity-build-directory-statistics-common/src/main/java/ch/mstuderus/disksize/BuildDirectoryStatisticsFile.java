package ch.mstuderus.disksize;

public class BuildDirectoryStatisticsFile {

    private String path;
    private long bytes;

    public BuildDirectoryStatisticsFile(String path, long bytes) {
        this.path = path;
        this.bytes = bytes;
    }
}
