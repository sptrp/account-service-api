package account.presentation.access;

import account.business.model.entities.User;
import account.business.model.requests.ChangeRoleRequest;
import account.business.model.requests.LockUnlockUserRequest;
import account.business.service.UserService;
import account.business.service.LoggerService;
import account.util.handlers.ResponseHandler;
import account.util.enums.LockUnlockOperation;
import account.util.enums.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class AccessController {

    @Autowired
    private UserService userService;

    @Autowired
    LoggerService loggerService;

    @PutMapping("api/admin/user/role")
    public ResponseEntity<Object> setRole(@RequestBody ChangeRoleRequest changeRoleRequest, Authentication auth) {
        Optional<User> userOptional = userService.findUser(changeRoleRequest.getUser().toLowerCase());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (Arrays.stream(Roles.values()).noneMatch(role -> role.name().equals(changeRoleRequest.getRole()))) {
                return ResponseHandler.generateRoleNotFoundResponse();
            }

            if (Objects.equals(changeRoleRequest.getOperation(), "GRANT")) {
                return userService.grantRole(changeRoleRequest, auth, user);

            } else {
                return userService.removeRole(changeRoleRequest, auth, user);
            }
        }
        return ResponseHandler.generateUserNotFoundResponse("/api/admin/user/role");
    }

    @GetMapping("api/admin/user")
    public ArrayList<User> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping({"api/admin/user/", "api/admin/user/{email}"})
    @Transactional
    public ResponseEntity<Object> deleteUser(@PathVariable(required = false) String email, Authentication auth) {

        if (Objects.equals(auth.getName(), email)) {
            return ResponseHandler.generateAdminSelfDeletingResponse(email);
        }

        if (userService.findUser(email).isEmpty()) {
            return ResponseHandler.generateUserNotFoundResponse("/api/admin/user/" + email);

        } else {
            userService.deleteUser(email, auth);

            return ResponseHandler.generateUserDeletedResponse(email);
        }
    }

    @PutMapping("api/admin/user/access")
    public ResponseEntity<Object> lockUnlockUser(@RequestBody LockUnlockUserRequest lockUnlockUserRequest, Authentication auth) {
        String email = lockUnlockUserRequest.getUser();
        Optional<User> userOptional = userService.findUser(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getRoles().contains("ROLE_ADMINISTRATOR")) {
                return ResponseHandler.generateLockAdministratorResponse();
            }

            if (lockUnlockUserRequest.getOperation() == LockUnlockOperation.LOCK) {
                userService.lock(user, auth.getName().toLowerCase(), "/api/admin/user/access");

                return ResponseHandler.generateUserLockedUnlockedResponse(user.getEmail(), "locked");
            } else {
                userService.unlock(user, auth.getName().toLowerCase(), "/api/admin/user/access");

                return ResponseHandler.generateUserLockedUnlockedResponse(user.getEmail(), "unlocked");
            }
        } else {
            return ResponseHandler.generateUserNotFoundResponse("/api/admin/user/" + email);
        }
    }
}
