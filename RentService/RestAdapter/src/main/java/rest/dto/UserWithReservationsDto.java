package rest.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserWithReservationsDto {

    @NonNull
    @NotNull
    @Size(min = 6, max = 20)
    @Length(min = 5, message = "The field must be at least 5 characters")
    private String username;

    @NonNull
    @NotNull
    List<ReservationForUsersDto> reservations;
}
