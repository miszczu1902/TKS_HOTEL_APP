package soap.adpater;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import domain.model.Role;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.BeforeClass;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import soap.model.UserSoap;

import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class AbstractSoapTest {

    protected static final String ENDPOINT_URL = "http://localhost:8080/UserService";
    protected static final String NAMESPACE_URI = "http://adapter.soap/";
    protected static final String PREFIX = "ns2";
    protected static final String SOAP_ENV = "http://schemas.xmlsoap.org/soap/envelope/";
    protected static SOAPConnectionFactory soapConnectionFactory;
    protected static SOAPConnection soapConnection;
    protected static MessageFactory messageFactory;
    protected static Logger logger = Logger.getLogger(AbstractSoapTest.class.getName());
    protected static XmlMapper mapper = new XmlMapper();

    @BeforeClass
    public static void initTest() {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    protected int getUsers(Role role) throws Exception {
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();
        messageFactory = MessageFactory.newInstance();

        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("SOAP-ENV", SOAP_ENV);
        envelope.addNamespaceDeclaration("ns2", NAMESPACE_URI);
        SOAPBody soapBody = envelope.getBody();

        switch (role) {
            case ADMIN -> soapBody.addChildElement("getAllAdmins", PREFIX, NAMESPACE_URI);
            case MODERATOR -> soapBody.addChildElement("getAllModerators", PREFIX, NAMESPACE_URI);
            default -> soapBody.addChildElement("getAllClients", PREFIX, NAMESPACE_URI);
        }

        SOAPMessage soapResponse = soapConnection.call(soapMessage, ENDPOINT_URL);
        log("XML Response", soapResponse);

        SOAPBody responseBody = soapResponse.getSOAPBody();
        soapConnection.close();

        return responseBody.getChildElements().next().getChildNodes().getLength();
    }

    protected String getUser(String username, Role role) throws Exception {
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();
        messageFactory = MessageFactory.newInstance();

        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("SOAP-ENV", SOAP_ENV);
        envelope.addNamespaceDeclaration("ns2", NAMESPACE_URI);

        SOAPBody soapBody = envelope.getBody();
        String method;

        switch (role) {
            case ADMIN -> method = "getAllAdmins";
            case MODERATOR -> method = "getAllModerators";
            default -> method = "getAllClients";
        }

        SOAPElement element = soapBody.addChildElement(method, PREFIX);
        soapBody.addChildElement("arg0").setTextContent(username);
        log("XML Request", soapMessage);

        SOAPMessage soapResponse = soapConnection.call(soapMessage, ENDPOINT_URL);
        log("XML Response", soapResponse);

        SOAPBody responseBody = soapResponse.getSOAPBody();
        soapConnection.close();

        NodeList nodes = responseBody.getElementsByTagName("return");
        if (nodes.getLength() > 0) {
            Element userElement = (Element) nodes.item(0);
            return userElement.getElementsByTagName("username").item(0).getTextContent();
        } else {
            return "";
        }

    }

    protected SOAPMessage createUser(Role role) throws Exception {
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();
        messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(PREFIX, NAMESPACE_URI);

        UserSoap userSoap = new UserSoap(
                RandomStringUtils.randomAlphabetic(10) + role.toString(),
                "examplePassword",
                "John",
                "Doe",
                "ExampleCity",
                "ExampleStreet",
                "123",
                "12-345",
                role.toString(),
                true
        );

        SOAPBody requestBody = envelope.getBody();
        SOAPElement createUser = requestBody.addChildElement("addUser", PREFIX);
        SOAPElement user = createUser.addChildElement("arg0");

        user.addChildElement("username").setTextContent(userSoap.getUsername());
        user.addChildElement("password").setTextContent(userSoap.getPassword());
        user.addChildElement("firstName").setTextContent(userSoap.getFirstName());
        user.addChildElement("lastName").setTextContent(userSoap.getLastName());
        user.addChildElement("city").setTextContent(userSoap.getCity());
        user.addChildElement("street").setTextContent(userSoap.getStreet());
        user.addChildElement("streetNumber").setTextContent(userSoap.getStreetNumber());
        user.addChildElement("postalCode").setTextContent(userSoap.getPostalCode());
        user.addChildElement("role").setTextContent(userSoap.getRole());
        user.addChildElement("isActive").setTextContent(userSoap.getIsActive().toString());

        log("XML Request", message);
        message.saveChanges();

        return message;
    }

    protected void log(String msg, SOAPMessage soapMessage) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMessage.writeTo(out);
        logger.info(msg + ":\n " + mapper.writeValueAsString(out.toString(StandardCharsets.UTF_8)));
    }

}
