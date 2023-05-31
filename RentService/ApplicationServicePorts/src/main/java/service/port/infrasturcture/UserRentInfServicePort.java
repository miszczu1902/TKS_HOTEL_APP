package service.port.infrasturcture;

import domain.model.user.User;

public interface UserRentInfServicePort {
    User getUser(String username);
}
