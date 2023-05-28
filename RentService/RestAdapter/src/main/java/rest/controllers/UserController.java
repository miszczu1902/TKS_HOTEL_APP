package rest.controllers;

import adapter.RestReservationAdapter;
import adapter.RestUserAdapter;
import com.nimbusds.jose.JOSEException;
import domain.exceptions.ReservationException;
import domain.exceptions.UserException;
import domain.model.Reservation;
import domain.model.user.User;
import mapper.RestMapper;
import org.jetbrains.annotations.NotNull;
//import rest.auth.JwsGenerator;
import rest.dto.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/users")
public class UserController {

    @Inject
    private RestUserAdapter restUserAdapter;

    @Inject
    private RestReservationAdapter restReservationAdapter;

    @Inject
    private SecurityContext securityContext;


    private final Logger log = Logger.getLogger(getClass().getName());

    @GET
//    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<GetUserDto> users = restUserAdapter.getAllUsers().stream()
                .map(user -> new GetUserDto(user.getUsername()))
                .toList();
        return Response.ok().entity(users).build();
    }

    @GET
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed({"ADMIN", "MODERATOR"})
    public Response getAllClients() {
        List<GetUserDto> users = restUserAdapter.getAllClients().stream()
                .map(user -> new GetUserDto(user.getUsername())).toList();
        return Response.ok().entity(users).build();
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed({"ADMIN", "MODERATOR"})
    public Response getUsersByUsername(@QueryParam("username") String pattern) {
        try {
            List<GetUserDto> users = restUserAdapter.getUsersByUsername(pattern).stream()
                    .map(user -> new GetUserDto(user.getUsername())).toList();
            return Response.ok().entity(users).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    public Response getUser(@PathParam("username") String username) {
        try {
            User user = restUserAdapter.getUser(username);
            return Response.ok().entity(new GetUserDto(user.getUsername())).build();
        } catch (NoSuchElementException e) {
            String message = "Client %s does not exist.".formatted(username);
            log.warning(message);
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), message).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
//    @RolesAllowed("GUEST")
    public Response addUser(@Valid CreateUserDto user) {
        try {
            restUserAdapter.addUser(RestMapper.createUserDtoToUser(user));
            return Response.created(URI.create("/users/%s".formatted(user.getUsername()))).build();
        } catch (UserException e) {
            return Response.status(Response.Status.CONFLICT.getStatusCode()).build();
        } catch (ValidationException e) {
            String message = "User validation failed for user: %s".formatted(user.toString());
            log.warning(message);
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), message).build();
        }
    }

    @PUT
//    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@Valid UpdateUserDto client, @Context HttpServletRequest request) {
        try {
            String jws = request.getHeader("If-Match");
            if (jws == null) {
                throw new BadRequestException();
            }

            User user = restUserAdapter.getUser(client.getUsername());
            user.setUsername(client.getUsername());
            restUserAdapter.updateUser(user);

            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (UserException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        } catch (BadRequestException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }
    }

    @POST
    @Path("/{username}/activate")
//    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@PathParam("username") String username) {
        try {
            restUserAdapter.activateUser(username);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (NoSuchElementException | UserException e) {
            log.warning("User %s does not exist.".formatted(username));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }
    }

    @POST
//    @RolesAllowed({"ADMIN"})
    @Path("/{username}/deactivate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateUser(@PathParam("username") String username) {
        try {
            restUserAdapter.deactivateUser(username);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (NoSuchElementException | UserException e) {
            log.warning("Client %s does not exist.".formatted(username));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }
    }

    @GET
    @Path("/{username}/reservations")
//    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientWithReservations(@PathParam("username") String username) {
        return getInfoClientWithReservations(username);
    }

    @GET
    @Path("/reservations")
//    @RolesAllowed({"USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientWithReservations() {
        return getInfoClientWithReservations(securityContext.getCallerPrincipal().getName());
    }

    private Response getInfoClientWithReservations(String username) {
        try {
            User user = restUserAdapter.getUser(username);
            List<Reservation> reservation = restReservationAdapter.getReservationsForClient(username);
            List<ReservationForUsersDto> reservations = reservation.stream().
                    map(resForUser -> new ReservationForUsersDto(
                            resForUser.getRoom().getRoomNumber(),
                            resForUser.getBeginTime(),
                            resForUser.getEndTime()))
                    .collect(Collectors.toList());
            UserWithReservationsDto userWithReservationsDto =
                    new UserWithReservationsDto(user.getUsername(), reservations);
            return Response.ok().entity(userWithReservationsDto).build();
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist.".formatted(username));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        } catch (ReservationException e) {
            log.warning("Not found reservations for client %s".formatted(username));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }
    }
}
