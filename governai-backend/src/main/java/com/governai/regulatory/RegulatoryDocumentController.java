package com.governai.regulatory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/regulatory-documents") public class RegulatoryDocumentController {private final RegulatoryService service;public RegulatoryDocumentController(RegulatoryService service){this.service=service;}@PostMapping @ResponseStatus(HttpStatus.CREATED) public RegulatoryDocumentResponse create(@Valid @RequestBody RegulatoryDocumentRequest r){return service.createDocument(r);}@GetMapping("/{id}") public RegulatoryDocumentResponse get(@PathVariable UUID id){return service.getDocument(id);}@GetMapping public List<RegulatoryDocumentResponse> list(@RequestParam(required=false) String jurisdiction){return service.listDocuments(jurisdiction);}@PutMapping("/{id}") public RegulatoryDocumentResponse update(@PathVariable UUID id,@Valid @RequestBody RegulatoryDocumentRequest r){return service.updateDocument(id,r);}@DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id){service.deleteDocument(id);}}
