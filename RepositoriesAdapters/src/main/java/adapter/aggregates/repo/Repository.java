package adapter.aggregates.repo;

import jakarta.persistence.*;
import lombok.Data;

import javax.enterprise.context.ApplicationScoped;

@Data
@ApplicationScoped
public class Repository {

    @PersistenceContext
    private EntityManager entityManager = Persistence.createEntityManagerFactory("TEST_HOTEL").createEntityManager();

    private final EntityTransaction entityTransaction = entityManager.getTransaction();
}
