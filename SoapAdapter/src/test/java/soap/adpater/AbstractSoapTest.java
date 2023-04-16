package soap.adpater;

import domain.model.Role;
import org.junit.Before;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import soap.model.UserSoap;

import javax.xml.soap.*;
import java.util.logging.Logger;

public class AbstractSoapTest {

    protected static final String ENDPOINT_URL = "http://localhost:8080/UserService";
    protected static final String NAMESPACE_URI = "http://adapter.soap/";
    protected static final String PREFIX = "UserService";
    protected static SOAPConnectionFactory soapConnectionFactory;
    protected static SOAPConnection soapConnection;
    protected static MessageFactory messageFactory;
    protected static Logger logger = Logger.getLogger(AbstractSoapTest.class.getName());

//    @BeforeEach
//    public void initTest() throws SOAPException {
////        soapConnectionFactory = SOAPConnectionFactory.newInstance();
////        soapConnection = soapConnectionFactory.createConnection();
////        messageFactory = MessageFactory.newInstance();
//    }

    protected int getUsers(Role role) throws SOAPException {
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();
        messageFactory = MessageFactory.newInstance();

        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();

        switch (role) {
            case ADMIN -> soapBody.addChildElement("getAllAdmins", PREFIX, NAMESPACE_URI);
            case MODERATOR -> soapBody.addChildElement("getAllModerators", PREFIX, NAMESPACE_URI);
            default -> soapBody.addChildElement("getAllUsers", PREFIX, NAMESPACE_URI);
        }

        SOAPMessage soapResponse = soapConnection.call(soapMessage, ENDPOINT_URL);

        SOAPBody responseBody = soapResponse.getSOAPBody();
        soapConnection.close();

        return responseBody.getChildElements().next().getChildNodes().getLength();
    }

    protected SOAPMessage createUser(Role role) throws SOAPException {
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
        soapConnection = soapConnectionFactory.createConnection();
        messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPPart soapPart = message.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(PREFIX, NAMESPACE_URI + "/SoapHotelAdapter/addUserRequest");

        UserSoap userSoap = new UserSoap(
                "zaq12wsxcde34rfv",
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                "12-345",
                role.toString(),
                true
        );

        SOAPBody requestBody = envelope.getBody();
        SOAPElement createUser = requestBody.addChildElement("addUser", PREFIX);
        SOAPElement user = createUser.addChildElement("userSoap");
        user.addChildElement("city").setTextContent(userSoap.getCity());
//        user.addChildElement("city").setTextContent("abcdef");
//        user.addChildElement("city", userSoap.getCity());
        user.addChildElement("firstName").setTextContent(userSoap.getFirstName());

//        user.addChildElement("firstName", userSoap.getFirstName());
        user.addChildElement("isActive").setTextContent(userSoap.getIsActive().toString());

//        user.addChildElement("isActive", userSoap.getIsActive().toString());
        user.addChildElement("lastName").setTextContent(userSoap.getLastName());

//        user.addChildElement("lastName", userSoap.getLastName());
        user.addChildElement("postalCode").setTextContent(userSoap.getPostalCode());

//        user.addChildElement("postalCode", userSoap.getPostalCode());
        user.addChildElement("role").setTextContent(userSoap.getRole());

//        user.addChildElement("role", userSoap.getRole());
//        user.addChildElement("street", userSoap.getStreet());
        user.addChildElement("street").setTextContent(userSoap.getStreet());
//        user.addChildElement("streetNumber", userSoap.getStreetNumber());
        user.addChildElement("streetNumber").setTextContent(userSoap.getStreetNumber());
//        user.addChildElement("username", userSoap.getUsername());
        user.addChildElement("username").setTextContent(userSoap.getUsername());
//        user.addChildElement("password", userSoap.getPassword());
        user.addChildElement("password").setTextContent(userSoap.getPassword());
        message.saveChanges();

        return message;
    }


}
