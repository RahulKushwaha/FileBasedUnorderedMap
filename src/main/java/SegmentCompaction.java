import Models.GlobalLookup;
import Models.KeyMetadata;
import Models.Record;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class SegmentCompaction {
    private GlobalLookup globalLookup = null;
    private String readFileUuid = null;
    private RecordReader recordReader = null;
    private RecordWriter recordWriter = null;
    private boolean isComplete = false;
    private long readFileOffset = 0;
    private long readFileLength = -1;
    private HashMap<String, KeyMetadata> newLookup = null;
    private String writeFileUuid = null;


    SegmentCompaction(GlobalLookup globalLookup, FileManager fileManager, String readFileUuid) {
        this.globalLookup = globalLookup;
        writeFileUuid = fileManager.getNewFile();
        RandomAccessFile writeFile = fileManager.openFileForWriting(writeFileUuid);
        this.recordWriter = new FileBasedRecordWriter(writeFile, writeFileUuid);
        RandomAccessFile readFile = fileManager.openFileForReading(readFileUuid);
        this.recordReader = new FileBasedRecordReader(readFile);
        readFileLength = this.recordReader.getFileLength();
        this.newLookup = new HashMap<>();
    }

    public String getCompactedFileName() {
        return writeFileUuid;
    }

    public boolean isDone() {
        return isComplete;
    }

    public int percentComplete() {
        return (int) (readFileOffset * 100 / readFileLength);
    }

    public HashMap<String, KeyMetadata> getLookup() {
        return this.newLookup;
    }

    public void start() throws IOException {
        long writeFileOffset = 0;
        readFileOffset = 0;
        // Continue reading the file until we reach the end.
        while (readFileOffset < readFileLength) {
            Record record = new Record();
            long newReadFileOffset = recordReader.getRecord(readFileOffset, record);

            // Check if key is present in global buffer.
            AtomicReference<KeyMetadata> keyMetadataAtomicReference = this.globalLookup.get(record.getKey());
            if (keyMetadataAtomicReference == null) {
                readFileOffset = newReadFileOffset;
                System.out.println("Skipping record as the record is missing.");
                continue;
            }

            KeyMetadata keyMetadata = keyMetadataAtomicReference.get();
            if (keyMetadata == null || keyMetadata.getOffset() != readFileOffset) {
                readFileOffset = newReadFileOffset;
                System.out.println(String.format("Skipping record as the offset is different [%d] [%d]",
                        keyMetadata.getOffset(), readFileOffset));
                continue;
            }

            recordWriter.appendRecord(record);

            keyMetadata.setFileuuid(readFileUuid);
            keyMetadata.setOffset(writeFileOffset);

            this.globalLookup.conditionalUpdate(record.getKey(), keyMetadata);

            writeFileOffset += record.getKey().length() + record.getValue().length() + 16;
            readFileOffset = newReadFileOffset;
        }

        System.out.println(String.format("Old Size: [%d] New Size: [%d]",
                this.recordReader.getFileLength(), this.recordWriter.getFileLength()));
        recordWriter.close();
        isComplete = true;
    }
}
