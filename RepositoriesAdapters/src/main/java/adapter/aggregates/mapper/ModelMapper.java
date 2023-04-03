package adapter.aggregates.mapper;

import adapter.model.ReservationEnt;
import adapter.model.room.EquipmentTypeEnt;
import adapter.model.room.RoomEnt;
import adapter.model.user.UserEnt;
import domain.model.Reservation;
import domain.model.room.EquipmentType;
import domain.model.room.Room;
import domain.model.user.User;

import java.util.NoSuchElementException;

public class ModelMapper {

    public static ReservationEnt reservationToReservationEnt(Reservation reservation) {
        try {
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
        } catch (NullPointerException e) {
            throw new NoSuchElementException(e);
        }
    }

    public static Reservation reservationEntToReservation(ReservationEnt reservationEnt) {
        try {
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
        } catch (NullPointerException e) {
            throw new NoSuchElementException(e);
        }
    }

    public static UserEnt userToUserEnt(User user) {
        try {
            return new UserEnt(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(),
                    user.getCity(), user.getStreet(), user.getStreetNumber(), user.getPostalCode());
        } catch (NullPointerException e) {
            throw new NoSuchElementException(e);
        }
    }

    public static User userEntToUser(UserEnt userEnt) {
        try {
            return new User(userEnt.getUsername(), userEnt.getPassword(), userEnt.getFirstName(), userEnt.getLastName(),
                    userEnt.getCity(), userEnt.getStreet(), userEnt.getStreetNumber(), userEnt.getPostalCode());
        } catch (NullPointerException e) {
            throw new NoSuchElementException(e);
        }
    }

    public static RoomEnt roomToRoomEnt(Room room) {
        try {
            return new RoomEnt(
                    room.getRoomNumber(),
                    room.getCapacity(),
                    room.getPrice(),
                    EquipmentTypeEnt.valueOf(room.getEquipmentType().toString()),
                    room.getVersion()
            );
        } catch (NullPointerException e) {
            throw new NoSuchElementException(e);
        }
    }

    public static Room roomEntToRoom(RoomEnt roomEnt) {
        try {
            return new Room(
                    roomEnt.getRoomNumber(),
                    roomEnt.getCapacity(),
                    roomEnt.getPrice(),
                    EquipmentType.valueOf(roomEnt.getEquipmentTypeEnt().toString()),
                    roomEnt.getVersion()
            );
        } catch (NullPointerException e) {
            throw new NoSuchElementException(e);
        }
    }

}
