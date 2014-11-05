package nl.surfnet.coin.stoker;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static org.w3c.dom.Node.ELEMENT_NODE;

public class Stoker {

  public static final String ELEMENT_NAME_ID_FORMAT = "NameIDFormat";
  public static final String ELEMENT_ASSERTION_CONSUMER_SERVICE = "AssertionConsumerService";

  private static interface ElementHandler {
    void handle(Element element, StokerEntry stokerEntry);
  }

  private static class ElementTemplate {
    private final Document document;
    private final StokerEntry stokerEntry;

    public ElementTemplate(Document document, StokerEntry stokerEntry) {
      this.document = document;
      this.stokerEntry = stokerEntry;
    }

    public void doForElements(String elementName, ElementHandler elementHandler) {
      NodeList nodes = document.getElementsByTagNameNS(NAMESPACE_URI, elementName);
      for (int i = 0; i < nodes.getLength(); i++) {
        if (nodes.item(i).getNodeType() == ELEMENT_NODE) {
          Element element = (Element) nodes.item(i);
          elementHandler.handle(element, stokerEntry);
        }
      }
    }
  }

  public static final String NAMESPACE_URI = "urn:oasis:names:tc:SAML:2.0:metadata";
  public static final String ATTR_BINDING = "Binding";
  public static final String ATTR_LOCATION = "Location";
  public static final String ATTR_INDEX = "index";
  private final ObjectMapper objectMapper;
  private final Resource detailDataFolder;
  private final StokerData stokerData;

  private static final Logger LOG = LoggerFactory.getLogger(Stoker.class);

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
    for (StokerEntry stokerEntry : getEduGainServiceProviders()) {
      String filename = calculateFilename(stokerEntry.getEntityId());
      Document document = parseDetailData(filename);
      ElementTemplate elementTemplate = new ElementTemplate(document, stokerEntry);
      addContactPersonsFromDocument(elementTemplate);
      addAssertionConsumerServices(elementTemplate);
      addNameIdFormats(elementTemplate);
    }

  }

  private void addNameIdFormats(ElementTemplate elementTemplate) {
    elementTemplate.doForElements(ELEMENT_NAME_ID_FORMAT, new ElementHandler() {
      @Override
      public void handle(Element element, StokerEntry stokerEntry) {
        stokerEntry.addNameIdFormat(element.getTextContent());
      }
    });
  }

  private void addAssertionConsumerServices(ElementTemplate elementTemplate) {
    elementTemplate.doForElements(ELEMENT_ASSERTION_CONSUMER_SERVICE, new ElementHandler() {
      @Override
      public void handle(Element element, StokerEntry stokerEntry) {
        String binding = getAttrValueFromElement(element, ATTR_BINDING);
        String location = getAttrValueFromElement(element, ATTR_LOCATION);
        String index = getAttrValueFromElement(element, ATTR_INDEX);
        stokerEntry.addAssertionConsumerService(
          ImmutableMap.of(
            ATTR_BINDING, binding,
            ATTR_LOCATION, location,
            ATTR_INDEX, index
          )
        );
      }
    });
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

  private void addContactPersonsFromDocument(ElementTemplate elementTemplate) {

    elementTemplate.doForElements("ContactPerson", new ElementHandler() {
      @Override
      public void handle(Element element, StokerEntry stokerEntry) {
        String contactType = getAttrValueFromElement(element, "contactType");

        String emailAddress = getTextFromElement(element, "EmailAddress").replaceAll("mailto:", "");
        String givenName = getTextFromElement(element, "GivenName");
        String surName = getTextFromElement(element, "SurName");
        String company = getTextFromElement(element, "Company");
        String phone = getTextFromElement(element, "TelephoneNumber");

        stokerEntry.addContactPerson(new ContactPerson(contactType, givenName, surName, emailAddress, phone, company));
      }


    });
  }

  private String getTextFromElement(Element element, String nodeName) {
    String text = "";
    NodeList emailAddressNodes = element.getElementsByTagNameNS(NAMESPACE_URI, nodeName);
    if (any(emailAddressNodes)) {
      text = emailAddressNodes.item(0).getTextContent();
    }
    return text;
  }


  private String getAttrValueFromElement(Element element, String attribute) {
    Node node = element.getAttributes().getNamedItem(attribute);
    return node == null ? "" : node.getTextContent();
  }

  private NodeList getElementsFromDocument(Document document, String nodeName) {
    return document.getElementsByTagNameNS(NAMESPACE_URI, nodeName);
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
    LOG.debug("Calculated filename for entityId {} is {}.xml", spEntityId, filename);
    return filename;
  }
}
