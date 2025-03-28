package capstone.be.jobprofile;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobProfileRepository extends JpaRepository<JobProfile, Long> {

        @Query("SELECT j FROM JobProfile j JOIN j.company c WHERE LOWER(c.description) = LOWER(?1)")
        List<JobProfile> findByCompanyDescription(String companyDescription);

        @Query("SELECT j FROM JobProfile j JOIN j.branch b WHERE LOWER(b.description) = LOWER(?1)")
        List<JobProfile> findByBranchDescription(String branchDescription);

        static final String WHERE_CLAUSE = """
                        (LOWER(j.jobDescription) LIKE %:q%) OR
                        (LOWER(j.company.description) LIKE %:q%) OR
                        (LOWER(j.branch.description) LIKE %:q%)
                        """;

        @Query(value = "SELECT j FROM JobProfile j WHERE "
                        + WHERE_CLAUSE, countQuery = "SELECT COUNT(j) FROM JobProfile j WHERE " + WHERE_CLAUSE)
        Page<JobProfile> omniSearch(@Param("q") String q, Pageable pageable);
}
