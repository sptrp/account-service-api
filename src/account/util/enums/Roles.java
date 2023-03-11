package account.util.enums;

public enum Roles {
    USER("USER"),
    ACCOUNTANT("ACCOUNTANT"),
    AUDITOR("AUDITOR"),
    ADMINISTRATOR("ADMINISTRATOR");

    private final String stringValue;

    Roles(String role) {
        stringValue = role;
    }

    public String getStringValue() {
        return stringValue;
    }
}
