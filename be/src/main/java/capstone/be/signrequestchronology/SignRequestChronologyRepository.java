package capstone.be.signrequestchronology;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import capstone.be.signrequest.SignRequest;

@Repository
public interface SignRequestChronologyRepository extends JpaRepository<SignRequestChronology, Long> {

    static final String WHERE_CLAUSE = """
            (LOWER(src.eventType) LIKE %:q%) OR
            (LOWER(src.status) LIKE %:q%) OR
            (LOWER(src.message) LIKE %:q%) OR
            (LOWER(src.eventData) LIKE %:q%) OR
            (LOWER(src.signRequest.status) LIKE %:q%) OR
            (CAST(src.createdAt AS string) LIKE %:q%) OR
            (CAST(src.updatedAt AS string) LIKE %:q%)
            """;

    @Query(value = "SELECT src FROM SignRequestChronology src WHERE "
            + WHERE_CLAUSE, countQuery = "SELECT COUNT(src) FROM SignRequestChronology src WHERE " + WHERE_CLAUSE)
    Page<SignRequestChronology> omniSearch(@Param("q") String q, Pageable pageable);

    Page<SignRequestChronology> findByEventType(String eventType, Pageable pageable);

    List<SignRequestChronology> findByStatus(String status);
}
