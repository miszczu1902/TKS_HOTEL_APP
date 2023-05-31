package rabbit.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserCreatedEvent {
    @NonNull
    private String username;

    private Boolean isModified;
    private Boolean isCreated;

    public UserCreatedEvent(@NonNull String username) {
        this.username = username;
        this.isModified = false;
        this.isCreated = true;
    }
}