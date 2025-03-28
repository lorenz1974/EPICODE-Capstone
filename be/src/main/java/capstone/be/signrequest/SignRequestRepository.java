package capstone.be.signrequest;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SignRequestRepository extends JpaRepository<SignRequest, Long> {

        static final String WHERE_CLAUSE = """
                        (LOWER(sr.providerEnvelopeId) LIKE %:q%) OR
                        (LOWER(sr.status) LIKE %:q%) OR
                        (CAST(sr.createdAt AS string) LIKE %:q%) OR
                        (CAST(sr.updatedAt AS string) LIKE %:q%) OR
                        (LOWER(sr.signedByAppUser.nameSurname) LIKE %:q%) OR
                        (LOWER(sr.createdByAppUser.nameSurname) LIKE %:q%) OR
                        (LOWER(sr.docTemplate.name) LIKE %:q%) OR
                        (LOWER(sr.docTemplate.subject) LIKE %:q%) OR
                        (LOWER(sr.docTemplate.templateProvider) LIKE %:q%)
                        """;

        @Query(value = "SELECT sr FROM SignRequest sr WHERE "
                        + WHERE_CLAUSE, countQuery = "SELECT COUNT(sr) FROM SignRequest sr WHERE " + WHERE_CLAUSE)
        Page<SignRequest> omniSearch(@Param("q") String q, Pageable pageable);

        Optional<SignRequest> findByProviderEnvelopeId(String providerEnvelopeId);

        List<SignRequest> findByStatus(SignRequestStatus status);

        List<SignRequest> findByStatusIn(List<SignRequestStatus> statuses);

        @Query("SELECT new capstone.be.signrequest.SignRequestSimpleStats(s.status, COUNT(s.status)) FROM SignRequest s GROUP BY s.status ORDER BY s.status")
        List<SignRequestSimpleStats> getSimpleStats();

        @Query("SELECT new capstone.be.signrequest.SignRequestSimpleStats(s.status, COUNT(s.status)) FROM SignRequest s WHERE s.signedByAppUser.id = :userId OR s.createdByAppUser.id = :userId GROUP BY s.status ORDER BY s.status")
        List<SignRequestSimpleStats> getSimpleStats(@Param("userId") Long userId);
}
