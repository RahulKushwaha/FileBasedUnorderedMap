import models.Record;

public interface RecordReader {
    long getRecord(long offset, Record record);

    long getFileLength();

    int getNumberOfRecordsRead();

    void close();
}
