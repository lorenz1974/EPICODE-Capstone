package capstone.be.city;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    boolean existsByCityStateAndProvince(String city, String province);

    Optional<City> findByCityStateAndProvince(String city, String province);

    // @Query("SELECT c FROM City c WHERE LOWER(c.cityState) LIKE %?1%")
    Page<City> findByCityStateStartsWithIgnoreCase(String query, Pageable pageable);

    @Query("SELECT c FROM City c WHERE LOWER(c.province) LIKE CONCAT(:province, '%')")
    Page<City> findByProvinceStartsWithIgnoreCase(@Param("province") String province, Pageable pageable);

    static final String WHERE_CLAUSE = """
            (LOWER(c.cityState) LIKE %:q%) OR
            (LOWER(c.province) LIKE %:q%) OR
            (LOWER(c.region) LIKE %:q%) OR
            (LOWER(c.state) LIKE %:q%)
            """;

    @Query(value = "SELECT c FROM City c WHERE " + WHERE_CLAUSE, countQuery = "SELECT COUNT(c) FROM City c WHERE "
            + WHERE_CLAUSE)
    Page<City> omniSearch(@Param("q") String q, Pageable pageable);

    @Query("SELECT COUNT(c) FROM City c WHERE c.id < 10000")
    long countByIdLess10000();

    @Query("SELECT c FROM City c WHERE c.id < 10000")
    List<City> findByIdLess10000();
}
