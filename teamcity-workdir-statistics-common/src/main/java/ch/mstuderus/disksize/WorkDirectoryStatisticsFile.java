package ch.mstuderus.disksize;

public class WorkDirectoryStatisticsFile {

    private String path;
    private long bytes;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }


    public WorkDirectoryStatisticsFile(String path, long bytes) {
        this.path = path;
        this.bytes = bytes;
    }
}
