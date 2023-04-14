package services;

import data.control.RoomControlPort;
import data.infrastructure.ReservationInfPort;
import data.infrastructure.RoomInfPort;
import domain.exceptions.RoomException;
import domain.model.room.Room;
import service.port.control.RoomControlServicePort;
import service.port.infrasturcture.RoomInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional(dontRollbackOn = NoSuchElementException.class)
public class RoomService implements RoomInfServicePort, RoomControlServicePort {

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

    @Override
    public void addRoom(Room room) throws RoomException {
        try {
            roomInfPort.get(room.getRoomNumber());
            log.warning("Room %s already exists".formatted(room.getRoomNumber()));
            throw new RoomException("Room %s already exists".formatted(room.getRoomNumber()));
        } catch (NoSuchElementException e) {
            roomControlPort.add(room);
        }
    }

    @Override
    public List<Room> getAllRooms() {
        return roomInfPort.getAll();
    }

    @Override
    public Room getRoom(int roomNumber) {
        return roomInfPort.get(roomNumber);
    }

    @Override
    public void updateRoom(Room room) throws RoomException {
        try {
            Room roomToUpdate = roomInfPort.get(room.getRoomNumber());

            roomToUpdate.setCapacity(room.getCapacity());
            roomToUpdate.setPrice(room.getPrice());
            roomToUpdate.setEquipmentType(room.getEquipmentType());

            roomControlPort.update(roomToUpdate);

        } catch (NoSuchElementException e) {
            log.warning("Room %s doesn't exist".formatted(room.getRoomNumber()));
            throw new RoomException("Room doesn't exist");
        }
    }

    @Override
    public void removeRoom(int roomNumber) throws RoomException {
        if (!checkIfRoomCanBeRemoved(roomNumber)) {
            log.warning("A given room %s couldn't be removed because it's reserved".formatted(roomNumber));
            throw new RoomException("A given room couldn't be removed because it's reserved");
        }
        Room room = roomInfPort.get(roomNumber);
        roomControlPort.remove(room);
    }
}