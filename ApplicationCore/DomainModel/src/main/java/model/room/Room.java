package model.room;

import lombok.*;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"capacity", "price", "equipmentType", "version"})
public class Room implements Serializable {
    @NonNull
    private Integer roomNumber;

    @NonNull
    private Integer capacity;

    @NonNull
    private Double price;

    @NonNull
    private EquipmentType equipmentType;

    private Long version;
}
