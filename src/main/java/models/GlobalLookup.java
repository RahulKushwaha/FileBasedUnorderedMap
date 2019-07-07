package models;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class GlobalLookup {

  private final ConcurrentHashMap<String, AtomicReference<KeyMetadata>> lookup = new ConcurrentHashMap<>();

  public boolean conditionalUpdate(String key, KeyMetadata keyMetadata) {
    AtomicReference<KeyMetadata> keyMetadataAtomicReference = this.lookup.get(key);
    if (keyMetadataAtomicReference == null) {
      // The key may have been deleted.
      return false;
    }

    return keyMetadataAtomicReference.compareAndSet(keyMetadata, keyMetadata);
  }

  public AtomicReference<KeyMetadata> get(String key) {
    return this.lookup.get(key);
  }

  public void put(String key, KeyMetadata keyMetadata) {
    this.lookup.put(key, new AtomicReference<>(keyMetadata));
  }
}
