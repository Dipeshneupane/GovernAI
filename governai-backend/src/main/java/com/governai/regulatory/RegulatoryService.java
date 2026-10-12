package com.governai.regulatory;
import com.governai.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service public class RegulatoryService {
 private final RegulatoryDocumentRepository documents; private final RegulatorySectionRepository sections;
 public RegulatoryService(RegulatoryDocumentRepository documents,RegulatorySectionRepository sections){this.documents=documents;this.sections=sections;}
 @Transactional public RegulatoryDocumentResponse createDocument(RegulatoryDocumentRequest r){RegulatoryDocument d=new RegulatoryDocument();apply(d,r);return RegulatoryDocumentResponse.from(documents.save(d));}
 @Transactional(readOnly=true) public RegulatoryDocumentResponse getDocument(UUID id){return RegulatoryDocumentResponse.from(document(id));}
 @Transactional(readOnly=true) public List<RegulatoryDocumentResponse> listDocuments(String jurisdiction){List<RegulatoryDocument> result=jurisdiction==null?documents.findAll():documents.findByJurisdictionIgnoreCase(jurisdiction);return result.stream().map(RegulatoryDocumentResponse::from).toList();}
 @Transactional public RegulatoryDocumentResponse updateDocument(UUID id,RegulatoryDocumentRequest r){RegulatoryDocument d=document(id);apply(d,r);return RegulatoryDocumentResponse.from(documents.save(d));}
 @Transactional public void deleteDocument(UUID id){documents.delete(document(id));}
 @Transactional public RegulatorySectionResponse createSection(RegulatorySectionRequest r){RegulatorySection s=new RegulatorySection();apply(s,r);return RegulatorySectionResponse.from(sections.save(s));}
 @Transactional(readOnly=true) public RegulatorySectionResponse getSection(UUID id){return RegulatorySectionResponse.from(section(id));}
 @Transactional(readOnly=true) public List<RegulatorySectionResponse> listSections(UUID documentId){document(documentId);return sections.findByRegulatoryDocument_IdOrderBySectionNumberAsc(documentId).stream().map(RegulatorySectionResponse::from).toList();}
 @Transactional public RegulatorySectionResponse updateSection(UUID id,RegulatorySectionRequest r){RegulatorySection s=section(id);apply(s,r);return RegulatorySectionResponse.from(sections.save(s));}
 @Transactional public void deleteSection(UUID id){sections.delete(section(id));}
 private RegulatoryDocument document(UUID id){return documents.findById(id).orElseThrow(()->new ResourceNotFoundException("Regulatory document",id));}
 private RegulatorySection section(UUID id){return sections.findById(id).orElseThrow(()->new ResourceNotFoundException("Regulatory section",id));}
 private void apply(RegulatoryDocument d,RegulatoryDocumentRequest r){d.setTitle(r.title());d.setDocumentCode(r.documentCode());d.setJurisdiction(r.jurisdiction());d.setAuthority(r.authority());d.setCategory(r.category());d.setVersion(r.version());if(r.status()!=null)d.setStatus(r.status());d.setEffectiveDate(r.effectiveDate());d.setSourceUrl(r.sourceUrl());d.setMetadata(r.metadata());}
 private void apply(RegulatorySection s,RegulatorySectionRequest r){s.setRegulatoryDocument(document(r.regulatoryDocumentId()));s.setSectionNumber(r.sectionNumber());s.setHeading(r.heading());s.setContent(r.content());s.setMetadata(r.metadata());}
}
