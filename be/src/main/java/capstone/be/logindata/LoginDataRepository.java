package capstone.be.logindata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDataRepository extends JpaRepository<LoginData, Long> {

    static final String WHERE_CLAUSE = """
            (LOWER(l.username) LIKE %:q%) OR
            (LOWER(l.ipAddress) LIKE %:q%) OR
            (LOWER(l.userAgent) LIKE %:q%) OR
            (LOWER(l.device) LIKE %:q%) OR
            (LOWER(l.os) LIKE %:q%) OR
            (LOWER(l.browser) LIKE %:q%) OR
            (LOWER(l.status) LIKE %:q%) OR
            (LOWER(l.message) LIKE %:q%) OR
            (LOWER(l.token) LIKE %:q%) OR
            (LOWER(l.action) LIKE %:q%)
            """;

    @Query(value = "SELECT l FROM LoginData l WHERE "
            + WHERE_CLAUSE, countQuery = "SELECT COUNT(l) FROM LoginData l WHERE " + WHERE_CLAUSE)
    Page<LoginData> omniSearch(@Param("q") String q, Pageable pageable);

    @Query("SELECT l FROM LoginData l WHERE l.username = ?1")
    Page<LoginData> findByUsername(String username, Pageable pageable);
}
