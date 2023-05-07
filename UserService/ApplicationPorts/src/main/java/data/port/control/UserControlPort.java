package data.port.control;

import domain.model.User;

public interface UserControlPort {

    void add(User element);

    void update(User... elements);

}
