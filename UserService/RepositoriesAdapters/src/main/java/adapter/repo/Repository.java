package adapter.repo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import javax.annotation.sql.DataSourceDefinition;
import java.sql.Connection;

@DataSourceDefinition(
        name = "java:app/jdbc/TEST_HOTEL_USER",
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "nbd",
        password = "nbdpassword",
        serverName = "databaseUser",
        portNumber = 5432,
        databaseName = "nbddb",
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)
public class Repository {
    @PersistenceContext(unitName = "TEST_HOTEL_USER")
    private EntityManager entityManager;
}
