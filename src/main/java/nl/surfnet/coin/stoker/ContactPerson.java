package nl.surfnet.coin.stoker;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactPerson {

  private String type;
  private String fullName;
  private String emailAddress;

  public ContactPerson(String type, String fullName, String emailAddress) {
    this.type = type;
    this.fullName = fullName;
    this.emailAddress = emailAddress;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ContactPerson that = (ContactPerson) o;

    if (emailAddress != null ? !emailAddress.equals(that.emailAddress) : that.emailAddress != null) return false;
    if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;
    if (type != null ? !type.equals(that.type) : that.type != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
    result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
    return result;
  }

  public String getType() {

    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public String toString() {
    return "ContactPerson{" +
      "type='" + type + '\'' +
      ", givenName='" + fullName + '\'' +
      ", emailAddress='" + emailAddress + '\'' +
      '}';
  }
}
