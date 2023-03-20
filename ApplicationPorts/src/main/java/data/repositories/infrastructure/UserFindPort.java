package data.repositories.infrastructure;

import model.user.User;

import java.util.List;

public interface UserFindPort {
    List<User> find(Object... elements);
}
