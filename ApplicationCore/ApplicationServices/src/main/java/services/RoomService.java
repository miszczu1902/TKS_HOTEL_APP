package services;

import data.control.RoomControlPort;
import data.infrastructure.ReservationInfPort;
import data.infrastructure.RoomInfPort;
import domain.exceptions.RoomException;
import jakarta.transaction.Transactional;
import domain.model.room.Room;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@ApplicationScoped
public class RoomService {

    @Inject
    private RoomInfPort roomInfPort;

    @Inject
    private RoomControlPort roomControlPort;

    @Inject
    private ReservationInfPort reservationInfPort;

    private final Logger log = Logger.getLogger(getClass().getName());

    private boolean checkIfRoomCanBeRemoved(int roomNumber) {
        return reservationInfPort.getAll().stream()
                .noneMatch(reservation -> reservation.getRoom().getRoomNumber() == roomNumber);
    }

//    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void addRoom(Room room) throws RoomException {
        try {
//            roomInfPort.getRepository().getEntityTransaction().begin();
            roomInfPort.get(room.getRoomNumber());
//            roomInfPort.getRepository().getEntityTransaction().rollback();
            log.warning("Room %s already exists".formatted(room.getRoomNumber()));
            throw new RoomException("Room %s already exists".formatted(room.getRoomNumber()));
        } catch (NoSuchElementException e) {
            roomControlPort.add(room);
//            roomInfPort.getRepository().getEntityTransaction().commit();
        }
    }

    public List<Room> getAllRooms() {
        return roomInfPort.getAll();
    }

    public Room getRoom(int roomNumber) {
        return roomInfPort.find(roomNumber).get(0);
    }

//    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void updateRoom(Room room) throws RoomException {
        try {
//            roomInfPort.getRepository().getEntityTransaction().begin();
            Room roomToUpdate = roomInfPort.get(room.getRoomNumber());

            roomToUpdate.setCapacity(room.getCapacity());
            roomToUpdate.setPrice(room.getPrice());
            roomToUpdate.setEquipmentType(room.getEquipmentType());

            roomControlPort.update(roomToUpdate);
//            roomControlPort.getRepository().getEntityTransaction().commit();

        } catch (NoSuchElementException e) {
//            roomInfPort.getRepository().getEntityTransaction().rollback();
            log.warning("Room %s doesn't exist".formatted(room.getRoomNumber()));
            throw new RoomException("Room doesn't exist");
        }
    }

//    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void removeRoom(int roomNumber) throws RoomException {
        try {
//            roomInfPort.getRepository().getEntityTransaction().begin();
            if (!checkIfRoomCanBeRemoved(roomNumber)) {
                log.warning("A given room %s couldn't be removed because it's reserved".formatted(roomNumber));
                throw new RoomException("A given room couldn't be removed because it's reserved");
            }
            Room room = roomInfPort.get(roomNumber);
            roomControlPort.remove(room);
//            roomInfPort.getRepository().getEntityTransaction().commit();
        } catch (Exception e) {
//            roomInfPort.getRepository().getEntityTransaction().rollback();
        }
    }
}