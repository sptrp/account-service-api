package account.business.service;

import account.business.model.entities.User;
import account.business.model.entities.User.UserData;
import account.business.model.requests.ChangeRoleRequest;
import account.business.model.requests.SignUpRequest;
import account.business.model.requests.SignUpRequest.NewPasswordResponse;

import account.persistence.UserRepository;
import account.util.enums.EventAction;
import account.util.handlers.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.util.*;

import static account.util.enums.Roles.ADMINISTRATOR;
import static account.util.enums.Roles.USER;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    @Qualifier("encoder")
    BCryptPasswordEncoder encoder;

    @Autowired
    LoggerService loggerService;

    private static final String VALIDATION_RESPONSE_DIFF = "The passwords must be different!";
    private static final String VALIDATION_RESPONSE_LENGTH = "Password length must be 12 chars minimum!";
    private static final String VALIDATION_RESPONSE_BREACHED = "The password is in the hacker's database!";
    public static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000;

    private static final List<String> filter = Arrays.asList("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    public Optional<User> findUser(String email) {
        return userRepository.findUserByEmail(email);
    }

    public UserData signUpUser(@Valid SignUpRequest signUpRequest) {
        validateRequest(signUpRequest);
        User user = new User(
                signUpRequest.getName(),
                signUpRequest.getLastname(),
                signUpRequest.getEmail().toLowerCase(),
                encoder.encode(signUpRequest.getPassword())
        );

        if (getUsers().size() == 0) {
            user.setRole("ROLE_" + ADMINISTRATOR.name());
        } else {
            user.setRole("ROLE_" + USER.name());
        }

        userRepository.save(user);

        loggerService.logEvent(EventAction.CREATE_USER, "Anonymous", signUpRequest.getEmail().toLowerCase(), "/api/auth/signup");

        return user.getUserData();
    }

    public ResponseEntity<Object> removeRole(ChangeRoleRequest changeRoleRequest, Authentication auth, User user) {
        if (user.getRoles().contains("ROLE_ADMINISTRATOR")) {
            return ResponseHandler.generateRemoveAdministratorResponse();
        }
        if (!user.getRoles().contains("ROLE_" + changeRoleRequest.getRole())) {
            return ResponseHandler.generateRemoveNonExistingRoleResponse();
        }
        if (user.getRoles().size() == 1) {
            return ResponseHandler.generateRemoveSingleRoleResponse();
        }

        user.removeRole("ROLE_" + changeRoleRequest.getRole());
        userRepository.save(user);

        loggerService.logEvent(EventAction.REMOVE_ROLE, auth.getName().toLowerCase(), String.format("Remove role %s from %s", changeRoleRequest.getRole(), changeRoleRequest.getUser().toLowerCase()), "/api/admin/user/role");

        return ResponseHandler.generateRoleChangedResponse(user);
    }

    public ResponseEntity<Object> grantRole(ChangeRoleRequest changeRoleRequest, Authentication auth, User user) {
        if (user.isRoleConflict(changeRoleRequest.getRole())) {
            return ResponseHandler.generateRoleConflictResponse();
        }

        user.setRole("ROLE_" + changeRoleRequest.getRole());
        userRepository.save(user);

        loggerService.logEvent(EventAction.GRANT_ROLE, auth.getName().toLowerCase(), String.format("Grant role %s to %s", changeRoleRequest.getRole(), changeRoleRequest.getUser().toLowerCase()), "/api/admin/user/role");

        return ResponseHandler.generateRoleChangedResponse(user);
    }

    public UserData getUserData(String email) {
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isPresent()) {
            return user.get().getUserData();

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void deleteUser(String email, Authentication auth) {
        userRepository.deleteByEmail(email);

        loggerService.logEvent(EventAction.DELETE_USER, auth.getName().toLowerCase(), email.toLowerCase(), "/api/admin/user");
    }

    public ArrayList<User> getUsers() {
        return userRepository.findAllByOrderByIdAsc();
    }

    public NewPasswordResponse changePassword(String name, SignUpRequest.NewPassword password, Authentication auth) {
        String newPassword = password.getNewPassword();

        User user = userRepository.findUserByEmail(name).get();
        String oldPassword = user.getPassword();

        validateRequest(oldPassword, newPassword);

        String newPasswordEncoded = encoder.encode(newPassword);
        user.setPassword(newPasswordEncoded);
        userRepository.save(user);

        loggerService.logEvent(EventAction.CHANGE_PASSWORD, auth.getName().toLowerCase(), name.toLowerCase(), "/api/auth/changepass");

        return new SignUpRequest.NewPasswordResponse(name);
    }

    // Lock/unlock
    public void increaseFailedAttempts(User user, String requestUri) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        updateFailedAttempts(newFailAttempts, user.getEmail());

        loggerService.logEvent(EventAction.LOGIN_FAILED, user.getEmail(), requestUri, requestUri);
    }

    public void resetFailedAttempts(String email) {
        updateFailedAttempts(0, email);
    }

    public void lock(User user, String subject, String path) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());

        userRepository.save(user);

        loggerService.logEvent(EventAction.LOCK_USER, subject, "Lock user " + user.getEmail().toLowerCase(), path);
    }

    public void unlock(User user, String subject, String path) {
        user.setAccountNonLocked(true);
        user.setLockTime(null);
        user.setFailedAttempt(0);

        userRepository.save(user);

        loggerService.logEvent(EventAction.UNLOCK_USER, subject, "Unlock user " + user.getEmail().toLowerCase(), path);
    }

    public boolean unlockWhenTimeExpired(User user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTimeInMillis = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);

            userRepository.save(user);

            return true;
        }

        return false;
    }

    public void updateFailedAttempts(int failAttempts, String email) {
        Optional<User> userOptional = findUser(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFailedAttempt(failAttempts);

            userRepository.save(user);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    private void validateRequest(SignUpRequest signUpRequest) {
        Optional<User> foundUser = userRepository.findUserByEmail(signUpRequest.getEmail().toLowerCase());
        String password = signUpRequest.getPassword();

        if (foundUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");

        } else if (password.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, VALIDATION_RESPONSE_LENGTH);

        } else if (filter.contains(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, VALIDATION_RESPONSE_BREACHED);
        }
    }

    private void validateRequest(String oldPassword, String newPassword) {

        if (encoder.matches(newPassword, oldPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, VALIDATION_RESPONSE_DIFF);

        } else if (newPassword.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, VALIDATION_RESPONSE_LENGTH);

        } else if (filter.contains(newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, VALIDATION_RESPONSE_BREACHED);
        }
    }
}