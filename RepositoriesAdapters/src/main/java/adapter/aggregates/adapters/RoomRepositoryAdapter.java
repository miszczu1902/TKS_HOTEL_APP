package adapter.aggregates.adapters;

import adapter.aggregates.mapper.ModelMapper;
import adapter.aggregates.repo.Repository;
import adapter.model.room.RoomEnt;
import data.control.RoomControlPort;
import data.infrastructure.RoomInfPort;
import lombok.Getter;
import lombok.NoArgsConstructor;
import domain.model.room.Room;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
public class RoomRepositoryAdapter implements RoomInfPort, RoomControlPort {

//    @Inject
//    private Repository repository;

    @PersistenceContext(unitName = "TEST_HOTEL")
    private EntityManager entityManager;


    @Override
    public Room get(Object element) {
        return Optional.of(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<Room> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                .map(element -> entityManager.find(RoomEnt.class, element))
                .map(ModelMapper::roomEntToRoom)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<Room> getAll() {
        return entityManager.createQuery("SELECT roomEnt FROM RoomEnt roomEnt", RoomEnt.class)
                .getResultList().stream()
                .map(ModelMapper::roomEntToRoom)
                .collect(Collectors.toList());
    }

    @Override
    public void add(Room element) {
        entityManager.persist(ModelMapper.roomToRoomEnt(element));
    }

    @Override
    public void remove(Room... elements) {
        Arrays.asList(elements).forEach(element -> entityManager.remove(ModelMapper.roomToRoomEnt(element)));
    }

    @Override
    public void update(Room... elements) {
        Arrays.asList(elements).forEach(element -> entityManager.merge(ModelMapper.roomToRoomEnt(element)));
    }

}
