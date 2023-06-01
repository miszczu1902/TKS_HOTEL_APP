package rabbit.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserCreatedEvent {
    @NonNull
    private String username;
    private Boolean isActive;

    private Boolean isModified;
    private Boolean isCreated;

    public UserCreatedEvent(@NonNull String username) {
        this.username = username;
        this.isModified = false;
        this.isCreated = true;
        this.isActive = true;
    }

    public UserCreatedEvent(@NonNull String username, Boolean isActive, Boolean isModified) {
        this.username = username;
        this.isActive = isActive;
        this.isModified = isModified;
        this.isCreated = false;
    }
}
