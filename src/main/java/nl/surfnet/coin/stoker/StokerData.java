package nl.surfnet.coin.stoker;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StokerData {

  private Map<String, StokerEntry> entities;
  private DateTime cacheUntil;
  private DateTime processed;
  private DateTime validUntil;

  public Map<String, StokerEntry> getEntities() {
    return entities;
  }

  public void setEntitiesMap(Map<String, StokerEntry> entities) {
    this.entities = entities;
  }

  public Collection<StokerEntry> entities() {
    return entities.values();
  }

  public DateTime getCacheUntil() {
    return cacheUntil;
  }

  public void setCacheUntil(DateTime cacheUntil) {
    this.cacheUntil = cacheUntil;
  }

  public DateTime getProcessed() {
    return processed;
  }

  public void setProcessed(DateTime processed) {
    this.processed = processed;
  }

  public DateTime getValidUntil() {
    return validUntil;
  }

  public void setValidUntil(DateTime validUntil) {
    this.validUntil = validUntil;
  }
}
