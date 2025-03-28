package capstone.be.sectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;

    public Page<Sector> getAllSectors(Pageable pageable, String query) {
        return sectorRepository.findByDescriptionContainingIgnoreCase(query, pageable);
    }

    public Sector getBySectorId(Long id) {
        return sectorRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Sector not found"));
    }
}
