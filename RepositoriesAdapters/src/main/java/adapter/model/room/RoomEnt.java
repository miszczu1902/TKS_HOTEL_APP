package adapter.model.room;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"capacity", "price", "equipmentType", "version"})
@Entity
@Getter
@Setter
@Table(name = "room")
public class RoomEnt implements Serializable {

    @Id
    @NonNull
    @Column(name = "room_number", nullable = false)
    private Integer roomNumber;

    @NonNull
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @NonNull
    @Column(name = "price", nullable = false)
    private Double price;

    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false)
    private EquipmentTypeEnt equipmentTypeEnt;

    @Version
    private Long version;
}
