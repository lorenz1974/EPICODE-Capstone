package capstone.be.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    static final String WHERE_CLAUSE = """
            (LOWER(c.description) LIKE %:q%) OR
            (LOWER(c.alternativeDescription) LIKE %:q%) OR
            (LOWER(c.city) LIKE %:q%) OR
            (LOWER(c.province) LIKE %:q%) OR
            (LOWER(c.cap) LIKE %:q%) OR
            (LOWER(c.address) LIKE %:q%) OR
            (LOWER(c.phone) LIKE %:q%) OR
            (LOWER(c.piva) LIKE %:q%) OR
            (LOWER(c.notes) LIKE %:q%) OR
            (LOWER(c.mail) LIKE %:q%)
            """;

    @Query(value = "SELECT c FROM Company c WHERE " + WHERE_CLAUSE, countQuery = "SELECT COUNT(c) FROM Company c WHERE "
            + WHERE_CLAUSE)
    Page<Company> omniSearch(@Param("q") String q, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO companies (
                id, description, alternative_description, city, province, cap, address, phone, piva, notes, mail, sector_id
            ) VALUES (
                :id, :description, :alternativeDescription, :city, :province, :cap, :address, :phone, :piva, :notes, :mail, :sectorId
            )
            """, nativeQuery = true)
    void insertCompany(
            @Param("id") Long id, // ID specificato manualmente
            @Param("description") String description,
            @Param("alternativeDescription") String alternativeDescription,
            @Param("city") String city,
            @Param("province") String province,
            @Param("cap") String cap,
            @Param("address") String address,
            @Param("phone") String phone,
            @Param("piva") String piva,
            @Param("notes") String notes,
            @Param("mail") String mail,
            @Param("sectorId") Long sectorId);

}
