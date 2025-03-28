package capstone.be.doctemplate;

import capstone.be.config.GetVariables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.query.SortDirection;
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
@RequestMapping("/api/doctemplates")
@RequiredArgsConstructor
@Slf4j
@DependsOn("configManager")
public class DocTemplateController {

    private final DocTemplateService docTemplateService;
    private final GetVariables getVariables;
    private final PagedResourcesAssembler<DocTemplate> pagedResourcesAssembler;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<DocTemplate>>> getDocTemplates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "false") boolean alsoDeleted,
            @RequestParam(required = false) String q) {

        log.debug("Fetching doc templates, page: {}, sortBy: {}, query: {}, alsoDeleted: {}", page, sortBy, q,
                alsoDeleted);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<DocTemplate> docTemplates = docTemplateService.getDocTemplates(pageable, sortBy, q, alsoDeleted);
        PagedModel<EntityModel<DocTemplate>> pagedModel = pagedResourcesAssembler.toModel(docTemplates);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DocTemplate>> getDocTemplate(@PathVariable long id) {
        log.debug("Fetching doc template with id '{}'", id);
        DocTemplate docTemplate = docTemplateService.getDocTemplateById(id);
        return ResponseEntity.ok(EntityModel.of(docTemplate));
    }
}
