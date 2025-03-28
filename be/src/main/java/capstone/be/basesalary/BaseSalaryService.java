package capstone.be.basesalary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BaseSalaryService {

    private final BaseSalaryRepository baseSalariesRepository;

    public Page<BaseSalary> getAllBaseSalaries(Pageable pageable, String q) {
        log.debug("Searching base salary with query '{}'", q);

        if (q == null || q.isEmpty()) {
            return baseSalariesRepository.findAll(pageable);
        }
        return baseSalariesRepository.omniSearch(q.toLowerCase(), pageable);
    }

    public void createBaseSalary(BaseSalaryRequest request) {
        log.debug("Creating new base salary with amount {}", request.getBaseSalary());

        baseSalariesRepository.insertBaseSalary(
                request.getBaseSalary(),
                request.getContingency(),
                request.getDueFullTime(),
                request.getValideFrom(),
                request.getValideTo(),
                request.getTredicesima(),
                request.getThirteenMonth(),
                request.getQuattordicesima(),
                request.getFourteenMonth(),
                request.getContract_Id());
    }

    public BaseSalary getBaseSalaryById(Long id) {
        return baseSalariesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Base Salary not found"));
    }

    public List<BaseSalary> getBaseSalaryBySectorId(Long id) {
        List<BaseSalary> baseSalaries = baseSalariesRepository.findBySectorId(id);
        if (baseSalaries.isEmpty()) {
            throw new EntityNotFoundException("Base Salary not found for Sector ID: " + id);
        }
        return baseSalaries;
    }
}
