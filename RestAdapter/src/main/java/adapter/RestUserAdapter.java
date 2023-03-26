package adapter;

import domain.exceptions.JwsException;
import domain.exceptions.UserException;
import domain.model.user.User;
import service.port.control.UserControlServicePort;
import service.port.infrasturcture.UserInfServicePort;
import services.UserService;

import javax.inject.Inject;
import java.util.List;

public class RestUserAdapter implements UserInfServicePort, UserControlServicePort {

    @Inject
    private UserService userService;

    @Override
    public void addUser(User user) throws UserException {
        userService.addClientToHotel(user);
    }

    @Override
    public void updateUser(User user, String jws) throws UserException, JwsException {
        userService.modifyClient(user);
    }

    @Override
    public void activateUser(String username) throws UserException {
        userService.activateClient(username);
    }

    @Override
    public void deactivateUser(String username) throws UserException {
        userService.deactivateClient(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    public List<User> getAllClients() {
        return userService.getAllClients();
    }

    @Override
    public List<User> getUsersByUsername(String pattern) {
        return userService.getClientsByUsername(pattern);
    }

    @Override
    public User getUser(String username) {
        return userService.getClientByUsername(username);
    }

    @Override
    public User getByUsernameAndPassword(String username, String password) {
        return userService.getByUsernameAndPasswd(username, password);
    }

}
