package services;

import data.control.UserControlPort;
import data.infrastructure.UserInfPort;
import domain.exceptions.UserException;
import jakarta.transaction.Transactional;
import domain.model.user.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@RequestScoped
public class UserService {

    @Inject
    private UserControlPort userControlPort;

    @Inject
    private UserInfPort userInfPort;


    private final Logger log = Logger.getLogger(getClass().getName());

    public List<User> getAllUsers() {
        return userInfPort.getAll();
    }

    public List<User> getAllClients() {
        return userInfPort.getAllClients();
    }

    public List<User> getClientsByUsername(String pattern) {
        return userInfPort.getByUsername(pattern);
    }

    public User getClientByUsername(String username) {
        return userInfPort.get(username);
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void addClientToHotel(User client) throws UserException {
        try {
            userInfPort.get(client.getUsername());
            log.warning("Client %s already exists".formatted(client.getUsername()));
            throw new UserException("Client %s already exists".formatted(client.getUsername()));
        } catch (NoSuchElementException e) {
            userControlPort.add(new User(client.getUsername(), client.getPassword(), client.getFirstName(),
                    client.getLastName(), client.getCity(), client.getStreet(), client.getStreetNumber(), client.getPostalCode()));
        }
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void modifyClient(User client) throws UserException {
        try {
            User oldUser = userInfPort.get(client.getUsername());

            oldUser.setUsername(client.getUsername());
            oldUser.setFirstName(client.getFirstName());
            oldUser.setLastName(client.getLastName());
            oldUser.setCity(client.getCity());
            oldUser.setStreet(client.getStreet());
            oldUser.setStreetNumber(client.getStreetNumber());
            oldUser.setPostalCode(client.getPostalCode());
            oldUser.setRole(client.getRole());

            userControlPort.update(oldUser);
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist".formatted(client.getUsername()));
            throw new UserException("Client %s does not exist".formatted(client.getUsername()));
        }
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void deactivateClient(String username) throws UserException {
        try {
            User user = userInfPort.get(username);
            user.setIsActive(false);
            userControlPort.update(user);
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void activateClient(String username) throws UserException {
        try {
            User user = userInfPort.get(username);
            user.setIsActive(true);
            userControlPort.update(user);
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

    public User getByUsernameAndPasswd(String username, String password) {
        return userInfPort.getByUsernameAndPasswd(username, password);
    }

}
