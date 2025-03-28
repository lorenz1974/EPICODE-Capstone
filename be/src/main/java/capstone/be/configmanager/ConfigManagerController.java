package capstone.be.configmanager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ObjectInputFilter.Config;

import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PagedResourcesAssembler;

import capstone.be.config.GetVariables;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Slf4j
@DependsOn("configManager")
public class ConfigManagerController {

    private final ConfigManagerService configManagerService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<ConfigVariable> pagedResourcesAssembler;

    // @GetMapping("/{variableName}")
    // @PreAuthorize("hasRole('ADMIN') or #variableName.startsWith('fe_')")
    // @ResponseStatus(HttpStatus.OK)
    // public ResponseEntity<ConfigVariable> getVariableByName(@PathVariable String
    // variableName) {
    // log.debug("Received request to get configuration: {}", variableName);
    // ConfigVariable value = configManagerService.findByVariableName(variableName);
    // return ResponseEntity.ok(value);
    // }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ConfigVariable> getVariableById(@PathVariable Long id) {
        log.debug("Received request to get configuration variable with id: {}", id);
        ConfigVariable value = configManagerService.findByVariableId(id);
        return ResponseEntity.ok(value);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagedModel<EntityModel<ConfigVariable>>> getAllConfigurations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "variableName") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "") String q) {
        log.info("Fetching config variable, page: {}, size: {}, sortBy: {}, direction: {}, q: {}", page, size, sortBy,
                direction, q);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<ConfigVariable> variables = configManagerService.getAllConfigurations(pageable, q);
        PagedModel<EntityModel<ConfigVariable>> pagedModel = pagedResourcesAssembler.toModel(variables);
        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ConfigVariable> setConfiguration(@RequestBody ConfigVariable configVariable) {
        log.info("Received request to create configuration: {}", configVariable.getVariableName());
        ConfigVariable variable = configManagerService.saveConfigVariable(configVariable);
        return ResponseEntity.ok(variable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ConfigVariable> updateConfiguration(@PathVariable Long id,
            @RequestBody ConfigVariable variable) {
        log.info("Received request to update configuration: {}", id);
        ConfigVariable updatedVariable = configManagerService.updateConfigVariable(id, variable);
        return ResponseEntity.ok(updatedVariable);
    }
}
