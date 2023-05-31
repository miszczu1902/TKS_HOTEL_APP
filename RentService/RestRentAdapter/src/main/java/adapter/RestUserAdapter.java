package adapter;

import domain.model.user.User;
import service.port.infrasturcture.UserRentInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RestUserAdapter {

    @Inject
    private UserRentInfServicePort userRentInfServicePort;

    public User getUser(String username) {
        return userRentInfServicePort.getUser(username);
    }

}
