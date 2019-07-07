import config.DefaultConfig;
import java.io.IOException;
import java.io.RandomAccessFile;
import models.GlobalLookup;
import models.KeyMetadata;
import models.Record;

public class Initializer {

  private final GlobalLookup globalLookup;
  private final FileManager fileManager;

  public Initializer(GlobalLookup globalLookup, FileManager fileManager) {
    this.globalLookup = globalLookup;
    this.fileManager = fileManager;
  }

  public void buildGlobalLookup() throws IOException {
    // Read all the files sequentially from the data directory
    RandomAccessFile masterFile = fileManager
        .openFileForWriting(DefaultConfig.MASTER_FILE_LOCATION);

    String filename;
    while ((filename = masterFile.readLine()) != null) {
      RandomAccessFile dataFile = this.fileManager
          .openFileForReading(DefaultConfig.DATA_FILE_DIRECTORY + filename);
      RecordReader recordReader = new FileBasedRecordReader(dataFile);
      long offset = 0;
      while (offset >= 0) {
        Record record = new Record();
        KeyMetadata keyMetadata = new KeyMetadata(filename, offset);
        offset = recordReader.getRecord(offset, record);
        this.globalLookup.put(record.getKey(), keyMetadata);
      }

      recordReader.close();
    }
  }
}
