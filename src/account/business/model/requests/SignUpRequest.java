package account.business.model.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SignUpRequest {
    @NotEmpty
    private String name;

    @NotEmpty
    private String lastname;

    @NotEmpty
    @Pattern(regexp = ".+@acme.com")
    private String email;

    @NotEmpty
    @Size(min = 12)
    private String password;

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "SignUpRequest{" +
                "name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public static class NewPassword {
        @JsonProperty("new_password")
        private String newPassword;

        public String getNewPassword() { return newPassword; }
    }

    public static class NewPasswordResponse {
        private final String email;
        private final String status;

        public NewPasswordResponse(String email) {
            this.email = email;
            status = "The password has been updated successfully";
        }

        public String getEmail() {
            return email;
        }

        public String getStatus() {
            return status;
        }
    }
}
