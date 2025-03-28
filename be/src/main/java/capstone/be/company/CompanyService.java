package capstone.be.company;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import capstone.be.city.City;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Page<Company> getAllCompanies(Pageable pageable, String q) {
        log.debug("Searching companies with query '{}'", q);

        if (q == null || q.isEmpty()) {
            return companyRepository.findAll(pageable);
        }
        return companyRepository.omniSearch(q.toLowerCase(), pageable);
    }

    public Company getCompanyById(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Company not found"));
    }

    public Company findRandomCompany() {
        log.debug("Fetching a random company");
        long count = companyRepository.count();
        int randomIndex = (int) (Math.random() * count);
        return companyRepository.findAll(PageRequest.of(randomIndex, 1)).getContent().get(0);
    }
}
