package data.repositories.infrastructure;

import model.user.User;

import java.util.List;

public interface UserGetAllPort {
    List<User> getAll();
}
