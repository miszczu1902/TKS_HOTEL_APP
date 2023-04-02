package rest.controllers;

import adapter.RestReservationAdapter;
import adapter.RestRoomAdapter;
import adapter.RestUserAdapter;
import domain.model.Reservation;
import domain.model.room.Room;
import domain.model.user.User;
import rest.dto.ReservationDto;
import rest.dto.ReservationSelfDto;
import domain.exceptions.LogicException;
import domain.exceptions.ReservationException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Path("/reservations")
public class ReservationController {

    @Inject
    private RestReservationAdapter restReservationAdapter;

    @Inject
    private RestRoomAdapter restRoomAdapter;

    @Inject
    private RestUserAdapter restUserAdapter;

    @Context
    private SecurityContext securityContext;

    @POST
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reserveRoom(@Valid ReservationDto reservation) throws LogicException {
        try {
            restReservationAdapter.reserveRoom(new Reservation(
                            restRoomAdapter.getRoom(reservation.getRoomNumber()),
                            LocalDate.parse(reservation.getBeginTime()),
                            LocalDate.parse(reservation.getEndTime()),
                            restUserAdapter.getUser(reservation.getUsername())
                    )
            );
            return Response.created(URI.create("/Reservations/%s".formatted(reservation.getRoomNumber()))).build();
        } catch (ReservationException | ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @RolesAllowed({"USER"})
    @Path("/self")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reserveRoomByClient(@Valid ReservationSelfDto reservation) throws LogicException {
        try {
            String username = securityContext.getUserPrincipal().getName();
            Room room = restRoomAdapter.getRoom(reservation.getRoomNumber());
            User user = restUserAdapter.getUser(username);
            restReservationAdapter.reserveRoom(new Reservation(
                    room,
                    LocalDate.parse(reservation.getBeginTime()),
                    LocalDate.parse(reservation.getEndTime()),
                    user));
            return Response.created(URI.create("/Reservations/%s".formatted(reservation.getRoomNumber()))).build();
        } catch (ReservationException | ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @POST
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Path("/{reservationId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response endReserveRoom(@PathParam("reservationId") String reservationId) {
        try {
            restReservationAdapter.endReserveRoom(reservationId);
            return Response.ok().build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReservations() {
        return Response.ok().entity(restReservationAdapter.getAllReservations()).build();
    }

    @GET
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Path("/{reservationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReservationById(@PathParam("reservationId") String reservationId) {
        try {
            return Response.ok().entity(restReservationAdapter.getReservationById(reservationId)).build();
        } catch (ReservationException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), e.getMessage()).build();
        }
    }
}
