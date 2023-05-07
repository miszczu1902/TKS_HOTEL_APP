package service.port.control;

import domain.exceptions.JwsException;
import domain.exceptions.UserException;
import domain.model.User;

public interface UserControlServicePort {

    void addUser(User user) throws UserException;

    void updateUser(User user, String jws) throws UserException, JwsException;

    void activateUser(String username) throws UserException;

    void deactivateUser(String username) throws UserException;
}
