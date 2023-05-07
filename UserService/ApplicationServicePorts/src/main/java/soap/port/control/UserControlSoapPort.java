package soap.port.control;

import domain.exceptions.UserException;
import domain.model.user.User;

public interface UserControlSoapPort {

    void addUser(User user) throws UserException;

    void modifyUser(User user) throws UserException;

}
