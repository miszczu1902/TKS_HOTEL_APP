package adapter.aggregates.mapper;

import adapter.model.RoleEnt;
import adapter.model.UserEnt;
import domain.model.Role;
import domain.model.User;

import java.util.NoSuchElementException;

public class ModelMapper {

    public static UserEnt userToUserEnt(User user) {
        try {
            return new UserEnt(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(),
                    RoleEnt.valueOf(user.getRole().toString()), user.getIsActive(),
                    user.getCity(), user.getStreet(), user.getStreetNumber(), user.getPostalCode());
        } catch (NullPointerException e) {
            throw new NoSuchElementException(e);
        }
    }

    public static User userEntToUser(UserEnt userEnt) {
        try {
            return new User(userEnt.getUsername(), userEnt.getPassword(), userEnt.getFirstName(), userEnt.getLastName(),
                    Role.valueOf(userEnt.getRole().toString()), userEnt.getIsActive(),
                    userEnt.getCity(), userEnt.getStreet(), userEnt.getStreetNumber(), userEnt.getPostalCode());
        } catch (NullPointerException e) {
            throw new NoSuchElementException(e);
        }
    }

}
