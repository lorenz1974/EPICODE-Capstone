package capstone.be.doctemplate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DocTemplateRepository extends JpaRepository<DocTemplate, Long> {

        boolean existsByNameOrSubject(String name, String subject);

        Optional<DocTemplate> findByNameOrSubject(String name, String subject);

        static final String WHERE_CLAUSE = """
                        (LOWER(d.name) LIKE %:q%) OR
                        (LOWER(d.subject) LIKE %:q%) OR
                        (LOWER(d.dataTemplateClass) LIKE %:q%) OR
                        (LOWER(d.templatePath) LIKE %:q%) OR
                        (LOWER(d.templateProvider) LIKE %:q%) OR
                        (LOWER(d.templateProviderApiEndpoint) LIKE %:q%) OR
                        (LOWER(d.templateApiCrudType) LIKE %:q%)
                        """;

        @Query(value = "SELECT d FROM DocTemplate d WHERE "
                        + WHERE_CLAUSE, countQuery = "SELECT COUNT(d) FROM DocTemplate d WHERE " + WHERE_CLAUSE)
        Page<DocTemplate> omniSearch(@Param("q") String q, Pageable pageable);

}
