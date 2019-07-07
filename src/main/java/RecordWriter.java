import models.KeyMetadata;
import models.Record;

public interface RecordWriter {
    KeyMetadata appendRecord(Record record);

    long getFileLength();

    int getNumberOfRecordsWritten();

    void close();
}
