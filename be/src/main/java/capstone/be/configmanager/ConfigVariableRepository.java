package capstone.be.configmanager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConfigVariableRepository extends JpaRepository<ConfigVariable, Long> {

    Optional<ConfigVariable> findByVariableName(String variableName);

    @Query("SELECT c FROM ConfigVariable c WHERE " +
            "LOWER(c.variableName) LIKE %?1% OR " +
            "LOWER(c.value) LIKE %?1% OR " +
            "LOWER(c.type) LIKE %?1%")
    Page<ConfigVariable> omniSearch(String query, Pageable pageable);
}
