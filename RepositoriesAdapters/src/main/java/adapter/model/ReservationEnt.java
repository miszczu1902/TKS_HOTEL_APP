package adapter.model;

import adapter.model.room.RoomEnt;
import adapter.model.user.UserEnt;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
public class ReservationEnt implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reservation_id")
    private UUID id;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "room_number", nullable = false)
    private RoomEnt room;

    @NonNull
    @Column(name = "begin_time", nullable = false)
    private LocalDate beginTime;

    @NonNull
    @Column(name = "end_time", nullable = false)
    private LocalDate endTime;

    @NonNull
    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private UserEnt user;

    @NonNull
    @NotNull
    @Column(name = "reservation_cost", nullable = false)
    private Double reservationCost;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

}
