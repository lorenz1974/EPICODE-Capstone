package capstone.be.contract;

import capstone.be.config.GetVariables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedResourcesAssembler;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Slf4j
public class ContractController {

    private final ContractService contractService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<Contract> pagedResourcesAssembler;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagedModel<EntityModel<Contract>>> getContracts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "level") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(required = false) String q) {

        log.info("Fetching contracts, page: {}, sortBy: {}, direction: {}, query: {}", page, sortBy, direction, q);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Contract> contracts = contractService.getAllContracts(pageable, q);
        PagedModel<EntityModel<Contract>> pagedModel = pagedResourcesAssembler.toModel(contracts);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Contract> getContractById(@PathVariable Long id) {
        Contract contract = contractService.getContractById(id);
        return ResponseEntity.ok(contract);
    }

    @GetMapping("/sectors/{sectorId}")
    public ResponseEntity<PagedModel<EntityModel<Contract>>> getContractBySectorId(
            @PathVariable Long sectorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "level") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {

        log.debug("Fetching contracts for sectorId: {}, page: {}, sortBy: {}, direction: {}", sectorId, page,
                sortBy, direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Contract> contracts = contractService.getContractBySectorId(sectorId, pageable);
        PagedModel<EntityModel<Contract>> pagedModel = pagedResourcesAssembler.toModel(contracts);
        return ResponseEntity.ok(pagedModel);
    }
}

/*
 * @PostMapping
 * public ResponseEntity<Void> createContract(@RequestBody ContractRequest
 * request) {
 * log.debug("Received request to create a contract: {}", request.getLevel());
 * contractService.createContract(request);
 * return ResponseEntity.ok().build();
 * }
 */
