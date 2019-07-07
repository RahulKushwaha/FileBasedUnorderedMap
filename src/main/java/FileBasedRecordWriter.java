import Models.KeyMetadata;
import Models.Record;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class FileBasedRecordWriter implements RecordWriter {

  private RandomAccessFile randomAccessFile = null;
  private String fileUuid = null;
  private long offset = 0;
  private int numberOfRecords = 0;

  public FileBasedRecordWriter(RandomAccessFile randomAccessFile, String fileUuid) {
    this.randomAccessFile = randomAccessFile;
    this.fileUuid = fileUuid;
  }

  @Override
  public KeyMetadata appendRecord(Record record) {
    byte[] keyAsBytes = null;
    byte[] valueAsBytes = null;
    long recordStartingPosition = this.offset;

    try {
      keyAsBytes = record.getKey().getBytes("utf-8");
      valueAsBytes = record.getValue().getBytes("utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    }

    try {
      // Seek to the end of file.
      this.randomAccessFile.seek(this.offset);
      // Write length of key.
      this.randomAccessFile.writeLong(keyAsBytes.length);
      // Write Key Bytes
      this.randomAccessFile.write(keyAsBytes);
      // Write length of value.
      this.randomAccessFile.writeLong(valueAsBytes.length);
      // Write value bytes.
      this.randomAccessFile.write(valueAsBytes);

      // Total bytes = keyBytesLength(8 Bytes) + keyBytes + valueBytesLength(8 Bytes) + valueBytes
      this.offset += keyAsBytes.length + valueAsBytes.length + 16;
      this.numberOfRecords++;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    return new KeyMetadata(this.fileUuid, recordStartingPosition, keyAsBytes.length,
        valueAsBytes.length);
  }

  @Override
  public long getFileLength() {
    return this.offset;
  }

  @Override
  public int getNumberOfRecordsWritten() {
    return this.numberOfRecords;
  }

  @Override
  public void close() {
    try {
      this.randomAccessFile.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
