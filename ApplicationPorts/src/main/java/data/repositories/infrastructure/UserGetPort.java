package data.repositories.infrastructure;

import model.user.User;

public interface UserGetPort {
    User get(Object element);
}
