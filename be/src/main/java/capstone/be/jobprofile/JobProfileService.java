package capstone.be.jobprofile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import capstone.be.appuser.AppUser;
import capstone.be.appuser.AppUserRepository;
import capstone.be.basesalary.BaseSalary;
import capstone.be.basesalary.BaseSalaryRepository;
import capstone.be.branch.Branch;
import capstone.be.branch.BranchRepository;
import capstone.be.company.Company;
import capstone.be.company.CompanyRepository;
import capstone.be.contract.Contract;
import capstone.be.contract.ContractRepository;
import capstone.be.sectors.Sector;
import capstone.be.sectors.SectorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobProfileService {

    private final AppUserRepository appUserRepository;
    private final JobProfileRepository jobProfileRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final ContractRepository contractRepository;
    private final BaseSalaryRepository baseSalaryRepository;
    private final SectorRepository sectorRepository;

    private final JobProfileMapper jobProfileMapper;

    public Page<JobProfile> getAllJobProfiles(Pageable pageable, String q, boolean alsoDeleted) {
        log.debug("Searching job profiles with query '{}' and alsoDeleted: {}", q, alsoDeleted);

        if (q == null || q.isEmpty()) {
            return jobProfileRepository.findAll(pageable);
        }
        return jobProfileRepository.omniSearch(q.toLowerCase(), pageable);
    }

    public JobProfile getJobProfileById(Long id) {
        return jobProfileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CheckJobProfile - JobProfile not found"));
    }

    public JobProfile saveJobProfile(JobProfile jobProfile) {
        // No need to check, the checkJobProfile method will throw an exception if the
        // entities are not found
        return jobProfileRepository.save(jobProfile);
    }

    public JobProfile createJobProfile(JobProfileRequest jobProfileRequest) {
        // Do all the checks on the entities
        JobProfile jobProfile = checkJobProfile(jobProfileRequest, false);
        // ... then call the save method
        return saveJobProfile(jobProfile);
    }

    public JobProfile updateJobProfile(Long id, JobProfileRequest jobProfileRequest) {

        // Check if the job profile exists
        if (!jobProfileRepository.existsById(id)) {
            throw new EntityNotFoundException("Job profile not found");
        }

        // Do all the checks on the entities
        JobProfile updatedJobProfile = checkJobProfile(jobProfileRequest, false);
        // ...then set the id of the job profile
        updatedJobProfile.setId(id);

        // Include the AppUser data in the updated job profile
        // this could be not strictly necessary but it is a good practice
        // to include the AppUser data in the job profile
        //
        // Use the repository to avoid circular references
        AppUser appUser = appUserRepository.findById(jobProfileRequest.getAppUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User ID not found"));
        updatedJobProfile.setAppUser(appUser);

        // ... then call the save method
        return saveJobProfile(updatedJobProfile);
    }

    public void deleteJobProfile(Long id) {
        //
        // Realtion shoud change from one to one to one to many so it could be possible
        // to soft delete the record and keep the history
        //
        JobProfile jobProfile = getJobProfileById(id);
        // jobProfile.setDeleted(true);
        // jobProfileRepository.save(jobProfile);
        jobProfileRepository.delete(jobProfile);
    }

    //
    // Method to check all the entities in the job profile request
    // and return a JobProfile object
    //
    public JobProfile checkJobProfile(JobProfileRequest jobProfileRequest, boolean noUserCheck) {

        // Create a new job profile object
        JobProfile jobProfile = new JobProfile();

        // Than update it from the DTO with the method updateEntityFromDto
        jobProfileMapper.updateEntityFromDto(jobProfile, jobProfileRequest);

        // Then check the object with the checkJobProfile method and return the object
        // if everything is ok
        return checkJobProfile(jobProfile, noUserCheck);
    }

    //
    // This method will check all the entities in the job profile
    // and return the same job profile object. This method is used when updating a
    // job profile.
    //
    public JobProfile checkJobProfile(JobProfile jobProfile, boolean noUserCheck) {

        //
        // Check if the app user exists only if noUserCheck is false.
        // noUserCheck is used to avoid checking the user when creating a job profile
        //
        // Use the repository to avoid circular references
        if (!noUserCheck) {
            if (!appUserRepository.existsById(jobProfile.getAppUser().getId())) {
                throw new EntityNotFoundException("CheckJobProfile - User ID not found");
            }
        }

        //
        // Check if the company exists, if not company service will throw an exception
        //
        Company company = companyRepository.findById(jobProfile.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("CheckJobProfile - Company not found"));

        //
        // Check if the branch exists and belongs to the company
        //
        Branch branch = branchRepository.findById(jobProfile.getBranch().getId())
                .orElseThrow(() -> new EntityNotFoundException("CheckJobProfile - Branch not found"));
        // Check if the branch belongs to the company
        if (!company.getBranches().stream().anyMatch(b -> b.getId() == jobProfile.getBranch().getId())) {
            throw new EntityNotFoundException("CheckJobProfile - Branch '" + jobProfile.getBranch().getId()
                    + "' does not belong to the company '" + jobProfile.getCompany().getId() + "'");
        }

        //
        // Check if the contract exists and belongs to the company sector
        //
        // Check if the sector exists
        Sector sector = sectorRepository.findById(company.getSector().getId())
                .orElseThrow(() -> new EntityNotFoundException("CheckJobProfile - Sector not found"));
        // Check if the contract exists
        Contract contract = contractRepository.findById(jobProfile.getContract().getId())
                .orElseThrow(() -> new EntityNotFoundException("CheckJobProfile - Contract not found"));
        // Check if the contract belongs to the company sector
        if (!sector.getContracts().stream().anyMatch(c -> c.getId() == contract.getId())) {
            throw new EntityNotFoundException("CheckJobProfile - Contract '" + jobProfile.getContract().getId()
                    + "' does not belong to the company sector '" + sector.getId() + "'");
        }

        //
        // Check if the base salary exists and belongs to the contract
        //
        if (!baseSalaryRepository.existsById(jobProfile.getBaseSalary().getId())) {
            throw new EntityNotFoundException("CheckJobProfile - Base salary not found");
        }
        // Check if the base salary belongs to the contract
        if (!contract.getBaseSalaries().stream().anyMatch(b -> b.getId() == jobProfile.getBaseSalary().getId())) {
            throw new EntityNotFoundException("CheckJobProfile - Base salary '" + jobProfile.getBaseSalary().getId()
                    + "' does not belong to the contract '" + jobProfile.getContract().getId() + "'");
        }

        //
        // Validate jobProfileType is unuseful because it is an enum that is
        // already checked in the JobProfile class
        //

        return jobProfile;
    }
}
