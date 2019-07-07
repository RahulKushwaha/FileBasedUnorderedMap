package Models;

public class KeyMetadata {

  private String fileuuid;
  private long offset;
  private int keyBytesLength;
  private int valueBytesLength;

  public KeyMetadata(String fileuuid, long offset, int keyBytesLength, int valueBytesLength) {
    this.fileuuid = fileuuid;
    this.offset = offset;
    this.keyBytesLength = keyBytesLength;
    this.valueBytesLength = valueBytesLength;
  }

  public int getKeyBytesLength() {
    return keyBytesLength;
  }

  public void setKeyBytesLength(int bytesLength) {
    this.keyBytesLength = bytesLength;
  }

  public long getOffset() {
    return offset;
  }

  public void setOffset(long offset) {
    this.offset = offset;
  }

  public int getValueBytesLength() {
    return valueBytesLength;
  }

  public void setValueBytesLength(int valueBytesLength) {
    this.valueBytesLength = valueBytesLength;
  }

  public String getFileuuid() {
    return fileuuid;
  }

  public void setFileuuid(String fileuuid) {
    this.fileuuid = fileuuid;
  }
}
