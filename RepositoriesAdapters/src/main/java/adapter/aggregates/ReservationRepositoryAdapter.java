package adapter.aggregates;

import adapter.model.ReservationEnt;
import adapter.aggregates.repo.Repository;
import adapter.model.room.EquipmentTypeEnt;
import adapter.model.room.RoomEnt;
import adapter.model.user.UserEnt;
import data.repositories.control.ReservationAddPort;
import data.repositories.control.ReservationRemovePort;
import data.repositories.control.ReservationUpdatePort;
import data.repositories.infrastructure.ReservationFindPort;
import data.repositories.infrastructure.ReservationGetAllPort;
import data.repositories.infrastructure.ReservationGetPort;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.Reservation;
import model.room.EquipmentType;
import model.room.Room;
import model.user.User;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReservationRepositoryAdapter implements ReservationAddPort, ReservationRemovePort,
        ReservationUpdatePort, ReservationFindPort,
        ReservationGetAllPort, ReservationGetPort {

    @Inject
    private Repository repository;

    @Override
    public Reservation get(Object element) {
        return Optional.ofNullable(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<Reservation> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                .map(element -> repository.getEntityManager().find(ReservationEnt.class, element))
                .map(this::reservationEntToReservation)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<Reservation> getAll() {
        return repository.getEntityManager().createQuery("SELECT reservation FROM ReservationEnt reservation", ReservationEnt.class)
                .getResultList().stream()
                .map(this::reservationEntToReservation).collect(Collectors.toList());
    }

    public List<Reservation> getReservationsForClient(String username) {
        return repository.getEntityManager().createQuery("SELECT reservation FROM ReservationEnt reservation WHERE reservation.user.username = :username", ReservationEnt.class)
                .setParameter("username", username)
                .getResultList().stream()
                .map(this::reservationEntToReservation).collect(Collectors.toList());
    }

    public List<Reservation> getReservationsForRoom(int roomNumber) {
        return repository.getEntityManager().createQuery("SELECT reservation FROM ReservationEnt reservation WHERE reservation.room.roomNumber = :roomNumber", ReservationEnt.class)
                .setParameter("roomNumber", roomNumber)
                .getResultList().stream()
                .map(this::reservationEntToReservation).collect(Collectors.toList());
    }

    @Override
    public void add(Reservation element) {
        repository.getEntityManager().persist(element);
    }

    @Override
    public void remove(Reservation... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager().remove(reservationToReservationEnt(element)));
    }

    @Override
    public void update(Reservation... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager().merge(reservationToReservationEnt(element)));
    }

    private ReservationEnt reservationToReservationEnt(Reservation reservation) {
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

    private Reservation reservationEntToReservation(ReservationEnt reservationEnt) {
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

}
