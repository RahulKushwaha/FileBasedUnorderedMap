package Models;

public class Record {

  private String key;
  private String value;
  private long timestamp;

  public Record() {
  }

  public Record(String key, String value, long timestamp) {
    this.key = key;
    this.value = value;
    this.timestamp = timestamp;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
}
