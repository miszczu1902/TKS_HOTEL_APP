package rest.controllers;

import adapter.RestReservationAdapter;
import adapter.RestRoomAdapter;
import adapter.RestUserAdapter;
import domain.exceptions.LogicException;
import domain.exceptions.ReservationException;
import domain.model.Reservation;
import domain.model.room.Room;
import domain.model.user.User;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import rest.dto.ReservationDto;
import rest.dto.ReservationSelfDto;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.time.LocalDate;

@Path("/reservations")
@Counted(name = "reservationCounter", description = "Reservation endpoint counter")
@Timed(name = "reservationCallTimer")
public class ReservationController {

    @Inject
    private RestReservationAdapter restReservationAdapter;

    @Inject
    private RestRoomAdapter restRoomAdapter;

    @Inject
    private RestUserAdapter restUserAdapter;

    @Context
    private SecurityContext securityContext;

    @GET
    @PermitAll
    @Path("/health-check")
    public Response checkHealthy() {
        return Response.ok().build();
    }

    @POST
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reserveRoom(@Valid ReservationDto reservation) throws LogicException {
        restReservationAdapter.reserveRoom(new Reservation(
                        restRoomAdapter.getRoom(reservation.getRoomNumber()),
                        LocalDate.parse(reservation.getBeginTime()),
                        LocalDate.parse(reservation.getEndTime()),
                        restUserAdapter.getUser(reservation.getUsername())
                )
        );
        return Response.created(URI.create("/Reservations/%s".formatted(reservation.getRoomNumber()))).build();
    }

    @POST
    @RolesAllowed({"USER"})
    @Path("/self")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reserveRoomByClient(@Valid ReservationSelfDto reservation) throws LogicException {
        String username = securityContext.getUserPrincipal().getName();
        Room room = restRoomAdapter.getRoom(reservation.getRoomNumber());
        User user = restUserAdapter.getUser(username);
        restReservationAdapter.reserveRoom(new Reservation(
                room,
                LocalDate.parse(reservation.getBeginTime()),
                LocalDate.parse(reservation.getEndTime()),
                user));
        return Response.created(URI.create("/Reservations/%s".formatted(reservation.getRoomNumber()))).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Path("/{reservationId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response endReserveRoom(@PathParam("reservationId") String reservationId) throws LogicException {
        restReservationAdapter.endReserveRoom(reservationId);
        return Response.ok().build();
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
    public Response getReservationById(@PathParam("reservationId") String reservationId) throws ReservationException {
        return Response.ok().entity(restReservationAdapter.getReservationById(reservationId)).build();
    }
}
