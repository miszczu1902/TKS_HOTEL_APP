package adapter.aggregates.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import lombok.Getter;

import javax.enterprise.context.ApplicationScoped;

@Getter
@ApplicationScoped
public class Repository {

    @PersistenceContext(unitName = "TEST_HOTEL")
    private EntityManager entityManager;
//    private EntityManager entityManager = Persistence.createEntityManagerFactory("TEST_HOTEL").createEntityManager();

//    private final EntityTransaction entityTransaction = entityManager.getTransaction();
}
