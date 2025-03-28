package capstone.be.jobprofile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import capstone.be.appuser.AppUser;
import capstone.be.appuser.AppUserRepository;
import capstone.be.company.Company;
import capstone.be.company.CompanyRepository;
import capstone.be.contract.Contract;
import capstone.be.contract.ContractRepository;
import capstone.be.sectors.SectorRepository;
import capstone.be.branch.Branch;
import capstone.be.branch.BranchRepository;
import capstone.be.basesalary.BaseSalary;
import capstone.be.basesalary.BaseSalaryRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job_profiles")
public class JobProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "app_user_id", nullable = true, unique = true)
    @JsonIncludeProperties({ "id", "name", "surname" })
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnoreProperties("branches")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "base_salary_id", nullable = true)
    @JsonIgnoreProperties("contract")
    private BaseSalary baseSalary;

    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = true)
    @JsonIgnoreProperties("baseSalaries")
    private Contract contract;
    @Column(nullable = false)

    private boolean isDeleted = false;

    // @Min(value = 0, message = "Hours per week must be greater than 0")
    @Column(nullable = false, columnDefinition = "int default 0")
    private int hoursPerWeek;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Job profile type is required")
    private JobProfileType jobProfileType;

    // @PastOrPresent(message = "Start date must be in the past or present")
    @Column(nullable = true)
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    @Column(nullable = true)
    private String jobDescription;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "JobProfile{" + "id=" + id + ", appUser=" + appUser + ", company=" + company + ", branch=" + branch
                + ", contract=" + contract + ", baseSalary=" + baseSalary + ", isDeleted=" + isDeleted
                + ", hoursPerWeek=" + hoursPerWeek + ", jobProfileType=" + jobProfileType + ", startDate=" + startDate
                + ", endDate="
                + endDate + ", jobDescription=" + jobDescription + ", createdAt=" + createdAt + ", updatedAt="
                + updatedAt + "}";
    }

    @Override
    public int hashCode() {
        // Ensure no recursive calls or infinite loops
        return Objects.hash(id, appUser, company, branch, contract, baseSalary, isDeleted, createdAt, updatedAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JobProfile other = (JobProfile) obj;
        return Objects.equals(id, other.id) && Objects.equals(appUser, other.appUser)
                && Objects.equals(company, other.company) && Objects.equals(branch, other.branch)
                && Objects.equals(contract, other.contract) && Objects.equals(baseSalary, other.baseSalary)
                && Objects.equals(isDeleted, other.isDeleted) && Objects.equals(createdAt, other.createdAt)
                && Objects.equals(updatedAt, other.updatedAt);
    }
}