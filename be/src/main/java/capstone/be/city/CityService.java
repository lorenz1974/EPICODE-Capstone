package capstone.be.city;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

    private final CityRepository cityRepository;

    public Page<City> getCitiesByCityState(Pageable pageable, String sortBy, String q) {
        log.debug("Fetching cities by cityState with query '{}'", q);

        if (q == null || q.isEmpty()) {
            return cityRepository.findAll(pageable);
        }
        return cityRepository.findByCityStateStartsWithIgnoreCase(q.toLowerCase(), pageable);
    }

    public Page<City> getCitiesByProvince(Pageable pageable, String sortBy, String q) {
        log.debug("Fetching cities by province with query '{}'", q);

        if (q == null || q.isEmpty()) {
            return cityRepository.findAll(pageable);
        }
        return cityRepository.findByProvinceStartsWithIgnoreCase(q.toLowerCase(), pageable);
    }

    public City findRandomCity() {
        log.debug("Fetching a random city");
        List<City> cities = cityRepository.findByIdLess10000();
        int randomIndex = (int) (Math.random() * cities.size());
        return cities.get(randomIndex);
    }

    public City getCityById(Long id) {
        return cityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("City not found"));
    }
}
