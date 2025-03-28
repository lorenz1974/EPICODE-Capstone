package capstone.be.filesstore;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, UUID> {

    // Frammento riutilizzabile della query
    static final String BASE_SEARCH = """
                FROM FileEntity f
                WHERE LOWER(f.originalFilename) LIKE %:query%
                   OR LOWER(f.extension) LIKE %:query%
                   OR LOWER(f.mimeType) LIKE %:query%
                   OR LOWER(f.fatherType) LIKE %:query%
            """;

    @Query(value = "SELECT f " + BASE_SEARCH, countQuery = "SELECT COUNT(f) " + BASE_SEARCH)
    Page<FileEntity> omniSearch(String query, Pageable pageable);
}
