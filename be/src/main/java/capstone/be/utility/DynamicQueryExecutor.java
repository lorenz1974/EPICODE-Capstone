package capstone.be.utility;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.*;

//
// Repository class for executing native SQL queries and returning structured results.
//
@Repository
@RequiredArgsConstructor
public class DynamicQueryExecutor {

    @PersistenceContext
    private EntityManager entityManager;

    //
    // Executes a native SQL query and returns results as a list of maps.
    // Each map represents a record with column names as keys and values as data.
    //
    // @param sql the SQL query to execute
    // @return a list of maps with query results
    //
    public List<Map<String, Object>> executeNativeQuery(String sql) {
        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        List<Tuple> results = query.getResultList();

        List<Map<String, Object>> mappedResults = new ArrayList<>();

        for (Tuple tuple : results) {
            Map<String, Object> row = new HashMap<>();
            for (TupleElement<?> element : tuple.getElements()) {
                row.put(element.getAlias(), tuple.get(element));
            }
            mappedResults.add(row);
        }

        return mappedResults;
    }
}
