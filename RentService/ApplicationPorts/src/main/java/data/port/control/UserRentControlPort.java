package data.port.control;

import domain.model.user.User;

public interface UserRentControlPort {

    void add(User element);

    void update(User... elements);

}
