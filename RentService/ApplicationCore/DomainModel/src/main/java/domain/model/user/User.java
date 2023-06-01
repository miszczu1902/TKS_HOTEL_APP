package domain.model.user;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @NonNull
    private String username;
    private Boolean isActive;

    public User(@NonNull String username) {
        this.username = username;
        this.isActive = true;
    }
}
