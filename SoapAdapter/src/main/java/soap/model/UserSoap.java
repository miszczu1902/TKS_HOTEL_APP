package soap.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserSoap {

    @NonNull
    @NotEmpty
    @Size(min = 6, max = 20)
    private String username;

    private String password;

    @NonNull
    @NotEmpty
    @Size(min = 3, max = 35)
    private String firstName;

    @NonNull
    @NotEmpty
    @Size(min = 3, max = 35)
    private String lastName;

    @Size(max = 40)
    private String city;

    @Size(max = 96)
    private String street;

    @Size(max = 30)
    private String streetNumber;

    @Pattern(regexp = "^\\d{2}-\\d{3}$")
    private String postalCode;

    @NotNull
    @NonNull
    private String role;

    @NotNull
    @NonNull
    private Boolean isActive;

    public UserSoap(@NonNull String username, @NonNull String firstName, @NonNull String lastName,
                    String city, String street, String streetNumber, String postalCode,
                    @NonNull String role, @NonNull Boolean isActive) {
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

    public UserSoap(@NonNull String username, @NonNull String password, @NonNull String firstName, @NonNull String lastName,
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
