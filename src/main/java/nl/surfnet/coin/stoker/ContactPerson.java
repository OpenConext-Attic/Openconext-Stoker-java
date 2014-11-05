package nl.surfnet.coin.stoker;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.util.StringUtils;

@JsonIgnoreProperties(value= {"displayName"}, ignoreUnknown = true)
public class ContactPerson {

  private String type;
  private String givenName;
  private String surName;
  private String emailAddress;
  private String telephoneNumber;
  private String company;

  public ContactPerson(String type, String givenName, String surName, String emailAddress, String telephoneNumber, String company) {
    this.type = type;
    this.givenName = givenName;
    this.surName = surName;
    this.emailAddress = emailAddress;
    this.telephoneNumber = telephoneNumber;
    this.company = company;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getSurName() {
    return surName;
  }

  public void setSurName(String surName) {
    this.surName = surName;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getTelephoneNumber() {
    return telephoneNumber;
  }

  public void setTelephoneNumber(String telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getDisplayName() {
    String displayName = "";
    if(StringUtils.hasText(this.givenName)) {
      displayName = givenName;
    }

    if(StringUtils.hasText(this.surName)) {
      displayName = " " + surName;
    }

    if(StringUtils.isEmpty(displayName)) {
      displayName = this.company;
    }
    return displayName.trim();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ContactPerson that = (ContactPerson) o;

    if (company != null ? !company.equals(that.company) : that.company != null) return false;
    if (emailAddress != null ? !emailAddress.equals(that.emailAddress) : that.emailAddress != null) return false;
    if (givenName != null ? !givenName.equals(that.givenName) : that.givenName != null) return false;
    if (surName != null ? !surName.equals(that.surName) : that.surName != null) return false;
    if (telephoneNumber != null ? !telephoneNumber.equals(that.telephoneNumber) : that.telephoneNumber != null)
      return false;
    if (type != null ? !type.equals(that.type) : that.type != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = type != null ? type.hashCode() : 0;
    result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
    result = 31 * result + (surName != null ? surName.hashCode() : 0);
    result = 31 * result + (emailAddress != null ? emailAddress.hashCode() : 0);
    result = 31 * result + (telephoneNumber != null ? telephoneNumber.hashCode() : 0);
    result = 31 * result + (company != null ? company.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ContactPerson{" +
      "type='" + type + '\'' +
      ", givenName='" + givenName + '\'' +
      ", surName='" + surName + '\'' +
      ", emailAddress='" + emailAddress + '\'' +
      ", telephoneNumber='" + telephoneNumber + '\'' +
      ", company='" + company + '\'' +
      '}';
  }
}
