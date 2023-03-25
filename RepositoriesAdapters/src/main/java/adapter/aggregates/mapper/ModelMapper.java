package adapter.aggregates.mapper;

import adapter.model.ReservationEnt;
import adapter.model.room.EquipmentTypeEnt;
import adapter.model.room.RoomEnt;
import adapter.model.user.UserEnt;
import model.Reservation;
import model.dto.RoomDto;
import model.room.EquipmentType;
import model.room.Room;
import model.user.User;

public class ModelMapper {

    public static ReservationEnt reservationToReservationEnt(Reservation reservation) {
        Room room = reservation.getRoom();
        User user = reservation.getUser();
        return new ReservationEnt(
                reservation.getId(),
                new RoomEnt(room.getRoomNumber(), room.getCapacity(), room.getPrice(), EquipmentTypeEnt.valueOf(room.getEquipmentType().toString())),
                reservation.getBeginTime(),
                reservation.getEndTime(),
                new UserEnt(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(),
                        user.getCity(), user.getStreet(), user.getStreetNumber(), user.getPostalCode()),
                reservation.getReservationCost(),
                reservation.isActive());

    }

    public static Reservation reservationEntToReservation(ReservationEnt reservationEnt) {
        RoomEnt room = reservationEnt.getRoom();
        UserEnt user = reservationEnt.getUser();
        return new Reservation(
                reservationEnt.getId(),
                new Room(room.getRoomNumber(), room.getCapacity(), room.getPrice(), EquipmentType.valueOf(room.getEquipmentTypeEnt().toString())),
                reservationEnt.getBeginTime(),
                reservationEnt.getEndTime(),
                new User(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(),
                        user.getCity(), user.getStreet(), user.getStreetNumber(), user.getPostalCode()),
                reservationEnt.getReservationCost(),
                reservationEnt.isActive());

    }

    public static UserEnt userToUserEnt(User user) {
        return new UserEnt(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(),
                user.getCity(), user.getStreet(), user.getStreetNumber(), user.getPostalCode());
    }

    public static User userEntToUser(UserEnt userEnt) {
        return new User(userEnt.getUsername(), userEnt.getPassword(), userEnt.getFirstName(), userEnt.getLastName(),
                userEnt.getCity(), userEnt.getStreet(), userEnt.getStreetNumber(), userEnt.getPostalCode());
    }

    public static RoomEnt roomToRoomEnt(Room room) {
        return new RoomEnt(
                room.getRoomNumber(),
                room.getCapacity(),
                room.getPrice(),
                EquipmentTypeEnt.valueOf(room.getEquipmentType().toString()),
                room.getVersion()
        );
    }

    public static Room roomEntToRoom(RoomEnt roomEnt) {
        return new Room(
                roomEnt.getRoomNumber(),
                roomEnt.getCapacity(),
                roomEnt.getPrice(),
                EquipmentType.valueOf(roomEnt.getEquipmentTypeEnt().toString()),
                roomEnt.getVersion()
        );
    }

    public static RoomDto convertToRoomDto(Room room) {
        return new RoomDto(room.getRoomNumber(), room.getCapacity(), room.getPrice(),
                        room.getEquipmentType());
    }

    public static Room convertToRoom(RoomDto room) {
        return new Room(room.getRoomNumber(), room.getCapacity(), room.getPrice(),
                        room.getEquipmentType());
    }
}
