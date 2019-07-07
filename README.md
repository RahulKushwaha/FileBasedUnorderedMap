# FileBasedUnorderedMap

FileBaseUnorderedMap is a simple library supporting simple HashMap like operations, `get` and `put`. But instead of storing values in memory, they are stored on the disk.

## Data Format:

 `<KeyLength><KeyByteString><ValueLength><ValueByteString>`
 Example:
 `5hello5world`

where KeyLength and ValueLength are 8 Bytes long. 

## Important DataStructures: 

### Record:
	String 	key
	String 	value

### KeyMetadata:
	String  fileUuid                  // The file containing the key, value.
	long    offset                    // File offset to the starting byte of the record.
	int     KeyByteStringLength       // Number of bytes for Key.
	int     ValueByteStringLength     // Number of bytes for Value.

## Write Operation Path: 
1. Append the Record to the end of the file. 
2. Update in-memory HashMap, Key -> KeyMetadata.


Once a record is written to the disk it is never updated. To update a particular value, a new Record is simply appended to the file, and the in-memory HashMap is simply updated to point to the new location.

All the Records are not kept in the same file. Instead only 64,000,000 bytes are written to a single file, and then a new file is created to append. 

## Read Operation Path:
1. Check if the key is present in the in-memory HashMap. If not, we have never seen this key, return null. 
2. Read the record using they KeyMetadata information. 


## File Compaction: 
During normal operations we may have multiple key-value pairs which are no longer alive. To remove such key-value pairs, we start a compaction process which removes all such records. 

1. Once a file exceeds 64MB, add the file in the queue for compaction. 
2. During compaction, remove all the records which are no longer needed. 
	a. Check if the KeyMetadata of a particular record is the same as the one present in the in-memory HashMap. 
	b. If not it means that the Record has been updated in the meanwhile, and we can ignore this record.
	c. If the KeyMetadata is the same, we can atomically updated the in-memory HashMap to point to this new record.
