package capstone.be.doctemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocTemplateService {

    private final DocTemplateRepository docTemplateRepository;

    public Page<DocTemplate> getDocTemplates(Pageable pageable, String sortBy, String q, boolean alsoDeleted) {
        log.debug("Fetching doctemplates by query with query '{}'", q);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(sortBy));
        if (q == null || q.isEmpty()) {
            return docTemplateRepository.findAll(pageable);
        }
        return docTemplateRepository.omniSearch(q.toLowerCase(), sortedPageable);
    }

    public DocTemplate getDocTemplateById(long id) {
        log.debug("Fetching doc template with id '{}'", id);
        return docTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DocTemplate not found"));
    }

    public DocTemplate saveDocTemplate(DocTemplate docTemplate) {
        log.debug("Saving doc template with id '{}'", docTemplate.getId());
        return docTemplateRepository.save(docTemplate);
    }

    public void deleteDocTemplate(long id) {
        log.debug("Deleting doc template with id '{}'", id);
        DocTemplate docTemplate = getDocTemplateById(id);
        docTemplate.setDeleted(true);
        docTemplateRepository.save(docTemplate);
    }

    public DocTemplate updateDocTemplate(long id, DocTemplate docTemplate) {
        log.debug("Updating doc template with id '{}'", id);
        DocTemplate existingDocTemplate = getDocTemplateById(id);
        BeanUtils.copyProperties(docTemplate, existingDocTemplate);

        return docTemplateRepository.save(existingDocTemplate);
    }
}
