package model.user;

import lombok.*;
import model.Role;

import java.io.Serializable;

@Getter
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements Serializable {

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private Role role = Role.USER;

    @NonNull
    private Boolean isActive = true;

    private String city;


    private String street;

    private String streetNumber;

    private String postalCode;

    public User(@NonNull String username, @NonNull String password, @NonNull String firstName, @NonNull String lastName,
                String city, String street, String streetNumber, String postalCode) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.postalCode = postalCode;
    }
}
