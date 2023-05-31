package adapter.aggregates.adapters;

import adapter.aggregates.mapper.ModelMapper;
import adapter.model.user.UserEnt;
import data.port.control.UserRentControlPort;
import data.port.infrastructure.UserRentInfPort;
import domain.model.user.User;
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
public class UserRentRepositoryAdapter implements UserRentInfPort, UserRentControlPort {

    @PersistenceContext(unitName = "TEST_HOTEL_RENT")
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
    public void add(User user) {
        entityManager.persist(ModelMapper.userToUserEnt(user));
    }

    @Override
    public void update(User... elements) {
        Arrays.asList(elements).forEach(element -> entityManager.merge(ModelMapper.userToUserEnt(element)));
    }

}
