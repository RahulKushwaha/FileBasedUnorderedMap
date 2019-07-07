package models;

public class KeyMetadata {

  private String fileuuid;
  private long offset;

  public KeyMetadata(String fileuuid, long offset) {
    this.fileuuid = fileuuid;
    this.offset = offset;
  }

  public long getOffset() {
    return offset;
  }

  public void setOffset(long offset) {
    this.offset = offset;
  }

  public String getFileuuid() {
    return fileuuid;
  }

  public void setFileuuid(String fileuuid) {
    this.fileuuid = fileuuid;
  }
}
