package adapter.model.room;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"capacity", "price", "equipmentTypeEnt", "version"})
@Entity
@Getter
@Setter
public class RoomEnt implements Serializable {

    @Id
    @NonNull
    @NotNull
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
