package capstone.be.city;

import capstone.be.config.GetVariables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedResourcesAssembler;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
@Slf4j
@DependsOn("configManager")
public class CityController {

    private final CityService cityService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<City> pagedResourcesAssembler;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<City>>> getCities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "cityState") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(required = false) String q) {

        log.info("Fetching cities, page: {}, sortBy: {}, direction: {}, query: {}", page, sortBy, direction, q);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<City> cities = cityService.getCitiesByCityState(pageable, sortBy, q);
        PagedModel<EntityModel<City>> pagedModel = pagedResourcesAssembler.toModel(cities);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/provinces")
    public ResponseEntity<PagedModel<EntityModel<City>>> getCitiesByProvince(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "500") int size,
            @RequestParam(defaultValue = "cityState") String sortBy,
            @RequestParam(required = false) String q) {

        log.info("Fetching cities by province, page: {}, size: {}, sortBy: {}, query: {}", page, size, sortBy, q);
        Pageable pageable = PageRequest.of(page, size);
        Page<City> cities = cityService.getCitiesByProvince(pageable, sortBy, q);
        PagedModel<EntityModel<City>> pagedModel = pagedResourcesAssembler.toModel(cities);
        return ResponseEntity.ok(pagedModel);
    }
}
