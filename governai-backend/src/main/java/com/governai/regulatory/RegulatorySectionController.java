package com.governai.regulatory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/regulatory-sections") public class RegulatorySectionController {private final RegulatoryService service;public RegulatorySectionController(RegulatoryService service){this.service=service;}@PostMapping @ResponseStatus(HttpStatus.CREATED) public RegulatorySectionResponse create(@Valid @RequestBody RegulatorySectionRequest r){return service.createSection(r);}@GetMapping("/{id}") public RegulatorySectionResponse get(@PathVariable UUID id){return service.getSection(id);}@GetMapping("/document/{documentId}") public List<RegulatorySectionResponse> list(@PathVariable UUID documentId){return service.listSections(documentId);}@PutMapping("/{id}") public RegulatorySectionResponse update(@PathVariable UUID id,@Valid @RequestBody RegulatorySectionRequest r){return service.updateSection(id,r);}@DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id){service.deleteSection(id);}}
