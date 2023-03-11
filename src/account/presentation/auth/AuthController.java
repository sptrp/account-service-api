package account.presentation.auth;

import account.business.model.entities.User;
import account.business.model.requests.SignUpRequest;
import account.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AuthController {
    @Autowired
    UserService userService;

    @PostMapping("/api/auth/signup")
    public User.UserData registerUser(@RequestBody @Valid SignUpRequest signUpRequest) {
        return userService.signUpUser(signUpRequest);
    }

    @PostMapping("/api/auth/changepass")
    public SignUpRequest.NewPasswordResponse changeUserPassword(@RequestBody SignUpRequest.NewPassword newPassword, Authentication auth) {
        return userService.changePassword(auth.getName(), newPassword, auth);
    }
}
