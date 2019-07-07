import config.DefaultConfig;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.UUID;

public class FileManager {

  private final String fileURI = "/tmp/db/%s.log";
  private final RandomAccessFile masterFile;

  FileManager() {
    this.masterFile = this.openFileForWriting(DefaultConfig.MASTER_FILE_LOCATION);
  }

  public String getNewFile() {
    UUID uuid = UUID.randomUUID();
    String filePath = String.format(this.fileURI, uuid.toString());
    File file = new File(filePath);
    //Create the file
    try {
      if (file.createNewFile()) {
        return uuid.toString();
      } else {
        System.out.println("File already exists.");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public void deleteFile(String fileuuid) {
    try {
      Files.deleteIfExists(Paths.get(String.format(this.fileURI, fileuuid)));
    } catch (NoSuchFileException e) {
      System.out.println("No such file/directory exists");
    } catch (IOException e) {
      System.out.println("Invalid permissions.");
    }
  }

  public RandomAccessFile openFileForReading(String fileUuid) {
    try {
      return new RandomAccessFile(String.format(this.fileURI, fileUuid), "Initializer");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return null;
  }

  public RandomAccessFile openFileForWriting(String fileUuid) {
    try {
      RandomAccessFile newFile = new RandomAccessFile(String.format(this.fileURI, fileUuid), "rw");
      // Record data file in master table.
      this.masterFile.writeChars(fileUuid + "\n");

      return newFile;
    } catch (IOException e) {
      e.printStackTrace();

      return null;
    }
  }
}
