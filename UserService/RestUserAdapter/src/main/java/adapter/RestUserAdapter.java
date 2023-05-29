package adapter;

import domain.exceptions.JwsException;
import domain.exceptions.UserException;
import domain.model.User;
import service.port.control.UserControlServicePort;
import service.port.infrasturcture.UserInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RestUserAdapter {

    @Inject
    private UserInfServicePort userInfServicePort;

    @Inject
    private UserControlServicePort userControlServicePort;

    public void addUser(User user) throws UserException {
        userControlServicePort.addUser(user);
    }

    public void updateUser(User user, String jws) throws UserException, JwsException {
        userControlServicePort.updateUser(user, jws);
    }

    public void activateUser(String username) throws UserException {
        userControlServicePort.activateUser(username);
    }

    public void deactivateUser(String username) throws UserException {
        userControlServicePort.deactivateUser(username);
    }

    public List<User> getAllUsers() {
        return userInfServicePort.getAllUsers();
    }

    public List<User> getAllClients() {
        return userInfServicePort.getAllClients();
    }

    public List<User> getUsersByUsername(String pattern) {
        return userInfServicePort.getUsersByUsername(pattern);
    }

    public User getUser(String username) {
        return userInfServicePort.getUser(username);
    }

    public User getByUsernameAndPassword(String username, String password) {
        return userInfServicePort.getByUsernameAndPassword(username, password);
    }

}
