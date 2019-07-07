import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import models.GlobalLookup;

public class Main {

  private static void start() throws IOException {
    System.out.println("Starting File Based Key Value Store");
    FileManager fileManager = new FileManager();
    GlobalLookup globalLookup = new GlobalLookup();
    Initializer initializer = new Initializer(globalLookup, fileManager);
    initializer.buildGlobalLookup();

    CompactionManager compactionManager = new CompactionManager(globalLookup, fileManager);
    KeyValueStore keyValueWriter = new KeyValueStore(globalLookup, fileManager,
        compactionManager);


    Random rand = new Random();
    for (int i = 0; i < 2000000; i++) {
      UUID uuid = UUID.randomUUID();
      int num = rand.nextInt((100000) + 1);
      String key = String.format("Hello %s", num);
      keyValueWriter.add(key, uuid.toString());
      String value = keyValueWriter.get(key);

      //System.out.println(key + " " + value);
      assert value.equals(uuid.toString());
    }
  }

  public static void main(String[] args) throws IOException {
    start();
  }
}