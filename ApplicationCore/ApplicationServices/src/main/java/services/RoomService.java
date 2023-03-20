package services;

import adapter.aggregates.ReservationRepositoryAdapter;
import adapter.aggregates.RoomRepositoryAdapter;
import exceptions.RoomException;
import model.dto.RoomDto;
import model.room.Room;

import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

public class RoomService {

    @Inject
    private RoomRepositoryAdapter roomRepository;

    @Inject
    private ReservationRepositoryAdapter reservationRepository;

    private final Logger log = Logger.getLogger(getClass().getName());

    private boolean checkIfRoomCanBeRemoved(int roomNumber) {
        return reservationRepository.getAll().stream()
                .noneMatch(reservation -> reservation.getRoom().getRoomNumber() == roomNumber);
    }

    public void addRoom(RoomDto room) throws RoomException {
        try {
            roomRepository.getRepository().getEntityTransaction().begin();
            roomRepository.get(room.getRoomNumber());
            roomRepository.getRepository().getEntityTransaction().rollback();
            log.warning("Room %s already exists".formatted(room.getRoomNumber()));
            throw new RoomException("Room %s already exists".formatted(room.getRoomNumber()));
        } catch (NoSuchElementException e) {
            Room roomToAdd = roomRepository.convertToRoom(room).get(0);
            roomRepository.add(roomToAdd);
            roomRepository.getRepository().getEntityTransaction().commit();
        }
    }

    public List<RoomDto> getAllRooms() {
        return roomRepository.convertToRoomDto(roomRepository.getAll().toArray(new Room[0]));
    }

    public RoomDto getRoom(int roomNumber) {
        return roomRepository.convertToRoomDto(roomRepository.get(roomNumber)).get(0);
    }

    public void updateRoom(RoomDto room) throws RoomException {
        try {
            roomRepository.getRepository().getEntityTransaction().begin();
            Room roomToUpdate = roomRepository.get(room.getRoomNumber());

            roomToUpdate.setCapacity(room.getCapacity());
            roomToUpdate.setPrice(room.getPrice());
            roomToUpdate.setEquipmentType(room.getEquipmentType());

            roomRepository.update(roomToUpdate);
            roomRepository.getRepository().getEntityTransaction().commit();

        } catch (NoSuchElementException e) {
            roomRepository.getRepository().getEntityTransaction().rollback();
            log.warning("Room %s doesn't exist".formatted(room.getRoomNumber()));
            throw new RoomException("Room doesn't exist");
        }
    }

    public void removeRoom(int roomNumber) throws RoomException {
        try {
            roomRepository.getRepository().getEntityTransaction().begin();
            if (!checkIfRoomCanBeRemoved(roomNumber)) {
                log.warning("A given room %s couldn't be removed because it's reserved".formatted(roomNumber));
                throw new RoomException("A given room couldn't be removed because it's reserved");
            }
            Room room = roomRepository.get(roomNumber);
            roomRepository.remove(room);
            roomRepository.getRepository().getEntityTransaction().commit();
        } catch (Exception e) {
            roomRepository.getRepository().getEntityTransaction().rollback();
        }
    }
}