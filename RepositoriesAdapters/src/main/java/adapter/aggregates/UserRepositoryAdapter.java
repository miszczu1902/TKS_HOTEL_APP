package adapter.aggregates;

import adapter.model.user.UserEnt;
import adapter.aggregates.repo.Repository;
import data.repositories.control.UserRemovePort;
import data.repositories.control.UserAddPort;
import data.repositories.control.UserUpdatePort;
import data.repositories.infrastructure.UserFindPort;
import data.repositories.infrastructure.UserGetAllPort;
import data.repositories.infrastructure.UserGetPort;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.user.User;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class UserRepositoryAdapter implements UserAddPort, UserRemovePort, UserGetAllPort,
        UserFindPort, UserUpdatePort, UserGetPort{

    @Inject
    private Repository repository;

    @Override
    public User get(Object element) {
        return Optional.ofNullable(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<User> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                .map(element -> repository.getEntityManager().find(UserEnt.class, element)).map(this::userEntToUser)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<User> getAll() {
        return repository.getEntityManager().createQuery("SELECT user FROM UserEnt user", UserEnt.class).getResultList().stream().map(this::userEntToUser).collect(Collectors.toList());
    }

    @Override
    public void add(User user) {
        repository.getEntityManager().persist(user);
    }

    @Override
    public void remove(User... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager().remove(userToUserEnt(element)));
    }

    @Override
    public void update(User... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager().merge(userToUserEnt(element)));
    }

    public List<User> getAllClients() {
        return repository.getEntityManager().createQuery("SELECT user FROM UserEnt user WHERE role = 'USER'", UserEnt.class)
                .getResultList().stream()
                .map(this::userEntToUser).collect(Collectors.toList());
    }

    public List<User> getByUsername(String pattern) {
        return repository.getEntityManager().createQuery("SELECT user FROM UserEnt user WHERE username LIKE :id", UserEnt.class)
                .setParameter("id", "%" + pattern + "%")
                .getResultList().stream()
                .map(this::userEntToUser).collect(Collectors.toList());
    }

    public User getByUsernameAndPasswd(String username, String password) {
        return repository.getEntityManager().createQuery("SELECT user FROM UserEnt user WHERE username LIKE :id AND password LIKE :password", UserEnt.class)
                .setParameter("id", username)
                .setParameter("password", password)
                .getResultList().stream()
                .map(this::userEntToUser)
                .toList().get(0);
    }

    private UserEnt userToUserEnt(User user) {
                return new UserEnt(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(),
                        user.getCity(), user.getStreet(), user.getStreetNumber(), user.getPostalCode());
    }

    private User userEntToUser(UserEnt userEnt) {
        return new User(userEnt.getUsername(), userEnt.getPassword(), userEnt.getFirstName(), userEnt.getLastName(),
                        userEnt.getCity(), userEnt.getStreet(), userEnt.getStreetNumber(), userEnt.getPostalCode());
    }
}
