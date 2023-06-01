package adapter.model.user;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEnt implements Serializable {
    @Id
    @NonNull
    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public UserEnt(@NonNull String username) {
        this.username = username;
        this.isActive = true;
    }
}
