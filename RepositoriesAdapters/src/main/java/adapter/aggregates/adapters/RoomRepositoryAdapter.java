package adapter.aggregates.adapters;

import adapter.aggregates.mapper.ModelMapper;
import adapter.aggregates.repo.Repository;
import adapter.model.room.RoomEnt;
import data.control.RoomControlPort;
import data.infrastructure.RoomInfPort;
import lombok.Getter;
import lombok.NoArgsConstructor;
import domain.model.room.Room;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class RoomRepositoryAdapter implements RoomInfPort, RoomControlPort {

    @Inject
    private Repository repository;


    @Override
    public Room get(Object element) {
        return Optional.of(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<Room> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                .map(element -> repository.getEntityManager().find(RoomEnt.class, element))
                .map(ModelMapper::roomEntToRoom)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<Room> getAll() {
        return repository.getEntityManager().createQuery("SELECT room FROM RoomEnt room", RoomEnt.class)
                .getResultList().stream()
                .map(ModelMapper::roomEntToRoom)
                .collect(Collectors.toList());
    }

    @Override
    public void add(Room element) {
        repository.getEntityManager().persist(ModelMapper.roomToRoomEnt(element));
    }

    @Override
    public void remove(Room... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager().remove(ModelMapper.roomToRoomEnt(element)));
    }

    @Override
    public void update(Room... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager().merge(ModelMapper.roomToRoomEnt(element)));
    }

}
