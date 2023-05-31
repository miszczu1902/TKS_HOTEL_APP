package services;

import data.port.control.RoomRentControlPort;
import data.port.infrastructure.ReservationRentInfPort;
import data.port.infrastructure.RoomRentInfPort;
import domain.exceptions.RoomException;
import domain.model.room.Room;
import service.port.control.RoomRentControlServicePort;
import service.port.infrasturcture.RoomRentInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@ApplicationScoped
@Transactional(dontRollbackOn = NoSuchElementException.class)
public class RoomRentService implements RoomRentInfServicePort, RoomRentControlServicePort {

    @Inject
    private RoomRentInfPort roomRentInfPort;

    @Inject
    private RoomRentControlPort roomRentControlPort;

    @Inject
    private ReservationRentInfPort reservationRentInfPort;

    private final Logger log = Logger.getLogger(getClass().getName());

    private boolean checkIfRoomCanBeRemoved(int roomNumber) {
        return reservationRentInfPort.getAll().stream()
                .noneMatch(reservation -> reservation.getRoom().getRoomNumber() == roomNumber);
    }

    @Override
    public void addRoom(Room room) throws RoomException {
        try {
            roomRentInfPort.get(room.getRoomNumber());
            log.warning("Room %s already exists".formatted(room.getRoomNumber()));
            throw new RoomException("Room %s already exists".formatted(room.getRoomNumber()));
        } catch (NoSuchElementException e) {
            roomRentControlPort.add(room);
        }
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRentInfPort.getAll();
    }

    @Override
    public Room getRoom(int roomNumber) {
        return roomRentInfPort.get(roomNumber);
    }

    @Override
    public void updateRoom(Room room) throws RoomException {
        try {
            Room roomToUpdate = roomRentInfPort.get(room.getRoomNumber());

            roomToUpdate.setCapacity(room.getCapacity());
            roomToUpdate.setPrice(room.getPrice());
            roomToUpdate.setEquipmentType(room.getEquipmentType());

            roomRentControlPort.update(roomToUpdate);

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
        Room room = roomRentInfPort.get(roomNumber);
        roomRentControlPort.remove(room);
    }
}