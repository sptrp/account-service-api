package account.util.handlers;

import account.business.model.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    public static String generateAccessDeniedResponse(String path) {
        AccessDeniedResponse accessDeniedResponse = new AccessDeniedResponse(
                Instant.now().toString(),
                403,
                "Forbidden",
                "Access Denied!",
                path
        );

        Gson gson = new Gson();
        return gson.toJson(accessDeniedResponse);
    }

    public static ResponseEntity<Object> generateUserNotFoundResponse(String path) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", 404);
        map.put("error", "Not Found");
        map.put("message", "User not found!");
        map.put("path", path);

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<Object> generateUserDeletedResponse(String email) {
        Map<String, String> map = new HashMap<>();
        map.put("user", email);
        map.put("status", "Deleted successfully!");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public static ResponseEntity<Object> generateAdminSelfDeletingResponse(String email) {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", 400);
        map.put("error", "Bad Request");
        map.put("message", "Can't remove ADMINISTRATOR role!");
        map.put("path", "/api/admin/user/" + email);

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<Object> generateRoleChangedResponse(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("name", user.getName());
        map.put("lastname", user.getLastname());
        map.put("email", user.getEmail());
        map.put("roles", user.getRoles());

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public static ResponseEntity<Object> generateRoleNotFoundResponse() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", 404);
        map.put("error", "Not Found");
        map.put("message", "Role not found!");
        map.put("path", "/api/admin/user/role");

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<Object> generateRoleConflictResponse() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", 400);
        map.put("error", "Bad Request");
        map.put("message", "The user cannot combine administrative and business roles!");
        map.put("path", "/api/admin/user/role");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<Object> generateRemoveNonExistingRoleResponse() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", 400);
        map.put("error", "Bad Request");
        map.put("message", "The user does not have a role!");
        map.put("path", "/api/admin/user/role");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<Object> generateRemoveSingleRoleResponse() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", 400);
        map.put("error", "Bad Request");
        map.put("message", "The user must have at least one role!");
        map.put("path", "/api/admin/user/role");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<Object> generateRemoveAdministratorResponse() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", 400);
        map.put("error", "Bad Request");
        map.put("message", "Can't remove ADMINISTRATOR role!");
        map.put("path", "/api/admin/user/role");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<Object> generateLockAdministratorResponse() {
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", Instant.now().toString());
        map.put("status", 400);
        map.put("error", "Bad Request");
        map.put("message", "Can't lock the ADMINISTRATOR!");
        map.put("path", "/api/admin/user/access");

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<Object> generateBadRequestResponse(String path) {
        Map<String, Object> map = new HashMap<>();

        map.put("error", "Bad Request");
        map.put("path", path);
        map.put("status", 400);
        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<Object> generateUserLockedUnlockedResponse(String email, String operation) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", "User " + email + " " + operation + "!");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public static String generateDefaultUnauthorizedJson(String path, String message) throws JsonProcessingException {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 401);
        errorDetails.put("message", message);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("path", path);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(errorDetails);
    }

    private static class AccessDeniedResponse {
        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        public AccessDeniedResponse(String timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }
    }
}
