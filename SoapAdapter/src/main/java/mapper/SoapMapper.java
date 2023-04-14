package mapper;

import domain.model.user.User;
import model.UserSoap;

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

}
