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
    public void updateUser(User user) {
        userControlPort.update(user);
    }

    @Override
    public User getUser(String username) {
        return userInfPort.get(username);
    }

    public void processMessage(String oldUsername, String newUsername) throws UserException {
        User user = userInfPort.get(oldUsername);
        if (user == null) {
            addUser(new User(oldUsername));
        } else {
            user.setUsername(newUsername);
            userControlPort.update(user);
        }
    }
}
