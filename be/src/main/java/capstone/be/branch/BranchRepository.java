package capstone.be.branch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    @Query("SELECT b FROM Branch b WHERE " +
            "LOWER(b.description) LIKE %?1% OR " +
            "LOWER(b.alternativeDescription) LIKE %?1% OR " +
            "LOWER(b.city) LIKE %?1% OR " +
            "LOWER(b.province) LIKE %?1% OR " +
            "LOWER(b.cap) LIKE %?1% OR " +
            "LOWER(b.address) LIKE %?1% OR " +
            "LOWER(b.phone) LIKE %?1% OR " +
            "LOWER(b.piva) LIKE %?1% OR " +
            "LOWER(b.mail) LIKE %?1%")
    Page<Branch> omniSearch(String query, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO branches (
                id, description, alternative_description, city, province, cap, address, phone, piva, contribution_start, mail, company_id
            ) VALUES (
                :id, :description, :alternativeDescription, :city, :province, :cap, :address, :phone, :piva, CAST(:contributionStart AS timestamp) , :mail, :companyId
            )
            """, nativeQuery = true)
    void insertBranch(
            @Param("id") Long id,
            @Param("description") String description,
            @Param("alternativeDescription") String alternativeDescription,
            @Param("city") String city,
            @Param("province") String province,
            @Param("cap") String cap,
            @Param("address") String address,
            @Param("phone") String phone,
            @Param("piva") String piva,
            @Param("contributionStart") LocalDateTime contributionStart,
            @Param("mail") String mail,
            @Param("companyId") Long companyId);
}
