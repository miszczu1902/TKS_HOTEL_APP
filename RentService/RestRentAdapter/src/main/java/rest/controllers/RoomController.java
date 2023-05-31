package rest.controllers;

import adapter.RestReservationAdapter;
import adapter.RestRoomAdapter;
import domain.exceptions.ReservationException;
import domain.exceptions.RoomException;
import domain.model.Reservation;
import domain.model.room.Room;
import mapper.RestMapper;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import rest.dto.ReservationForRoomsDto;
import rest.dto.RoomDto;
import rest.dto.RoomWithReservationDto;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Path("/rooms")
@Counted(name = "roomCounter", description = "Room endpoint counter")
@Timed(name = "roomCallTimer")
public class RoomController {

    @Inject
    private RestRoomAdapter restRoomAdapter;

    @Inject
    private RestReservationAdapter restReservationAdapter;

    @GET
    @PermitAll
    @Path("/health-check")
    public Response checkHealthy() {
        return Response.ok().build();
    }

    @POST
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRoom(@Valid RoomDto room) throws RoomException {
        restRoomAdapter.addRoom(RestMapper.roomDtoToRoom(room));
        return Response.created(URI.create("/rooms/%s".formatted(room.getRoomNumber()))).build();
    }

    @GET
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        return Response.ok().entity(restRoomAdapter.getAllRooms().stream()
                .map(RestMapper::roomToRoomDto).toList()).build();
    }

    @GET
    @Path("/{roomNumber}")
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomNumber") int roomNumber) {
        return Response.ok().entity(RestMapper.roomToRoomDto(restRoomAdapter.getRoom(roomNumber))).build();
    }

    @PUT
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateRoom(@Valid RoomDto room) throws RoomException {
        Room oldRoom = restRoomAdapter.getRoom(room.getRoomNumber());
        oldRoom.setRoomNumber(room.getRoomNumber());
        oldRoom.setCapacity(room.getCapacity());
        oldRoom.setPrice(room.getPrice());
        oldRoom.setEquipmentType(room.getEquipmentType());

        restRoomAdapter.updateRoom(oldRoom);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @RolesAllowed({"ADMIN"})
    @Path("/{roomNumber}")
    public Response removeRoom(@PathParam("roomNumber") int roomNumber) throws RoomException {
        Room room = restRoomAdapter.getRoom(roomNumber);
        restRoomAdapter.removeRoom(room);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/{roomNumber}/reservations")
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomWithReservations(@PathParam("roomNumber") int roomNumber) throws ReservationException {
        Room room = restRoomAdapter.getRoom(roomNumber);

        List<Reservation> reservationsForRoom = restReservationAdapter.getReservationsForRoom(roomNumber);
        List<ReservationForRoomsDto> reservations = reservationsForRoom.stream().
                map(reservation -> new ReservationForRoomsDto(reservation.getBeginTime(),
                        reservation.getEndTime(), reservation.getUser().getUsername()))
                .collect(Collectors.toList());
        RoomWithReservationDto roomWithReservationDto =
                new RoomWithReservationDto(room.getRoomNumber(), room.getCapacity(), room.getPrice(),
                        room.getEquipmentType(), reservations);

        return Response.ok().entity(roomWithReservationDto).build();
    }
}
