package adapter.aggregates.mapper;

import adapter.model.ReservationEnt;
import adapter.model.room.EquipmentTypeEnt;
import adapter.model.room.RoomEnt;
import adapter.model.user.UserEnt;
import domain.model.Reservation;
import domain.model.room.EquipmentType;
import domain.model.room.Room;
import domain.model.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelMapperTest {
    @Test
    void reservationToReservationEnt() {
        String username = "Miszczak";

        Room room = new Room(1, 34, 15., EquipmentType.BASIC);
        User user = new User(username);
        Reservation reservation = new Reservation(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), room, LocalDate.now(), LocalDate.now().plusDays(2),
                user, 15, true);
        ReservationEnt reservationEnt = ModelMapper.reservationToReservationEnt(reservation);
        assertEquals(reservationEnt.getReservationCost(), reservation.getReservationCost());
        assertEquals(reservationEnt.getEndTime(), reservation.getEndTime());
        assertEquals(reservationEnt.getBeginTime(), reservation.getBeginTime());
        assertEquals(reservationEnt.getId(), reservation.getId());
        RoomEnt roomEnt = reservationEnt.getRoom();
        UserEnt userEnt = reservationEnt.getUser();
        assertEquals(user.getUsername(), userEnt.getUsername());
        assertEquals(room.getRoomNumber(), roomEnt.getRoomNumber());
        assertEquals(room.getCapacity(), roomEnt.getCapacity());
        assertEquals(room.getPrice(), roomEnt.getPrice());
        assertEquals(room.getEquipmentType().toString(), roomEnt.getEquipmentTypeEnt().toString());
    }

    @Test
    void reservationEntToReservation() {
        UserEnt userEnt = new UserEnt("BartoszMiszczak");
        RoomEnt roomEnt = new RoomEnt(1, 10, 10.5, EquipmentTypeEnt.BASIC);

        ReservationEnt reservationEnt = new ReservationEnt(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), roomEnt, LocalDate.now(), LocalDate.now().plusDays(2),
                userEnt, 15d, true);
        Reservation reservation = ModelMapper.reservationEntToReservation(reservationEnt);
        assertEquals(reservationEnt.getReservationCost(), reservation.getReservationCost());
        assertEquals(reservationEnt.getEndTime(), reservation.getEndTime());
        assertEquals(reservationEnt.getBeginTime(), reservation.getBeginTime());
        assertEquals(reservationEnt.getId(), reservation.getId());
        Room room = reservation.getRoom();
        User user = reservation.getUser();
        assertEquals(user.getUsername(), userEnt.getUsername());
        assertEquals(room.getRoomNumber(), roomEnt.getRoomNumber());
        assertEquals(room.getCapacity(), roomEnt.getCapacity());
        assertEquals(room.getPrice(), roomEnt.getPrice());
        assertEquals(room.getEquipmentType().toString(), roomEnt.getEquipmentTypeEnt().toString());
    }

    @Test
    void userToUserEnt() {
        User user = new User("BartoszMiszczak");
        UserEnt userEnt = ModelMapper.userToUserEnt(user);
        assertEquals(user.getUsername(), userEnt.getUsername());
    }

    @Test
    void userEntToUser() {
        UserEnt userEnt = new UserEnt("BartoszMiszczak");
        User user = ModelMapper.userEntToUser(userEnt);
    }

    @Test
    void roomToRoomEnt() {
        Room room = new Room(1, 10, 10.5, EquipmentType.BASIC);
        RoomEnt roomEnt = ModelMapper.roomToRoomEnt(room);

        assertEquals(room.getRoomNumber(), roomEnt.getRoomNumber());
        assertEquals(room.getCapacity(), roomEnt.getCapacity());
        assertEquals(room.getPrice(), roomEnt.getPrice());
        assertEquals(room.getEquipmentType().toString(), roomEnt.getEquipmentTypeEnt().toString());
    }

    @Test
    void roomEntToRoom() {
        RoomEnt roomEnt = new RoomEnt(1, 10, 10.5, EquipmentTypeEnt.BASIC);
        Room room = ModelMapper.roomEntToRoom(roomEnt);

        assertEquals(roomEnt.getRoomNumber(), room.getRoomNumber());
        assertEquals(roomEnt.getCapacity(), room.getCapacity());
        assertEquals(roomEnt.getPrice(), room.getPrice());
        assertEquals(roomEnt.getEquipmentTypeEnt().toString(), room.getEquipmentType().toString());
    }
}