import Models.KeyMetadata;
import Models.Record;

public interface RecordWriter {
    KeyMetadata appendRecord(Record record);

    long getFileLength();

    int getNumberOfRecordsWritten();

    void close();
}
