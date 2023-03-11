package account.business.model.requests;

import account.util.enums.LockUnlockOperation;

import javax.validation.constraints.NotEmpty;

public class LockUnlockUserRequest {
    @NotEmpty
    private String user;

    private LockUnlockOperation operation;

    public String getUser() { return user.toLowerCase(); }

    public LockUnlockOperation getOperation() { return operation; }
}
