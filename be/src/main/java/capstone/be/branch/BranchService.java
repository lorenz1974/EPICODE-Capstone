package capstone.be.branch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BranchService {

    private final BranchRepository branchRepository;

    public Page<Branch> getAllBranches(Pageable pageable, String q) {
        log.debug("Searching branches with query '{}'", q);

        if (q == null || q.isEmpty()) {
            return branchRepository.findAll(pageable);
        }
        return branchRepository.omniSearch(q.toLowerCase(), pageable);
    }

    public Branch getBranchById(Long id) {
        return branchRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Branch not found"));
    }
}
