package adapter.aggregates.adapters;

import adapter.aggregates.mapper.ModelMapper;
import adapter.model.UserEnt;
import data.port.control.UserControlPort;
import data.port.infrastructure.UserInfPort;
import domain.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@ApplicationScoped
public class UserRepositoryAdapter implements UserInfPort, UserControlPort {

    @PersistenceContext(unitName = "TEST_HOTEL_USER")
    private EntityManager entityManager;

    @Override
    public User get(Object element) {
        return Optional.ofNullable(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<User> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                .map(element -> entityManager.find(UserEnt.class, element))
                .map(ModelMapper::userEntToUser)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<User> getAll() {
        return entityManager
                .createQuery("SELECT userEnt FROM UserEnt userEnt", UserEnt.class)
                .getResultList().stream()
                .map(ModelMapper::userEntToUser).collect(Collectors.toList());
    }

    @Override
    public void add(User user) {
        entityManager.persist(ModelMapper.userToUserEnt(user));
    }

    @Override
    public void update(User... elements) {
        Arrays.asList(elements).forEach(element -> entityManager.merge(ModelMapper.userToUserEnt(element)));
    }

    @Override
    public List<User> getAllClients() {
        return entityManager
                .createQuery("SELECT userEnt FROM UserEnt userEnt WHERE role = 'USER'", UserEnt.class)
                .getResultList().stream()
                .map(ModelMapper::userEntToUser).collect(Collectors.toList());
    }

    @Override
    public List<User> getByUsername(String pattern) {
        return entityManager
                .createQuery("SELECT userEnt FROM UserEnt userEnt WHERE username LIKE :id", UserEnt.class)
                .setParameter("id", "%" + pattern + "%")
                .getResultList().stream()
                .map(ModelMapper::userEntToUser).collect(Collectors.toList());
    }

    @Override
    public User getByUsernameAndPasswd(String username, String password) {
        return entityManager
                .createQuery("SELECT userEnt FROM UserEnt userEnt WHERE username LIKE :id AND password LIKE :password", UserEnt.class)
                .setParameter("id", username)
                .setParameter("password", password)
                .getResultList().stream()
                .map(ModelMapper::userEntToUser)
                .toList().get(0);
    }

}
