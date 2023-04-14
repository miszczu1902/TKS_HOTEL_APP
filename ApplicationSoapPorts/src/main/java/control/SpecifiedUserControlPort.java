package control;

import domain.model.user.User;

public interface SpecifiedUserControlPort {

    void addUser(User user);

    void modifyUser(User user);

}
