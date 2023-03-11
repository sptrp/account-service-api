package account.business.model.requests;

import javax.validation.constraints.NotEmpty;

public class ChangeRoleRequest {
    @NotEmpty
    private String user;

    private String role;

    private String operation;

    public String getUser() {
        return user;
    }

    public String getRole() {
        return role;
    }

    public String getOperation() {
        return operation;
    }
}
