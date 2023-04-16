package soap.mapper;

import domain.model.Role;
import domain.model.user.User;
import soap.model.UserSoap;

public class SoapMapper {

    public static UserSoap userToUserSoap(User user) {
        return new UserSoap(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getCity(),
                user.getStreet(),
                user.getStreetNumber(),
                user.getPostalCode(),
                user.getRole().toString(),
                user.getIsActive()
        );
    }

    public static User userSoapToUser(UserSoap user) {
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                Role.valueOf(user.getRole()),
                user.getIsActive(),
                user.getCity(),
                user.getStreet(),
                user.getStreetNumber(),
                user.getPostalCode()
        );
    }

}
