import Models.GlobalLookup;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {

  public static void main(String[] args) throws InterruptedException {
    System.out.println("Starting File Based Key Value Store");
    Random rand = new Random();
    FileManager fileManager = new FileManager();
    GlobalLookup globalLookup = new GlobalLookup();
    CompactionManager compactionManager = new CompactionManager(globalLookup, fileManager);
    try {
      KeyValueStore keyValueWriter = new KeyValueStore(globalLookup, fileManager,
          compactionManager);
      for (int i = 0; i < 2000000; i++) {
        UUID uuid = UUID.randomUUID();
        int num = rand.nextInt((100000) + 1);
        String key = String.format("Hello %s", num);
        keyValueWriter.add(key, uuid.toString());
        String value = keyValueWriter.get(key);

        //System.out.println(key + " " + value);
        assert value.equals(uuid.toString());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
