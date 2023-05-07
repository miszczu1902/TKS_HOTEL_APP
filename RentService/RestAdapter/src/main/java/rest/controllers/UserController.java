package rest.controllers;

import adapter.RestReservationAdapter;
import adapter.RestUserAdapter;
import com.nimbusds.jose.JOSEException;
import domain.exceptions.ChangePasswordException;
import domain.exceptions.JwsException;
import domain.exceptions.ReservationException;
import domain.exceptions.UserException;
import domain.model.Reservation;
import domain.model.Role;
import domain.model.user.User;
import mapper.RestMapper;
import org.jetbrains.annotations.NotNull;
import rest.auth.JwsGenerator;
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

    @Inject
    private JwsGenerator jwsGenerator;

    private final Logger log = Logger.getLogger(getClass().getName());

    @GET
    @PermitAll
    @Path("/{username}/jws")
    public Response getJws(@PathParam("username") String username) {
        try {
            String jws = getJwsForUser(username);
            return Response.noContent().header("ETag", jws).build();
        } catch (UserException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        } catch (JOSEException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }
    }

    @GET
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<GetUserDto> users = restUserAdapter.getAllUsers().stream()
                .map(user -> new GetUserDto(user.getUsername(), user.getFirstName(), user.getLastName(),
                        Optional.ofNullable(user.getCity()).orElse(""),
                        Optional.ofNullable(user.getStreet()).orElse(""),
                        Optional.ofNullable(user.getStreetNumber()).orElse(""),
                        Optional.ofNullable(user.getPostalCode()).orElse(""),
                        user.getRole().toString(), user.getIsActive()))
                .toList();
        return Response.ok().entity(users).build();
    }

    @GET
    @Path("/clients")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public Response getAllClients() {
        List<GetUserDto> users = restUserAdapter.getAllClients().stream()
                .map(user -> new GetUserDto(user.getUsername(), user.getFirstName(), user.getLastName(), user.getCity(),
                        user.getStreet(), user.getStreetNumber(), user.getPostalCode(), user.getRole().toString(), user.getIsActive())).toList();
        return Response.ok().entity(users).build();
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR"})
    public Response getUsersByUsername(@QueryParam("username") String pattern) {
        try {
            List<GetUserDto> users = restUserAdapter.getUsersByUsername(pattern).stream()
                    .map(user -> new GetUserDto(user.getUsername(), user.getFirstName(), user.getLastName(),
                            Optional.ofNullable(user.getCity()).orElse(""),
                            Optional.ofNullable(user.getStreet()).orElse(""),
                            Optional.ofNullable(user.getStreetNumber()).orElse(""),
                            Optional.ofNullable(user.getPostalCode()).orElse(""),
                            user.getRole().toString(),
                            user.getIsActive())).toList();
            return Response.ok().entity(users).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    public Response getUser(@PathParam("username") String username) {
        try {
            User user = restUserAdapter.getUser(username);
            return Response.ok().entity(new GetUserDto(user.getUsername(), user.getFirstName(), user.getLastName(),
                    user.getCity(), user.getStreet(), user.getStreetNumber(), user.getPostalCode(), user.getRole().toString(),
                    user.getIsActive())).build();
        } catch (NoSuchElementException e) {
            String message = "Client %s does not exist.".formatted(username);
            log.warning(message);
            return Response.status(Response.Status.NOT_FOUND.getStatusCode(), message).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("GUEST")
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
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@Valid UpdateUserDto client, @Context HttpServletRequest request) {
        try {
            String jws = request.getHeader("If-Match");
            if (jws == null) {
                throw new BadRequestException();
            }

            User user = restUserAdapter.getUser(client.getUsername());
            user.setUsername(client.getUsername());
            user.setFirstName(client.getFirstName());
            user.setLastName(client.getLastName());
            user.setCity(client.getCity());
            user.setStreet(client.getStreet());
            user.setStreetNumber(client.getStreetNumber());
            user.setPostalCode(client.getPostalCode());
            user.setRole(Role.valueOf(client.getRole()));
            restUserAdapter.updateUser(user, jws);

            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (UserException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        } catch (BadRequestException | JwsException | IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }
    }

    @POST
    @Path("/{username}/activate")
    @RolesAllowed({"ADMIN"})
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
    @RolesAllowed({"ADMIN"})
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
    @RolesAllowed({"ADMIN", "MODERATOR"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientWithReservations(@PathParam("username") String username) {
        return getInfoClientWithReservations(username);
    }

    @GET
    @Path("/reservations")
    @RolesAllowed({"USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClientWithReservations() {
        return getInfoClientWithReservations(securityContext.getCallerPrincipal().getName());
    }

    @PATCH
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @Path("/updatePassword")
    public Response changeUserPassword(@Valid @NotNull ChangePasswordDto passwordDto) {
        try {
            changePassword(passwordDto.getOldPassword(), passwordDto.getNewPassword());
            return Response.ok().build();
        } catch (UserException e) {
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        } catch (ChangePasswordException | ValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }
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
                    new UserWithReservationsDto(user.getUsername(), user.getFirstName(), user.getLastName(),
                            user.getRole(), user.getIsActive(), user.getCity(), user.getStreet(),
                            user.getStreetNumber(), user.getPostalCode(), reservations);
            return Response.ok().entity(userWithReservationsDto).build();
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist.".formatted(username));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        } catch (ReservationException e) {
            log.warning("Not found reservations for client %s".formatted(username));
            return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();
        }
    }

    private String getJwsForUser(String username) throws UserException, JOSEException {
        try {
            User user = restUserAdapter.getUser(username);
            return jwsGenerator.generateJws(user.getUsername());
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

    private void changePassword(String oldPassword, String newPassword) throws UserException, ChangePasswordException {
        String username = securityContext.getCallerPrincipal().getName();
        try {
            User user = restUserAdapter.getUser(username);
            if (!user.getPassword().equals(oldPassword)) {
                throw new ChangePasswordException("Old password is wrong.");
            }
            user.setPassword(newPassword);
            restUserAdapter.updateUser(user, getJwsForUser(user.getUsername()));
        } catch (NoSuchElementException e) {
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        } catch (JwsException | JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
