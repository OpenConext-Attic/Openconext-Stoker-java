package nl.surfnet.coin.stoker;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StokerTest {

  public static final ClassPathResource BASE_FOLDER = new ClassPathResource("/");
  private Stoker stoker;

  @Test(expected = IOException.class)
  public void testThrowsExceptionWhenFileDoesNotExist() throws Exception {
    stoker = new Stoker(new FileSystemResource("not exists"), BASE_FOLDER);
    Collection<StokerEntry> serviceProviders = stoker.getEduGainServiceProviders();
    assertEquals(0, serviceProviders.size());
  }

  @Test
  public void testReturnServices() throws Exception {
    stoker = stoker();
    Collection<StokerEntry> serviceProviders = stoker.getEduGainServiceProviders();
    assertEquals(3, serviceProviders.size());
  }

  @Test
  public void testReturnsSingleService() throws Exception {
    stoker = stoker();
    StokerEntry actual = stoker.getEduGainServiceProvider("http://saml.ps-ui-test.qalab.geant.net");

    assertEquals("http://saml.ps-ui-test.qalab.geant.net", actual.getEntityId());
    assertEquals(2, actual.getContactPersons().size());
    assertThat(actual.getContactPersons(), hasItem(new ContactPerson("technical", "DANTE IT", "it@dante.net")));
    assertThat(actual.getContactPersons(), hasItem(new ContactPerson("support", "DANTE IT Support", "DANTEITSupport@dante.net")));
  }

  @Test
  public void testReturnsSingleServiceWithNamespaceInXml() throws Exception {
    stoker = stoker();
    StokerEntry actual = stoker.getEduGainServiceProvider("https://appdb-dev.marie.hellasgrid.gr/edugain-connect");

    assertEquals("https://appdb-dev.marie.hellasgrid.gr/edugain-connect", actual.getEntityId());
    assertEquals(2, actual.getContactPersons().size());
    assertThat(actual.getContactPersons(), hasItem(new ContactPerson("technical", "Marios Chatziangelou", "mhaggel@iasa.gr")));
    assertThat(actual.getContactPersons(), hasItem(new ContactPerson("support", "Marios Chatziangelou", "appdb-support@iasa.gr")));
  }

  private Stoker stoker() throws Exception {
    return new Stoker(new ClassPathResource("/metadata.index.formatted.json"), BASE_FOLDER);
  }

  @Test
  public void testParseContactPersonWithOnlyCompanyCorrectly() throws Exception {
    //2ae3ff2a2b9a0223e745e0a52a2b1e1b.xml
    stoker = new Stoker(new ClassPathResource("/parse-contact-person-with-only-company-name.json"), BASE_FOLDER);
    StokerEntry actual = stoker.getEduGainServiceProvider("https://bodportal.geant.net/autobahn-gui");

    assertEquals("https://bodportal.geant.net/autobahn-gui", actual.getEntityId());
    assertEquals(2, actual.getContactPersons().size());
    assertThat(actual.getContactPersons(), hasItem(new ContactPerson("support", "DANTE IT Support", "DANTEITSupport@dante.net")));
    assertThat(actual.getContactPersons(), hasItem(new ContactPerson("technical", "DANTE IT", "it@dante.net")));
  }

  @Test
  public void testParsesTheAssertionConsumerService() throws Exception {
    //2ae3ff2a2b9a0223e745e0a52a2b1e1b.xml
    stoker = new Stoker(new ClassPathResource("/parse-contact-person-with-only-company-name.json"), BASE_FOLDER);

    StokerEntry actual = stoker.getEduGainServiceProvider("https://bodportal.geant.net/autobahn-gui");

    assertEquals(2, actual.getAssertionConsumerServices().size());
    Map<String, String> first = actual.getAssertionConsumerServices().get(0);
    assertEquals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST", first.get("Binding"));
    assertEquals("https://bodportal.geant.net/autobahn-gui/saml/SAMLAssertionConsumer", first.get("Location"));
    assertEquals("0", first.get("index"));

    assertThat(actual.getNameIdFormats(), hasItems(
      "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress",
      "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent",
      "urn:oasis:names:tc:SAML:2.0:nameid-format:transient"
    ));
  }
}
