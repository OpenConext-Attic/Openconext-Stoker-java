package nl.surfnet.coin.stoker;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class StokerTest {

  private Stoker stoker;

  @Test(expected = IOException.class)
  public void testThrowsExceptionWhenFileDoesNotExist() throws Exception {
    stoker = new Stoker(new FileSystemResource("not exists"), new ClassPathResource("/"));
    Collection<StokerEntry> serviceProviders = stoker.getEduGainServiceProviders();
    assertEquals(0, serviceProviders.size());
  }

  @Test
  public void testReturnServices() throws Exception {
    stoker = new Stoker(new ClassPathResource("/metadata.index.formatted.json"), new ClassPathResource("/"));
    Collection<StokerEntry> serviceProviders = stoker.getEduGainServiceProviders();
    assertEquals(3, serviceProviders.size());
  }

  @Test
  public void testReturnsSingleService() throws Exception {
    stoker = new Stoker(new ClassPathResource("/metadata.index.formatted.json"), new ClassPathResource("/"));
    StokerEntry actual = stoker.getEduGainServiceProvider("http://saml.ps-ui-test.qalab.geant.net");

    assertEquals("http://saml.ps-ui-test.qalab.geant.net", actual.getEntityId());
    assertEquals(2, actual.getContactPersons().size());
    assertThat(actual.getContactPersons(), hasItem(new ContactPerson("technical", "DANTE IT", "it@dante.net")));
    assertThat(actual.getContactPersons(), hasItem(new ContactPerson("support", "DANTE IT Support", "DANTEITSupport@dante.net")));
  }

  @Test
  public void testReturnsSingleServiceWithNamespaceInXml() throws Exception {
    stoker = new Stoker(new ClassPathResource("/metadata.index.formatted.json"), new ClassPathResource("/"));
    StokerEntry actual = stoker.getEduGainServiceProvider("https://appdb-dev.marie.hellasgrid.gr/edugain-connect");

    assertEquals("https://appdb-dev.marie.hellasgrid.gr/edugain-connect", actual.getEntityId());
    assertEquals(2, actual.getContactPersons().size());
    assertThat(actual.getContactPersons(), hasItem(new ContactPerson("technical", "Marios Chatziangelou", "mhaggel@iasa.gr")));
    assertThat(actual.getContactPersons(), hasItem(new ContactPerson("support", "Marios Chatziangelou", "appdb-support@iasa.gr")));

  }
}
