package capstone.be.signrequest;

public enum SignRequestStatus {
    CREATED("Request created"),
    SENT("Request sent"),
    SIGNED("Request signed"),
    FAILED("Request failed"),
    ERROR("Request error"),
    ACTIVE("Request active. NAMIRIAL is waiting for the user to sign"),
    WAITING("Generic state of waiting for some action"),
    CANCELED("Request cancelled by the requestor"),
    EXPIRED("Request expired"),
    PENDING("Request pending"),
    DELETED("Request deleted"),
    REJECTED("Request rejected by the signer or the system"),
    COMPLETED("Request completed"),
    ANOMALY("Request anomaly");

    private final String description;

    SignRequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
