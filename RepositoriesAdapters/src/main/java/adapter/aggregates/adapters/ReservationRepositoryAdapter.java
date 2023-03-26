package adapter.aggregates.adapters;

import adapter.aggregates.mapper.ModelMapper;
import adapter.aggregates.repo.Repository;
import adapter.model.ReservationEnt;
import data.control.ReservationControlPort;
import data.infrastructure.ReservationInfPort;
import lombok.Getter;
import lombok.NoArgsConstructor;
import domain.model.Reservation;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ReservationRepositoryAdapter implements ReservationInfPort, ReservationControlPort {

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
                .map(ModelMapper::reservationEntToReservation)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<Reservation> getAll() {
        return repository.getEntityManager().createQuery("SELECT reservation FROM ReservationEnt reservation", ReservationEnt.class)
                .getResultList().stream()
                .map(ModelMapper::reservationEntToReservation).collect(Collectors.toList());
    }

    public List<Reservation> getReservationsForClient(String username) {
        return repository.getEntityManager().createQuery("SELECT reservation FROM ReservationEnt reservation WHERE reservation.user.username = :username", ReservationEnt.class)
                .setParameter("username", username)
                .getResultList().stream()
                .map(ModelMapper::reservationEntToReservation).collect(Collectors.toList());
    }

    @Override
    public void add(Reservation element) {
        repository.getEntityManager().persist(element);
    }

    @Override
    public void remove(Reservation... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager()
                .remove(ModelMapper.reservationToReservationEnt(element)));
    }

    @Override
    public void update(Reservation... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager()
                .merge(ModelMapper.reservationToReservationEnt(element)));
    }

    public List<Reservation> getReservationsForRoom(int roomNumber) {
        return repository.getEntityManager().createQuery("SELECT reservation FROM ReservationEnt reservation WHERE reservation.room.roomNumber = :roomNumber", ReservationEnt.class)
                .setParameter("roomNumber", roomNumber)
                .getResultList().stream()
                .map(ModelMapper::reservationEntToReservation).collect(Collectors.toList());
    }

}