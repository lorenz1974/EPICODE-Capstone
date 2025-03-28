package capstone.be.sectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {
    Page<Sector> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO sectors (id, description) VALUES (:id, :description)", nativeQuery = true)
    void insertSector(@Param("id") Long id, @Param("description") String description);
}
