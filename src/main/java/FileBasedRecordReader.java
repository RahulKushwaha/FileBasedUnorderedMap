import java.io.IOException;
import java.io.RandomAccessFile;
import models.Record;

public class FileBasedRecordReader implements RecordReader {

  private RandomAccessFile randomAccessFile = null;
  private int numberOfRecordsRead = 0;
  // This means the key length is limited to 1KB or 1000 bytes.
  private byte[] keyReadBuffer = new byte[1000];
  private byte[] valueReadBuffer = new byte[4 * 1000];

  public FileBasedRecordReader(RandomAccessFile randomAccessFile) {
    this.randomAccessFile = randomAccessFile;
  }

  @Override
  public long getRecord(long offset, Record record) {
    try {
      long currentOffset = offset;
      this.randomAccessFile.seek(currentOffset);
      long keyLength = this.randomAccessFile.readLong();
      // Skip 8 bytes due to length of long.
      currentOffset += 8;
      this.randomAccessFile.seek(currentOffset);
      //Read key.
      this.randomAccessFile.readFully(keyReadBuffer, 0, (int) keyLength);

      String key = new String(keyReadBuffer, 0, (int) keyLength, "utf-8");
      record.setKey(key);

      currentOffset += keyLength;
      this.randomAccessFile.seek(currentOffset);

      long valueLength = this.randomAccessFile.readLong();
      currentOffset += 8;
      this.randomAccessFile.seek(currentOffset);

      this.randomAccessFile.readFully(valueReadBuffer, 0, (int) valueLength);

      String value = new String(valueReadBuffer, 0, (int) valueLength);
      record.setValue(value);

      offset += 16 + keyLength + valueLength;
      this.numberOfRecordsRead++;
    } catch (IOException e) {
      // Something wrong happened.
      e.printStackTrace();
      return -1;
    }

    return offset;
  }

  @Override
  public long getFileLength() {
    try {
      return this.randomAccessFile.length();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return 0;
  }

  @Override
  public int getNumberOfRecordsRead() {
    return this.numberOfRecordsRead;
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
