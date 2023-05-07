package soap.adapter;

import domain.exceptions.UserException;
import soap.mapper.SoapMapper;
import soap.model.UserSoap;
import soap.port.control.UserControlSoapPort;
import soap.port.infrastructure.UserInfSoapPort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@ApplicationScoped
@WebService(serviceName = "UserService")
public class SoapHotelAdapter {

    @Inject
    private UserInfSoapPort userInfSoapPort;

    @Inject
    private UserControlSoapPort userControlSoapPort;

    @WebMethod
    public List<UserSoap> getAllClients() {
        return userInfSoapPort.getAllClients().stream()
                .map(SoapMapper::userToUserSoap).toList();
    }

    @WebMethod
    public List<UserSoap> getAllModerators() {
        return userInfSoapPort.getAllModerators().stream()
                .map(SoapMapper::userToUserSoap).toList();
    }

    @WebMethod
    public List<UserSoap> getAllAdmins() {
        return userInfSoapPort.getAllAdmins().stream()
                .map(SoapMapper::userToUserSoap).toList();
    }

    @WebMethod
    public UserSoap getClient(@WebParam String username) {
        return SoapMapper.userToUserSoap(userInfSoapPort.getClient(username));
    }

    @WebMethod
    public UserSoap getModerator(@WebParam String username) {
        return SoapMapper.userToUserSoap(userInfSoapPort.getModerator(username));
    }

    @WebMethod
    public UserSoap getAdmin(@WebParam String username) {
        return SoapMapper.userToUserSoap(userInfSoapPort.getAdmin(username));
    }

    @WebMethod
    public void addUser(UserSoap user) throws UserException {
        userControlSoapPort.addUser(SoapMapper.userSoapToUser(user));
    }

}
