package data.repositories.control;

import model.user.User;

public interface UserRemovePort {
    void remove(User... elements);

}
