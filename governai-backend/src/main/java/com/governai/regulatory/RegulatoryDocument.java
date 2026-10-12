package com.governai.regulatory;
import com.governai.policy.DocumentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
@Entity @Table(name = "regulatory_document")
public class RegulatoryDocument {
 @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
 @Column(nullable=false,length=255) private String title;
 @Column(name="document_code",length=100) private String documentCode;
 @Column(nullable=false,length=100) private String jurisdiction;
 @Column(length=255) private String authority; @Column(length=100) private String category; @Column(length=50) private String version;
 @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM) @Column(nullable=false,columnDefinition="document_status_enum") private DocumentStatus status=DocumentStatus.ACTIVE;
 @Column(name="effective_date") private LocalDate effectiveDate; @Column(name="source_url",columnDefinition="text") private String sourceUrl;
 @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition="jsonb") private JsonNode metadata;
 @Column(name="created_at",nullable=false) private OffsetDateTime createdAt; @Column(name="updated_at",nullable=false) private OffsetDateTime updatedAt;
 @PrePersist void create(){var now=OffsetDateTime.now();if(createdAt==null)createdAt=now;if(updatedAt==null)updatedAt=now;if(status==null)status=DocumentStatus.ACTIVE;} @PreUpdate void update(){updatedAt=OffsetDateTime.now();}
 public UUID getId(){return id;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getDocumentCode(){return documentCode;} public void setDocumentCode(String v){documentCode=v;} public String getJurisdiction(){return jurisdiction;} public void setJurisdiction(String v){jurisdiction=v;} public String getAuthority(){return authority;} public void setAuthority(String v){authority=v;} public String getCategory(){return category;} public void setCategory(String v){category=v;} public String getVersion(){return version;} public void setVersion(String v){version=v;} public DocumentStatus getStatus(){return status;} public void setStatus(DocumentStatus v){status=v;} public LocalDate getEffectiveDate(){return effectiveDate;} public void setEffectiveDate(LocalDate v){effectiveDate=v;} public String getSourceUrl(){return sourceUrl;} public void setSourceUrl(String v){sourceUrl=v;} public JsonNode getMetadata(){return metadata;} public void setMetadata(JsonNode v){metadata=v;} public OffsetDateTime getCreatedAt(){return createdAt;} public OffsetDateTime getUpdatedAt(){return updatedAt;}
}
