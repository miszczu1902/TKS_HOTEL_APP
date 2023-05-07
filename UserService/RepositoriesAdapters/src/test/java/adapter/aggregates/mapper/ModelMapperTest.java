package adapter.aggregates.mapper;

import adapter.model.room.EquipmentTypeEnt;
import adapter.model.room.RoomEnt;
import adapter.model.UserEnt;
import domain.model.Reservation;
import domain.model.room.EquipmentType;
import domain.model.room.Room;
import domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelMapperTest {

    @Test
    void userToUserEnt() {

        User user = new User("BartoszMiszczak", "123456", "Bartosz", "Miszczak", "Brzeziny", "Wolska", "12", "92-015");
        UserEnt userEnt = ModelMapper.userToUserEnt(user);

        assertEquals(user.getUsername(), userEnt.getUsername());
        assertEquals(user.getPassword(), userEnt.getPassword());
        assertEquals(user.getFirstName(), userEnt.getFirstName());
        assertEquals(user.getLastName(), userEnt.getLastName());
        assertEquals(user.getCity(), userEnt.getCity());
        assertEquals(user.getStreet(), userEnt.getStreet());
        assertEquals(user.getStreetNumber(), userEnt.getStreetNumber());
        assertEquals(user.getPostalCode(), userEnt.getPostalCode());
    }

    @Test
    void userEntToUser() {
        UserEnt userEnt = new UserEnt("BartoszMiszczak", "123456", "Bartosz", "Miszczak", "Brzeziny", "Wolska", "12", "92-015");
        User user = ModelMapper.userEntToUser(userEnt);
        assertEquals(userEnt.getUsername(), user.getUsername());
        assertEquals(userEnt.getPassword(), user.getPassword());
        assertEquals(userEnt.getFirstName(), user.getFirstName());
        assertEquals(userEnt.getLastName(), user.getLastName());
        assertEquals(userEnt.getCity(), user.getCity());
        assertEquals(userEnt.getStreet(), user.getStreet());
        assertEquals(userEnt.getStreetNumber(), user.getStreetNumber());
        assertEquals(userEnt.getPostalCode(), user.getPostalCode());

    }

}