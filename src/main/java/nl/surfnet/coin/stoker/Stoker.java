package nl.surfnet.coin.stoker;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;

import static javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING;

public class Stoker {
  public static final String NAMESPACE_URI = "urn:oasis:names:tc:SAML:2.0:metadata";
  private final ObjectMapper objectMapper;
  private final Resource detailDataFolder;
  private final StokerData stokerData;
  private final static Predicate<StokerEntry> onlyServiceProviders = new Predicate<StokerEntry>() {
    @Override
    public boolean apply(StokerEntry input) {
      return input.isServiceProvider();
    }
  };

  public Stoker(Resource metaDataFileLocation, Resource detailDataFolder) throws Exception {
    this.detailDataFolder = detailDataFolder;
    this.objectMapper = new ObjectMapper();
    this.stokerData = objectMapper.readValue(IOUtils.toString(metaDataFileLocation.getInputStream()), StokerData.class);
    for(StokerEntry stokerEntry: getEduGainServiceProviders()) {
      String filename = calculateFilename(stokerEntry.getEntityId());
      Document document = parseDetailData(filename);
      addContactPersonsFromDocument(stokerEntry, document);
    }

  }

  public Collection<StokerEntry> getEduGainServiceProviders() {
    return Collections2.filter(stokerData.getEntities(), onlyServiceProviders);
  }

  public Collection<StokerEntry> getEduGainServiceProviders(final Collection<String> spEntityIds) {
    return Collections2.filter(getEduGainServiceProviders(), new Predicate<StokerEntry>() {
      @Override
      public boolean apply(StokerEntry input) {
        return spEntityIds.contains(input.getEntityId());
      }
    });
  }

  public StokerEntry getEduGainServiceProvider(String spEntityId) {
    return getEduGainServiceProviders(Arrays.asList(spEntityId)).iterator().next();
  }

  private void addContactPersonsFromDocument(StokerEntry stokerEntry, Document document) {

    NodeList contactPersons = document.getElementsByTagNameNS(NAMESPACE_URI, "ContactPerson");
    for (int i = 0; i < contactPersons.getLength(); i++) {
      if (contactPersons.item(i).getNodeType() == Node.ELEMENT_NODE) {

        Element contactPersonNode = (Element) contactPersons.item(i);
        String contactType = contactPersonNode.getAttributes().getNamedItem("contactType").getTextContent();
        NodeList emailAddressNodes = contactPersonNode.getElementsByTagNameNS(NAMESPACE_URI, "EmailAddress");
        if (any(emailAddressNodes)) {
          String emailAddress = emailAddressNodes.item(0).getTextContent().replaceAll("mailto:", "");
          NodeList givenNameNodes = contactPersonNode.getElementsByTagNameNS(NAMESPACE_URI, "GivenName");
          String fullName = "";
          if (any(givenNameNodes)) {
            fullName = givenNameNodes.item(0).getTextContent();
          }
          NodeList surNameNodes = contactPersonNode.getElementsByTagNameNS(NAMESPACE_URI, "SurName");
          if (any(surNameNodes)) {
            fullName = String.format("%s %s", fullName, surNameNodes.item(0).getTextContent()).trim();
          }


          stokerEntry.addContactPerson(new ContactPerson(contactType, fullName, emailAddress));
        }
      }
    }
  }

  private boolean any(NodeList nodes) {
    return nodes != null && nodes.getLength() > 0;
  }

  private Document parseDetailData(String filename) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setFeature(FEATURE_SECURE_PROCESSING, true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(detailDataFolder.createRelative(filename + ".xml").getInputStream());
  }

  private String calculateFilename(String spEntityId) throws NoSuchAlgorithmException {
    MessageDigest m = MessageDigest.getInstance("MD5");
    m.reset();
    m.update(spEntityId.getBytes());
    byte[] digest = m.digest();
    BigInteger bigInt = new BigInteger(1, digest);
    String filename = bigInt.toString(16);
    // Now we need to zero pad it if you actually want the full 32 chars.
    while (filename.length() < 32) {
      filename = "0" + filename;
    }
    return filename;
  }
}
