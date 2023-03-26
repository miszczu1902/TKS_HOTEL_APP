package data.control;

import domain.model.user.User;

public interface UserControlPort {

    void add(User element);

    void remove(User... elements);

    void update(User... elements);

}
