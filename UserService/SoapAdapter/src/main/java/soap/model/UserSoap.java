package soap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSoap {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String city;
    private String street;
    private String streetNumber;
    private String postalCode;
    private String role;
    private Boolean isActive;

    public UserSoap(String username, String firstName, String lastName,
                    String city, String street, String streetNumber, String postalCode,
                     String role, Boolean isActive) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.postalCode = postalCode;
        this.role = role;
        this.isActive = isActive;
    }

    public UserSoap(String username, String password, String firstName, String lastName,
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
