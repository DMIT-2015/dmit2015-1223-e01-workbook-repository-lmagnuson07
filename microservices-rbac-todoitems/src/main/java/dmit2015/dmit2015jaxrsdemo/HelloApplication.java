package dmit2015.dmit2015jaxrsdemo;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.auth.LoginConfig;

@ApplicationPath("/restapi")
@LoginConfig(authMethod="MP-JWT", realmName="MP JWT Realm")
@DeclareRoles({"Sales","Shipping","Finance"})
public class HelloApplication extends Application {

}