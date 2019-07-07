import Models.GlobalLookup;
import Models.KeyMetadata;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CompactionManager {

  private ConcurrentLinkedQueue<String> filesForCompaction = null;
  private FileManager fileManager = null;
  private GlobalLookup globalLookup = null;
  private SegmentCompaction segmentCompaction = null;

  CompactionManager(GlobalLookup globalLookup, FileManager fileManager) {
    this.globalLookup = globalLookup;
    this.fileManager = fileManager;
    this.filesForCompaction = new ConcurrentLinkedQueue<>();
    CompletableFuture.runAsync(this::startCompactionProcess);
  }

  public boolean addFileForCompaction(String fileUuid) {
    return this.filesForCompaction.add(fileUuid);
  }

  private void mergeLookupWithGlobalLookup(HashMap<String, KeyMetadata> lookup) {
    for (String key : lookup.keySet()) {
      KeyMetadata keyMetadata = lookup.get(key);
      this.globalLookup.conditionalUpdate(key, keyMetadata);
    }
  }

  private void startCompactionProcess() {
    while (true) {
      if (segmentCompaction != null && segmentCompaction.isDone()) {
        this.mergeLookupWithGlobalLookup(segmentCompaction.getLookup());
        String completedFileUuid = this.filesForCompaction.poll();
        System.out.println(String.format("File Compaction completed: %s", completedFileUuid));
        System.out.println(
            String.format("Compact File Name: %s", this.segmentCompaction.getCompactedFileName()));
        this.fileManager.deleteFile(completedFileUuid);
        segmentCompaction = null;

      } else {
        if (segmentCompaction != null) {
          System.out.println(String
              .format("Segment Compaction Completed: %d", segmentCompaction.percentComplete()));
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        } else {
          if (this.filesForCompaction.size() > 0) {
            String fileUuid = this.filesForCompaction.peek();
            segmentCompaction = new SegmentCompaction(globalLookup, fileManager, fileUuid);
            CompletableFuture.runAsync(() -> {
              try {
                segmentCompaction.start();
              } catch (IOException e) {
                e.printStackTrace();
              }
            });
          } else {
            System.out.println("No File for compaction right now.");
            System.out.println("Sleeping for 5 seconds");
            try {
              Thread.sleep(5000);
            } catch (InterruptedException e) {
              e.printStackTrace();
              System.out.println("Thread interrupted during sleep.");
            }
          }
        }
      }
    }
  }
}
