package domain.model.dto;


import domain.model.room.EquipmentType;
import lombok.*;

@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class RoomDto {

    @NonNull
    private Integer roomNumber;

    @NonNull
    private Integer capacity;

    @NonNull
    private Double price;

    @NonNull
    private EquipmentType equipmentType = EquipmentType.BASIC;
}
