package adapter.aggregates.adapters;

import adapter.aggregates.mapper.ModelMapper;
import adapter.model.ReservationEnt;
import data.port.control.ReservationControlPort;
import data.port.infrastructure.ReservationInfPort;
import domain.model.Reservation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@ApplicationScoped
public class ReservationRepositoryAdapter implements ReservationInfPort, ReservationControlPort {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Reservation get(Object element) {
        return Optional.ofNullable(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<Reservation> getReservationsByRoomNumber(Integer roomNumber) {
        return Optional.of(entityManager
                        .createQuery("SELECT reservationEnt FROM ReservationEnt reservationEnt " +
                                "WHERE reservationEnt.room.roomNumber = :roomNumber", ReservationEnt.class)
                        .setParameter("roomNumber", roomNumber)
                        .getResultList().stream()
                        .map(ModelMapper::reservationEntToReservation)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Reservation> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                        .map(element -> entityManager.find(ReservationEnt.class, element))
                        .map(ModelMapper::reservationEntToReservation)
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Reservation> getAll() {
        return entityManager
                .createQuery("SELECT reservationEnt FROM ReservationEnt reservationEnt", ReservationEnt.class)
                .getResultList().stream()
                .map(ModelMapper::reservationEntToReservation)
                .collect(Collectors.toList());
    }

    @Override
    public void add(Reservation element) {
        ReservationEnt reservationEnt = new ReservationEnt(
                ModelMapper.roomToRoomEnt(element.getRoom()),
                element.getBeginTime(),
                element.getEndTime(),
                ModelMapper.userToUserEnt(element.getUser()),
                element.getReservationCost()
        );
        entityManager.persist(reservationEnt);
    }

    @Override
    public void remove(Reservation... elements) {
        Arrays.asList(elements).forEach(element -> entityManager
                .remove(ModelMapper.reservationToReservationEnt(element)));
    }

    @Override
    public void update(Reservation... elements) {
        Arrays.asList(elements).forEach(element -> entityManager
                .merge(ModelMapper.reservationToReservationEnt(element)));
    }

}
