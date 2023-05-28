package rest.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UpdateUserDto {

    @NonNull
    @NotEmpty
    @Size(min = 6, max = 20)
    private String username;
}
