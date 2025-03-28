package capstone.be.jobprofile;

import java.time.LocalDate;

import jakarta.persistence.Column;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobProfileRequest {

    // Could be null if still not assigned to an app user
    @Min(value = 1, message = "App user id must be greater than 1")
    private Long appUserId;

    @NotNull(message = "Company id is required")
    @Min(value = 0, message = "Company id must be greater than 0")
    private Long companyId;

    @NotNull(message = "Branch id is required")
    @Min(value = 0, message = "Branch id must be greater than 0")
    private Long branchId;

    @NotNull(message = "Contract id is required")
    @Min(value = 0, message = "Contract id must be greater than 0")
    private Long contractId;

    @Min(value = 0, message = "Base salary id must be greater than 0")
    private Long baseSalaryId;

    @Min(value = 0, message = "Hours per week must be greater than 0")
    @Column(nullable = false, columnDefinition = "int default 0")
    private int hoursPerWeek;

    @Column(nullable = true)
    private JobProfileType jobProfileType;

    @PastOrPresent(message = "Start date must be in the past or present")
    @Column(nullable = true)
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    @Column(nullable = true)
    private String jobDescription;
}
