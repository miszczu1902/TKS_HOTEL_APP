package data.port.infrastructure;

import domain.model.user.User;

import java.util.List;

public interface UserRentInfPort {

    User get(Object element);

    List<User> find(Object... elements);
}
