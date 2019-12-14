package ch.mstuderus.workdirectorystatistics.disksize;

public class WorkDirectoryStatisticsFile {
    public String path;
    public long bytes;

    public WorkDirectoryStatisticsFile(String path, long bytes) {
        this.path = path;
        this.bytes = bytes;
    }
}
