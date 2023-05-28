package rest;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/rent")
@DeclareRoles({"ADMIN", "MODERATOR", "USER", "GUEST"})
public class Main extends Application {
}

