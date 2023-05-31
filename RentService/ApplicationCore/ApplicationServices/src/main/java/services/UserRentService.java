package services;

import data.port.control.UserRentControlPort;
import data.port.infrastructure.UserRentInfPort;
import domain.exceptions.UserException;
import domain.model.user.User;
import service.port.control.UserRentControlServicePort;
import service.port.infrasturcture.UserRentInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional(dontRollbackOn = NoSuchElementException.class)
public class UserRentService implements UserRentControlServicePort, UserRentInfServicePort {

    @Inject
    private UserRentControlPort userRentControlPort;

    @Inject
    private UserRentInfPort userRentInfPort;

    private final Logger log = Logger.getLogger(getClass().getName());

    @Override
    public void addUser(User user) throws UserException {
        try {
            userRentInfPort.get(user.getUsername());
            log.warning("Client %s already exists".formatted(user.getUsername()));
            throw new UserException("Client %s already exists".formatted(user.getUsername()));
        } catch (NoSuchElementException e) {
            userRentControlPort.add(user);
        }
    }

    @Override
    public void updateUser(User user) {
        userRentControlPort.update(user);
    }

    @Override
    public User getUser(String username) {
        return userRentInfPort.get(username);
    }
}
