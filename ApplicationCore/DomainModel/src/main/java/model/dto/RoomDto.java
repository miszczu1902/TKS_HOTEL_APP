package model.dto;


import lombok.*;
import model.room.EquipmentType;

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
