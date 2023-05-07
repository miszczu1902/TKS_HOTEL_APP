package mapper;

import domain.model.Role;
import domain.model.User;
import rest.dto.CreateUserDto;

public class RestMapper {

    public static User createUserDtoToUser(CreateUserDto user) {
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                Role.USER,
                true,
                user.getCity(),
                user.getStreet(),
                user.getStreetNumber(),
                user.getPostalCode());
    }

}
