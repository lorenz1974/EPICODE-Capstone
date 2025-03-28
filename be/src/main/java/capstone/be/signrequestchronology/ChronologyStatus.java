package capstone.be.signrequestchronology;

public enum ChronologyStatus {
    STARTED("Started status"),
    IN_PROGRESS("In progress status"),
    PENDING("In progress status"), // TO BE REMOVED!!!
    COMPLETED("Completed status"),
    FAILED("Failed status"),
    CANCELLED("Cancelled status");

    private final String description;

    ChronologyStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
