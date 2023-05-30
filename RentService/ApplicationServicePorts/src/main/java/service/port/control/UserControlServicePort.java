package service.port.control;

import domain.exceptions.UserException;
import domain.model.user.User;

public interface UserControlServicePort {
    void addUser(User user) throws UserException;

    void updateUser(User user) throws UserException;
}
