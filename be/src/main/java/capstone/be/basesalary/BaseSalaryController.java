package capstone.be.basesalary;

import capstone.be.config.GetVariables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/basesalaries")
@RequiredArgsConstructor
@Slf4j
public class BaseSalaryController {

    private final BaseSalaryService contractService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<BaseSalary> pagedResourcesAssembler;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<BaseSalary>>> getBaseSalary(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "baseSalary") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(required = false) String q) {

        log.info("Fetching base salaries, page: {}, sortBy: {}, direction: {}, query: {}", page, sortBy, direction, q);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<BaseSalary> baseSalaries = contractService.getAllBaseSalaries(pageable, q);
        PagedModel<EntityModel<BaseSalary>> pagedModel = pagedResourcesAssembler.toModel(baseSalaries);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseSalary> getBaseSalaryById(@PathVariable Long id) {
        BaseSalary baseSalary = contractService.getBaseSalaryById(id);
        return ResponseEntity.ok(baseSalary);
    }

    /*
     * @PostMapping
     * public ResponseEntity<Void> createBaseSalary(@RequestBody BaseSalaryRequest
     * request) {
     * log.debug("Received request to create a base salary: {}",
     * request.getBaseSalary());
     * contractService.createBaseSalary(request);
     * return ResponseEntity.ok().build();
     * }
     */
}
