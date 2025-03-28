package capstone.be.sectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedResourcesAssembler;

import capstone.be.config.GetVariables;
import capstone.be.contract.Contract;
import capstone.be.contract.ContractService;

@RestController
@RequestMapping("/api/sectors")
@RequiredArgsConstructor
@Slf4j
public class SectorController {

    private final SectorService sectorService;
    private final ContractService contractService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<Sector> pagedResourcesAssembler;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Sector>>> getAllSectors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "description") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "") String q) {
        log.debug("Fetching sectors, page: {}, size: {}, sortBy: {}, direction: {}, query: {}", page, size, sortBy,
                direction, q);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Sector> sectors = sectorService.getAllSectors(pageable, q);
        PagedModel<EntityModel<Sector>> pagedModel = pagedResourcesAssembler.toModel(sectors);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sector> getContractsBySectorId(@PathVariable Long id) {
        Sector sector = sectorService.getBySectorId(id);
        return ResponseEntity.ok(sector);
    }
}
