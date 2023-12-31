package data.port.infrastructure;

import domain.model.User;

import java.util.List;

public interface UserInfPort {

    User get(Object element);

    List<User> getAll();

    List<User> find(Object... elements);

    List<User> getAllClients();

    List<User> getByUsername(String pattern);

    User getByUsernameAndPasswd(String username, String password);

}
