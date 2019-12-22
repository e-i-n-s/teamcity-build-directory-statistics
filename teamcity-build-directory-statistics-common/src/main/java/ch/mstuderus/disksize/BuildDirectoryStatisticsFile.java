package ch.mstuderus.disksize;

public class BuildDirectoryStatisticsFile {
    private String path;
    private long bytes;

    public String getPath() {
        return path;
    }

    public long getBytes() {
        return bytes;
    }

    public BuildDirectoryStatisticsFile(String path, long bytes) {
        this.path = path;
        this.bytes = bytes;
    }
}
