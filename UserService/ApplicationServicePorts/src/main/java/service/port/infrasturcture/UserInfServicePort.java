package service.port.infrasturcture;

import domain.model.user.User;

import java.util.List;

public interface UserInfServicePort {

    List<User> getAllUsers();

    List<User> getAllClients();

    List<User> getUsersByUsername(String pattern);

    User getUser(String username);

    User getByUsernameAndPassword(String username, String password);

}
