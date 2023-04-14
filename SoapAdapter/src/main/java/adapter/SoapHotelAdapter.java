package adapter;

import control.SpecifiedUserControlPort;
import data.infrastructure.SpecifiedUserInfPort;
import mapper.SoapMapper;
import model.UserSoap;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@RequestScoped
@WebService(serviceName = "UserService")
public class SoapHotelAdapter {

    @Inject
    private SpecifiedUserInfPort specifiedUserInfPort;

    private SpecifiedUserControlPort specifiedUserControlPort;

    @WebMethod
    public List<UserSoap> getAllClients() {
        return specifiedUserInfPort.getAllClients().stream()
                .map(SoapMapper::userToUserSoap).toList();
    }

    @WebMethod
    public List<UserSoap> getAllModerators() {
        return specifiedUserInfPort.getAllModerators().stream()
                .map(SoapMapper::userToUserSoap).toList();
    }

    @WebMethod
    public List<UserSoap> getAllAdmins() {
        return specifiedUserInfPort.getAllAdmins().stream()
                .map(SoapMapper::userToUserSoap).toList();
    }

    @WebMethod
    public UserSoap getClient(@WebParam String username) {
        return SoapMapper.userToUserSoap(specifiedUserInfPort.getClient(username));
    }

    @WebMethod
    public UserSoap getModerator(@WebParam String username) {
        return SoapMapper.userToUserSoap(specifiedUserInfPort.getModerator(username));
    }

    @WebMethod
    public UserSoap getAdmin(@WebParam String username) {
        return SoapMapper.userToUserSoap(specifiedUserInfPort.getAdmin(username));
    }

    @WebMethod
    public void addUser(UserSoap user) {
        specifiedUserControlPort.addUser(SoapMapper.userSoapToUser(user));
    }

    @WebMethod
    public void modifyUser(UserSoap user) {
        specifiedUserControlPort.modifyUser(SoapMapper.userSoapToUser(user));
    }

}
