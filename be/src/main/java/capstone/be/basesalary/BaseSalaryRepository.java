package capstone.be.basesalary;

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
public interface BaseSalaryRepository extends JpaRepository<BaseSalary, Long> {

    @Query("SELECT b FROM BaseSalary b ORDER BY b.valideFrom DESC")
    Page<BaseSalary> omniSearch(String query, Pageable pageable);

    @Query("SELECT b FROM BaseSalary b WHERE b.contract.sector.id = :id ORDER BY b.valideFrom DESC")
    List<BaseSalary> findBySectorId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO base_salaries (
                base_salary, contingency, due_full_time, valide_from, valide_to,
                tredicesima, thirteen_month, quattordicesima, fourteen_month, contract_id
            ) VALUES (
                :baseSalary, :contingency, :dueFullTime, CAST(:valideFrom AS timestamp), CAST(:valideTo AS timestamp),
                :tredicesima, :thirteenMonth, :quattordicesima, :fourteenMonth, :contractId
            )
            """, nativeQuery = true)
    void insertBaseSalary(
            @Param("baseSalary") Double baseSalary,
            @Param("contingency") Double contingency,
            @Param("dueFullTime") Double dueFullTime,
            @Param("valideFrom") String valideFrom,
            @Param("valideTo") String valideTo,
            @Param("tredicesima") Integer tredicesima,
            @Param("thirteenMonth") Integer thirteenMonth,
            @Param("quattordicesima") Integer quattordicesima,
            @Param("fourteenMonth") Integer fourteenMonth,
            @Param("contractId") Long contractId);
}
