package service.port.infrasturcture;

import domain.model.user.User;

import java.util.List;

public interface UserInfServicePort {
    User getUser(String username);
}
