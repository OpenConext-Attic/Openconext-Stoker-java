package nl.surfnet.coin.stoker;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collection;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StokerEntry {
  public static final String TYPE_SERVICE_PROVIDER = "sp";
  private String displayNameEn;
  private String displayNameNl;
  private String entityId;
  private Collection<String> types = new ArrayList<>();
  private Collection<ContactPerson> contactPersons = new ArrayList<>();

  public String getDisplayNameEn() {
    return displayNameEn;
  }

  public void setDisplayNameEn(String displayNameEn) {
    this.displayNameEn = displayNameEn;
  }

  public String getDisplayNameNl() {
    return displayNameNl;
  }

  public void setDisplayNameNl(String displayNameNl) {
    this.displayNameNl = displayNameNl;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public Collection<String> getTypes() {
    return types;
  }

  public void setTypes(Collection<String> types) {
    this.types = types;
  }

  public boolean isServiceProvider() {
    return types != null && types.contains(TYPE_SERVICE_PROVIDER);
  }

  public Collection<ContactPerson> getContactPersons() {
    return contactPersons;
  }

  public void setContactPersons(Collection<ContactPerson> contactPersons) {
    this.contactPersons = contactPersons;
  }

  public void addContactPerson(ContactPerson contactPerson) {
    this.contactPersons.add(contactPerson);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    StokerEntry that = (StokerEntry) o;

    if (!entityId.equals(that.entityId)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return entityId.hashCode();
  }

  @Override
  public String toString() {
    return "StokerEntry{" +
      "types=" + types +
      ", entityId='" + entityId + '\'' +
      ", displayNameEn='" + displayNameEn + '\'' +
      '}';
  }
}
