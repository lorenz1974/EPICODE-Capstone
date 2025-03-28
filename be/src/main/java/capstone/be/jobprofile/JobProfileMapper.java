package capstone.be.jobprofile;

import org.springframework.stereotype.Component;

import capstone.be.appuser.AppUser;
import capstone.be.appuser.AppUserRepository;
import capstone.be.basesalary.BaseSalaryRepository;
import capstone.be.branch.BranchRepository;
import capstone.be.company.CompanyRepository;
import capstone.be.contract.ContractRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JobProfileMapper {

    private final AppUserRepository appUserRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final ContractRepository contractRepository;
    private final BaseSalaryRepository baseSalaryRepository;

    public void updateEntityFromDto(JobProfile entity, JobProfileRequest dto) {
        if (dto.getAppUserId() != null && (entity.getAppUser() == null ||
                !dto.getAppUserId().equals(entity.getAppUser().getId()))) {
            AppUser user = appUserRepository.findById(dto.getAppUserId())
                    .orElseThrow(() -> new EntityNotFoundException("AppUser not found with ID: " + dto.getAppUserId()));
            entity.setAppUser(user);
        }

        if (dto.getCompanyId() != null) {
            entity.setCompany(companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Company not found with ID: " + dto.getCompanyId())));
        }

        if (dto.getBranchId() != null) {
            entity.setBranch(branchRepository.findById(dto.getBranchId())
                    .orElseThrow(() -> new EntityNotFoundException("Branch not found with ID: " + dto.getBranchId())));
        }

        if (dto.getContractId() != null) {
            entity.setContract(contractRepository.findById(dto.getContractId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Contract not found with ID: " + dto.getContractId())));
        }

        if (dto.getBaseSalaryId() != null) {
            entity.setBaseSalary(baseSalaryRepository.findById(dto.getBaseSalaryId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "BaseSalary not found with ID: " + dto.getBaseSalaryId())));
        }

        entity.setHoursPerWeek(dto.getHoursPerWeek());
        entity.setJobProfileType(dto.getJobProfileType());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setJobDescription(dto.getJobDescription());
    }
}
