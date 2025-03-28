package capstone.be.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

        static final String WHERE_CLAUSE = """
                        (LOWER(c.level) LIKE %:q%) OR
                        (c.sector.id = :q)""";

        @Query(value = "SELECT c FROM Contract c WHERE "
                        + WHERE_CLAUSE, countQuery = "SELECT COUNT(c) FROM Contract c WHERE " + WHERE_CLAUSE)
        Page<Contract> omniSearch(@Param("q") String q, Pageable pageable);

        //
        // List
        //
        List<Contract> findBySectorId(Long sectorId);

        //
        // Pageable
        //
        Page<Contract> findBySectorId(Pageable pageable, Long sectorId);

        long countBySectorId(long id);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO contracts (level, sector_id)
                        VALUES (:level, :sectorId)
                        """, nativeQuery = true)
        void insertContract(
                        @Param("level") String level,
                        @Param("sectorId") Long sectorId);

}
