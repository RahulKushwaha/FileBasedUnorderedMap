import Models.GlobalLookup;
import Models.KeyMetadata;
import Models.Record;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class KeyValueStore {

  private CompactionManager compactionManager = null;
  private FileManager fileManager = null;
  private GlobalLookup globalLookup = null;
  private HashMap<String, RecordReader> readerLookup = null;
  private RecordWriter recordWriter = null;
  private String fileUuid = null;

  public KeyValueStore(GlobalLookup globalLookup, FileManager fileManager,
      CompactionManager compactionManager) throws IOException {
    this.globalLookup = globalLookup;
    this.fileManager = fileManager;
    fileUuid = fileManager.getNewFile();
    RandomAccessFile randomAccessFile = fileManager.openFileForWriting(fileUuid);
    this.recordWriter = new FileBasedRecordWriter(randomAccessFile, fileUuid);
    this.readerLookup = new HashMap<>();
    this.compactionManager = compactionManager;
  }

  public boolean add(String key, String value) {
    KeyMetadata keyMetadata = this.recordWriter
        .appendRecord(new Record(key, value, System.currentTimeMillis()));
    if (keyMetadata == null) {
      return false;
    }

    this.globalLookup.put(key, keyMetadata);

    if (this.recordWriter.getFileLength() >= 1000000) {
      this.compactionManager.addFileForCompaction(fileUuid);
      fileUuid = this.fileManager.getNewFile();
      RandomAccessFile randomAccessFile = fileManager.openFileForWriting(fileUuid);
      this.recordWriter.close();
      this.recordWriter = new FileBasedRecordWriter(randomAccessFile, fileUuid);
    }
    return true;
  }

  public String get(String key) {
    KeyMetadata keyMetadata = this.globalLookup.get(key).get();
    if (keyMetadata == null) {
      return null;
    }

    RecordReader fileBasedRecordReader = this.readerLookup.get(keyMetadata.getFileuuid());
    if (fileBasedRecordReader == null) {
      RandomAccessFile randomAccessFile = this.fileManager
          .openFileForReading(keyMetadata.getFileuuid());
      fileBasedRecordReader = new FileBasedRecordReader(randomAccessFile);

      this.readerLookup.put(keyMetadata.getFileuuid(), fileBasedRecordReader);
    }

    Record record = new Record();
    fileBasedRecordReader.getRecord(keyMetadata.getOffset(), record);

    return record.getValue();
  }
}

