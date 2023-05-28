package services;

import data.port.control.UserControlPort;
import data.port.infrastructure.UserInfPort;
import domain.exceptions.UserException;
import domain.model.user.User;
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
public class UserService implements UserControlServicePort, UserInfServicePort {

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
    public void updateUser(User user) throws UserException {
        userControlPort.update(user);
    }

    @Override
    public void deactivateUser(String username) throws UserException {
        try {
            User user = userInfPort.get(username);
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
}
