package rest.controllers;

import adapter.RestUserAdapter;
import auth.JwsGenerator;
import com.nimbusds.jose.JOSEException;
import domain.exceptions.ChangePasswordException;
import domain.exceptions.JwsException;
import domain.exceptions.UserException;
import domain.model.Role;
import domain.model.User;
import mapper.RestMapper;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jetbrains.annotations.NotNull;
import rabbit.event.UserCreatedEvent;
import rabbit.message.MQProducer;
import rest.dto.ChangePasswordDto;
import rest.dto.CreateUserDto;
import rest.dto.GetUserDto;
import rest.dto.UpdateUserDto;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/users")
@Counted(name = "userServiceCounter", description = "User Service counter")
@Timed(name = "userServiceCallTimer")
public class UserController {

    @Inject
    private RestUserAdapter restUserAdapter;

    @Inject
    private SecurityContext securityContext;

    @Inject
    private JwsGenerator jwsGenerator;

    @Inject
    private MQProducer producer;

    @GET
    @PermitAll
    @Path("/health-check")
    public Response checkHealthy() {
        return Response.ok().build();
    }

    @GET
    @PermitAll
    @Path("/{username}/jws")
    public Response getJws(@PathParam("username") String username) throws JOSEException {
        String jws = getJwsForUser(username);
        return Response.noContent().header("ETag", jws).build();
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
        List<GetUserDto> users = restUserAdapter.getUsersByUsername(pattern).stream()
                .map(user -> new GetUserDto(user.getUsername(), user.getFirstName(), user.getLastName(),
                        Optional.ofNullable(user.getCity()).orElse(""),
                        Optional.ofNullable(user.getStreet()).orElse(""),
                        Optional.ofNullable(user.getStreetNumber()).orElse(""),
                        Optional.ofNullable(user.getPostalCode()).orElse(""),
                        user.getRole().toString(),
                        user.getIsActive())).toList();
        return Response.ok().entity(users).build();
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    public Response getUser(@PathParam("username") String username) {
        User user = restUserAdapter.getUser(username);
        return Response.ok().entity(new GetUserDto(user.getUsername(), user.getFirstName(), user.getLastName(),
                user.getCity(), user.getStreet(), user.getStreetNumber(), user.getPostalCode(), user.getRole().toString(),
                user.getIsActive())).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("GUEST")
    public Response addUser(@Valid CreateUserDto user) throws UserException {
        restUserAdapter.addUser(RestMapper.createUserDtoToUser(user));
        producer.produce(RestMapper.createUserDtoToUserCreatedEvent(user));
        return Response.created(URI.create("/users/%s".formatted(user.getUsername()))).build();
    }

    @PUT
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@Valid UpdateUserDto client, @Context HttpServletRequest request) throws JwsException, UserException {
        String jws = request.getHeader("If-Match");
        if (jws == null) throw new BadRequestException();

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
    }

    @POST
    @Path("/{username}/activate")
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@PathParam("username") String username) throws UserException {
        restUserAdapter.activateUser(username);
        producer.produce(new UserCreatedEvent(username, true, true));
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    @Path("/{username}/deactivate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateUser(@PathParam("username") String username) throws UserException {
        restUserAdapter.deactivateUser(username);
        producer.produce(new UserCreatedEvent(username, false, true));
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PATCH
    @RolesAllowed({"ADMIN", "MODERATOR", "USER"})
    @Path("/updatePassword")
    public Response changeUserPassword(@Valid @NotNull ChangePasswordDto passwordDto)
            throws ChangePasswordException, UserException, JOSEException, JwsException {
        changePassword(passwordDto.getOldPassword(), passwordDto.getNewPassword());
        return Response.ok().build();
    }

    private String getJwsForUser(String username) throws JOSEException {
        User user = restUserAdapter.getUser(username);
        return jwsGenerator.generateJws(user.getUsername());
    }

    private void changePassword(String oldPassword, String newPassword)
            throws UserException, ChangePasswordException, JOSEException, JwsException {
        String username = securityContext.getCallerPrincipal().getName();
        User user = restUserAdapter.getUser(username);
        if (!user.getPassword().equals(oldPassword)) {
            throw new ChangePasswordException("Old password is wrong.");
        }
        user.setPassword(newPassword);
        restUserAdapter.updateUser(user, getJwsForUser(user.getUsername()));
    }

}
