package services;

import control.UserControlSoapPort;
import data.control.UserControlPort;
import data.infrastructure.UserInfPort;
import domain.exceptions.JwsException;
import domain.exceptions.UserException;
import domain.model.Role;
import domain.model.user.User;
import infrastructure.UserInfSoapPort;
import service.port.control.UserControlServicePort;
import service.port.infrasturcture.UserInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional(dontRollbackOn = NoSuchElementException.class)
public class UserService implements UserControlSoapPort, UserInfSoapPort, UserControlServicePort, UserInfServicePort {

    @Inject
    private UserControlPort userControlPort;

    @Inject
    private UserInfPort userInfPort;

    private final Logger log = Logger.getLogger(getClass().getName());

    @Override
    public List<User> getAllUsers() {
        return userInfPort.getAll();
    }

    @Override
    public void addUser(User user) throws UserException {
        try {
            userInfPort.get(user.getUsername());
            log.warning("Client %s already exists".formatted(user.getUsername()));
            throw new UserException("Client %s already exists".formatted(user.getUsername()));
        } catch (NoSuchElementException e) {
            userControlPort.add(user);
        }
    }

    @Override
    public void updateUser(User user, String jws) throws UserException, JwsException {
        this.modifyUser(user);
    }

    @Override
    public void modifyUser(User user) throws UserException {
        try {
            User oldUser = userInfPort.get(user.getUsername());

            oldUser.setUsername(user.getUsername());
            oldUser.setFirstName(user.getFirstName());
            oldUser.setLastName(user.getLastName());
            oldUser.setCity(user.getCity());
            oldUser.setStreet(user.getStreet());
            oldUser.setStreetNumber(user.getStreetNumber());
            oldUser.setPostalCode(user.getPostalCode());
            oldUser.setRole(user.getRole());

            userControlPort.update(oldUser);
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist".formatted(user.getUsername()));
            throw new UserException("Client %s does not exist".formatted(user.getUsername()));
        }
    }

    @Override
    public void deactivateUser(String username) throws UserException {
        try {
            User user = userInfPort.get(username);
            user.setIsActive(false);
            userControlPort.update(user);
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

    @Override
    public void activateUser(String username) throws UserException {
        try {
            User user = userInfPort.get(username);
            user.setIsActive(true);
            userControlPort.update(user);
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

    @Override
    public List<User> getAllClients() {
        return userInfPort.getAllClients();
    }

    @Override
    public List<User> getUsersByUsername(String pattern) {
        return userInfPort.getByUsername(pattern);
    }

    @Override
    public User getUser(String username) {
        return userInfPort.get(username);
    }

    @Override
    public User getByUsernameAndPassword(String username, String password) {
        return userInfPort.getByUsernameAndPasswd(username, password);
    }

    @Override
    public List<User> getAllModerators() {
        return userInfPort.getAll().stream()
                .filter(mod -> mod.getRole() == Role.MODERATOR).toList();
    }

    @Override
    public List<User> getAllAdmins() {
        return userInfPort.getAll().stream()
                .filter(mod -> mod.getRole() == Role.ADMIN).toList();
    }

    @Override
    public User getClient(String username) {
        return this.getAllClients().stream()
                .filter(client -> client.getUsername().equals(username))
                .toList().get(0);
    }

    @Override
    public User getModerator(String username) {
        return this.getAllModerators().stream()
                .filter(client -> client.getUsername().equals(username))
                .toList().get(0);
    }

    @Override
    public User getAdmin(String username) {
        return this.getAllAdmins().stream()
                .filter(client -> client.getUsername().equals(username))
                .toList().get(0);
    }

}
