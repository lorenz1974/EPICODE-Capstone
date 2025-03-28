package capstone.be.signrequestchronology;

public enum ChronologyType {
    CREATION("Creation of the request"),
    UPDATE("Update of the request"),
    CHECK("Checking the request"),
    DELETION("Deletion of the request"),
    POLLING("Polling the sign request from the provider"),
    RETRIEVING("Retrieving the sign request information from the database"),
    TEMPLATE_BINDING("Binding the template"),
    DATA_PREPARATION("Preparing the data"),
    PUSHING("Pushing the request to the provider"),
    PROVIDER_ACK("Provider acknowledgment"),
    SIGNATURE_STARTED("Signature process started"),
    SIGNATURE_COMPLETED("Signature process completed"),
    SIGNATURE_FAILED("Signature process failed"),
    CANCELLATION("Cancellation of the request"),
    EXPIRATION("Expiration of the request"),
    NOTIFICATION_SENT("Notification sent"),
    STORAGE("Storing the request"),
    ERROR_HANDLING("Handling errors");

    private final String description;

    ChronologyType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
