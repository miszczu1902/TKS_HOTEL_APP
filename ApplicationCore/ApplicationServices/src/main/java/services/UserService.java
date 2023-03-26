package services;

//import auth.JwsGenerator;
//import com.nimbusds.jose.JOSEException;
import data.control.UserControlPort;
import data.infrastructure.UserInfPort;
import domain.exceptions.ChangePasswordException;
import domain.exceptions.JwsException;
import domain.exceptions.UserException;
import jakarta.transaction.Transactional;
import domain.model.Role;
import domain.model.dto.CreateUserDto;
import domain.model.dto.UpdateUserDto;
import domain.model.user.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@RequestScoped
public class UserService {

    @Inject
    private UserControlPort userControlPort;

    @Inject
    private UserInfPort userInfPort;

    @Inject
    private SecurityContext securityContext;

//    private final JwsGenerator jwsGenerator = new JwsGenerator();

    private final Logger log = Logger.getLogger(getClass().getName());

    public List<User> getAllUsers() {
        return userInfPort.getAll();
    }

    public List<User> getAllClients() {
        return userInfPort.getAllClients();
    }

    public List<User> getClientsByUsername(String pattern) {
        return userInfPort.getByUsername(pattern);
    }

    public User getClientByUsername(String username) {
        return userInfPort.get(username);
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void addClientToHotel(CreateUserDto client) throws UserException {
        try {
//            userControlPort.getRepository().getEntityTransaction().begin();
            userInfPort.get(client.getUsername());
//            userControlPort.getRepository().getEntityManager().lock(userControlPort.get(client.getUsername()), LockModeType.PESSIMISTIC_WRITE);
//            userControlPort.getRepository().getEntityTransaction().rollback();
            log.warning("Client %s already exists".formatted(client.getUsername()));
            throw new UserException("Client %s already exists".formatted(client.getUsername()));
        } catch (NoSuchElementException e) {
            userControlPort.add(new User(client.getUsername(), client.getPassword(), client.getFirstName(),
                    client.getLastName(), client.getCity(), client.getStreet(), client.getStreetNumber(), client.getPostalCode()));
//            userControlPort.getRepository().getEntityTransaction().commit();
        }
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void modifyClient(UpdateUserDto client, String jws) throws JwsException, UserException {
        try {
//            if (jwsGenerator.verify(jws)) {
//                String newJws = jwsGenerator.generateJws(client.getUsername());
//                if (!newJws.equals(jws)) {
//                    throw new JwsException("Username can not be changed");
//                }
//            }
//            userControlPort.getRepository().getEntityTransaction().begin();
            User oldUser = userInfPort.get(client.getUsername());
//            userControlPort.getRepository().getEntityManager().lock(oldUser, LockModeType.PESSIMISTIC_WRITE);

            oldUser.setUsername(client.getUsername());
            oldUser.setFirstName(client.getFirstName());
            oldUser.setLastName(client.getLastName());
            oldUser.setCity(client.getCity());
            oldUser.setStreet(client.getStreet());
            oldUser.setStreetNumber(client.getStreetNumber());
            oldUser.setPostalCode(client.getPostalCode());
            oldUser.setRole(Role.valueOf(client.getRole()));
            userControlPort.update(oldUser);
//            userControlPort.getRepository().getEntityTransaction().commit();
        } catch (NoSuchElementException e) {
//            userControlPort.getRepository().getEntityTransaction().rollback();
            log.warning("Client %s does not exist".formatted(client.getUsername()));
            throw new UserException("Client %s does not exist".formatted(client.getUsername()));
        }
//        catch (ParseException | JOSEException e) {
//            throw new JwsException("JWS exception");
//        }
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void deactivateClient(String username) throws UserException {
        try {
//            userControlPort.getRepository().getEntityTransaction().begin();
            User user = userInfPort.get(username);
//            userControlPort.getRepository().getEntityManager().lock(user, LockModeType.PESSIMISTIC_WRITE);
            user.setIsActive(false);
            userControlPort.update(user);
//            userControlPort.getRepository().getEntityTransaction().commit();
        } catch (NoSuchElementException e) {
//            userControlPort.getRepository().getEntityTransaction().rollback();
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void activateClient(String username) throws UserException {
        try {
//            userControlPort.getRepository().getEntityTransaction().begin();
            User user = userInfPort.get(username);
//            userControlPort.getRepository().getEntityManager().lock(user, LockModeType.PESSIMISTIC_WRITE);
            user.setIsActive(true);
            userControlPort.update(user);
//            userControlPort.getRepository().getEntityTransaction().commit();
        } catch (NoSuchElementException e) {
//            userControlPort.getRepository().getEntityTransaction().rollback();
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public void changePassword(String oldPassword, String newPassword) throws UserException, ChangePasswordException {
        String username = securityContext.getCallerPrincipal().getName();
        try {
            User user = userInfPort.get(username);
            if (!user.getPassword().equals(oldPassword)) {
                throw new ChangePasswordException("Old password is wrong.");
            }
//            userControlPort.getRepository().getEntityTransaction().begin();
            user.setPassword(newPassword);
            userControlPort.update(user);
//            userControlPort.getRepository().getEntityTransaction().commit();
        } catch (NoSuchElementException e) {
//            userControlPort.getRepository().getEntityTransaction().rollback();
            log.warning("Client %s does not exist".formatted(username));
            throw new UserException("Client %s does not exist".formatted(username));
        }
    }

//    public String getJwsForUser(String username) throws UserException, JOSEException {
//        try {
//            User user = getClientByUsername(username);
////            return jwsGenerator.generateJws(user.getUsername());
//        } catch (NoSuchElementException e) {
//            log.warning("Client %s does not exist".formatted(username));
//            throw new UserException("Client %s does not exist".formatted(username));
//        }
//    }

}
