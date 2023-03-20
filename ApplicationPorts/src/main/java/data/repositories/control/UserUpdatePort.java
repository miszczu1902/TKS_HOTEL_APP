package data.repositories.control;

import model.user.User;

public interface UserUpdatePort {
    void update(User... elements);
}
