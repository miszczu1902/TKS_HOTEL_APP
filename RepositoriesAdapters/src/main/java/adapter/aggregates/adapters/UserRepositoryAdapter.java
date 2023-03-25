package adapter.aggregates.adapters;

import adapter.aggregates.mapper.ModelMapper;
import adapter.aggregates.repo.Repository;
import adapter.model.user.UserEnt;
import data.control.UserControlPort;
import data.infrastructure.UserInfPort;
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
public class UserRepositoryAdapter implements UserInfPort, UserControlPort {

    @Inject
    private Repository repository;

    @Override
    public User get(Object element) {
        return Optional.ofNullable(find(element).get(0)).orElseThrow();
    }

    @Override
    public List<User> find(Object... elements) {
        return Optional.of(Arrays.stream(elements)
                .map(element -> repository.getEntityManager().find(UserEnt.class, element)).map(ModelMapper::userEntToUser)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    @Override
    public List<User> getAll() {
        return repository.getEntityManager().createQuery("SELECT user FROM UserEnt user", UserEnt.class).getResultList().stream().map(ModelMapper::userEntToUser).collect(Collectors.toList());
    }

    @Override
    public void add(User user) {
        repository.getEntityManager().persist(user);
    }

    @Override
    public void remove(User... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager().remove(ModelMapper.userToUserEnt(element)));
    }

    @Override
    public void update(User... elements) {
        Arrays.asList(elements).forEach(element -> repository.getEntityManager().merge(ModelMapper.userToUserEnt(element)));
    }

    @Override
    public List<User> getAllClients() {
        return repository.getEntityManager().createQuery("SELECT user FROM UserEnt user WHERE role = 'USER'", UserEnt.class)
                .getResultList().stream()
                .map(ModelMapper::userEntToUser).collect(Collectors.toList());
    }

    @Override
    public List<User> getByUsername(String pattern) {
        return repository.getEntityManager().createQuery("SELECT user FROM UserEnt user WHERE username LIKE :id", UserEnt.class)
                .setParameter("id", "%" + pattern + "%")
                .getResultList().stream()
                .map(ModelMapper::userEntToUser).collect(Collectors.toList());
    }

    @Override
    public User getByUsernameAndPasswd(String username, String password) {
        return repository.getEntityManager().createQuery("SELECT user FROM UserEnt user WHERE username LIKE :id AND password LIKE :password", UserEnt.class)
                .setParameter("id", username)
                .setParameter("password", password)
                .getResultList().stream()
                .map(ModelMapper::userEntToUser)
                .toList().get(0);
    }

}
