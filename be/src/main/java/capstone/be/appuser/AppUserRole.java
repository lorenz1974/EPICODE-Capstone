package capstone.be.appuser;

public enum AppUserRole {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_OPERATOR,
    ROLE_SECRETARY;

    @Override
    public String toString() {
        switch (this) {
            case ROLE_ADMIN:
                return "Administrator";
            case ROLE_USER:
                return "User";
            case ROLE_OPERATOR:
                return "Operator";
            case ROLE_SECRETARY:
                return "Secretary";
            default:
                return super.toString();
        }
    }

    public static AppUserRole fromString(String role) {
        switch (role.toLowerCase()) {
            case "administrator":
                return ROLE_ADMIN;
            case "user":
                return ROLE_USER;
            case "operator":
                return ROLE_OPERATOR;
            case "secretary":
                return ROLE_SECRETARY;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

}
