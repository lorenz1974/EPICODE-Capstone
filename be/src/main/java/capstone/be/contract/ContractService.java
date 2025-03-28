package capstone.be.contract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContractService {

    private final ContractRepository contractRepository;

    public Page<Contract> getAllContracts(Pageable pageable, String q) {
        log.debug("Searching contracts with query '{}'", q);

        if (q == null || q.isEmpty()) {
            return contractRepository.findAll(pageable);
        }
        return contractRepository.omniSearch(q.toLowerCase(), pageable);
    }

    public Contract getContractById(Long id) {
        return contractRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Contract not found"));
    }

    public Page<Contract> getContractBySectorId(Long sectorId, Pageable pageable) {
        log.debug("Searching contracts for sector ID: {}", sectorId);
        return contractRepository.findBySectorId(pageable, sectorId);
    }

    public Contract findRandomContract(long sectorId) {
        log.debug("Fetching a random contract for sector ID {}", sectorId);
        List<Contract> contracts = contractRepository.findBySectorId(sectorId);
        int randomIndex = (int) (Math.random() * contracts.size());
        Contract contract = contracts.get(randomIndex);
        log.debug("Random contract: {}", contract);
        return contract;
    }
}
