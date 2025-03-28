package capstone.be.jobprofile;

public enum JobProfileType {
    FULL_TIME("Full-time employment"),
    PART_TIME("Part-time employment"),
    FIXED_TERM("Fixed-term contract"),
    APPRENTICESHIP("Apprenticeship program"),
    INTERNSHIP("Internship position"),
    REMOTE("Remote work"),
    FREELANCE("Freelance work"),
    TEMPORARY("Temporary employment"),
    SEASONAL("Seasonal work"),
    ZERO_HOURS_CONTRACT("Zero-hours contract"),
    VOLUNTEER("Volunteer work"),
    CONTRACTOR("Contractor position"),
    OTHER("Other type of employment");

    private final String description;

    JobProfileType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
