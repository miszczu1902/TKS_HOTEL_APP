package services;

import adapter.aggregates.mapper.ModelMapper;
import adapter.model.user.UserEnt;
import data.control.UserControlPort;
import data.infrastructure.SpecifiedUserInfPort;
import data.infrastructure.UserInfPort;
import domain.exceptions.UserException;
import domain.model.Role;
import domain.model.user.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional(dontRollbackOn = NoSuchElementException.class)
public class UserService {

    @Inject
    private UserControlPort userControlPort;

    @Inject
    private UserInfPort userInfPort;

    @Inject
    private SpecifiedUserInfPort specifiedUserInfPort;

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

    public void addClientToHotel(User client) throws UserException {
        try {
            userInfPort.get(client.getUsername());
            log.warning("Client %s already exists".formatted(client.getUsername()));
            throw new UserException("Client %s already exists".formatted(client.getUsername()));
        } catch (NoSuchElementException e) {
            userControlPort.add(client);
        }
    }

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

    public List<User> getAllModerators() {
        return specifiedUserInfPort.getAllModerators();
    }

    public List<User> getAllAdmins() {
        return specifiedUserInfPort.getAllAdmins();
    }

    public User getClient(String username) {
        return specifiedUserInfPort.getClient(username);
    }

    public User getModerator(String username) {
        return specifiedUserInfPort.getModerator(username);
    }

    public User getAdmin(String username) {
        return specifiedUserInfPort.getAdmin(username);
    }

}
