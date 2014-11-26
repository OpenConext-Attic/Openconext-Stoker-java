package nl.surfnet.coin.stoker;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import static org.junit.Assert.assertTrue;

public class StokerIntegrationTest {

  @Test
  public void testStokerFiles() throws Exception {
    ClassPathResource detailDataFolder = new ClassPathResource("/stoker-data/");
    ClassPathResource metaDataFileLocation = new ClassPathResource("/stoker-data/metadata.index.json");
    Stoker stoker = new Stoker(metaDataFileLocation, detailDataFolder);
    assertTrue(stoker.getEduGainServiceProviders().size() > 0);
  }
}
