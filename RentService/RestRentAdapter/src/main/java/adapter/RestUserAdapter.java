package adapter;

import domain.model.user.User;
import service.port.infrasturcture.UserInfServicePort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class RestUserAdapter {

    @Inject
    private UserInfServicePort userInfServicePort;

    public User getUser(String username) {
        return userInfServicePort.getUser(username);
    }

}
