package dmit2015.faces;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Named("currentLoginView")
@SessionScoped
@Getter
@Setter
@NoArgsConstructor
public class LoginView implements Serializable {
    private Integer maxLoginAttempts = 3;
    private Integer loginAttempts;
    private Boolean overMax = false;
    private Boolean overOne = false;

    public void login() {
        if (loginAttempts == null) {
            loginAttempts = 1;
            overOne = true;
        } else {
            overOne = true;
            loginAttempts++;
        }

        if (loginAttempts >= maxLoginAttempts) {
            overMax = true;
        }
    }
}
