package adapter.aggregates;

import adapter.model.room.EquipmentTypeEnt;
import adapter.model.room.RoomEnt;
import adapter.aggregates.repo.Repository;
import data.repositories.control.*;
import data.repositories.infrastructure.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.dto.RoomDto;
import model.room.EquipmentType;
import model.room.Room;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
public class RoomRepositoryAdapter implements RoomAddPort, RoomRemovePort, RoomGetAllPort,
        RoomFindPort, RoomUpdatePort, RoomGetPort {

    @Inject
    private Repository repository;


    @Override
    public Room get(Object element) {
        return Optional.ofNullable(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<Room> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                .map(element -> repository.getEntityManager().find(RoomEnt.class, element))
                .map(this::roomEntToRoom)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<Room> getAll() {
        return repository.getEntityManager().createQuery("SELECT room FROM RoomEnt room", RoomEnt.class)
                .getResultList().stream()
                .map(this::roomEntToRoom).collect(Collectors.toList());
    }

    @Override
    public void add(Room element) {
        repository.getEntityManager().persist(roomToRoomEnt(element));
    }

    @Override
    public void remove(Room... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager().remove(roomToRoomEnt(element)));
    }

    @Override
    public void update(Room... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager().merge(roomToRoomEnt(element)));
    }

    private RoomEnt roomToRoomEnt(Room room) {
        return new RoomEnt(
                room.getRoomNumber(),
                room.getCapacity(),
                room.getPrice(),
                EquipmentTypeEnt.valueOf(room.getEquipmentType().toString()),
                room.getVersion()
        );
    }

    private Room roomEntToRoom(RoomEnt roomEnt) {
        return new Room(
                roomEnt.getRoomNumber(),
                roomEnt.getCapacity(),
                roomEnt.getPrice(),
                EquipmentType.valueOf(roomEnt.getEquipmentTypeEnt().toString()),
                roomEnt.getVersion()
        );
    }

    public List<RoomDto> convertToRoomDto(Room... rooms) {
        return Stream.of(rooms)
                .map(room -> new RoomDto(room.getRoomNumber(), room.getCapacity(), room.getPrice(),
                        room.getEquipmentType()))
                .collect(Collectors.toList());
    }

    public List<Room> convertToRoom(RoomDto... rooms) {
        return Stream.of(rooms)
                .map(oldRoom -> new Room(oldRoom.getRoomNumber(), oldRoom.getCapacity(), oldRoom.getPrice(),
                        oldRoom.getEquipmentType()))
                .collect(Collectors.toList());
    }

}
