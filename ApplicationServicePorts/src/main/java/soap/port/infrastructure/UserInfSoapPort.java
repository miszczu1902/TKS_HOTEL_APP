package soap.port.infrastructure;

import domain.model.user.User;

import java.util.List;

public interface UserInfSoapPort {

    List<User> getAllClients();

    List<User> getAllModerators();

    List<User> getAllAdmins();

    User getClient(String username);

    User getModerator(String username);

    User getAdmin(String username);

}
