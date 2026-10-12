package com.governai.regulatory;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;
import java.time.OffsetDateTime;
import java.util.UUID;
@Entity @Table(name="regulatory_section")
public class RegulatorySection { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="regulatory_document_id",nullable=false) private RegulatoryDocument regulatoryDocument; @Column(name="section_number",length=50) private String sectionNumber; @Column(length=255) private String heading; @Column(nullable=false,columnDefinition="text") private String content; @JdbcTypeCode(SqlTypes.JSON) @Column(columnDefinition="jsonb") private JsonNode metadata; @Column(name="created_at",nullable=false) private OffsetDateTime createdAt; @PrePersist void create(){if(createdAt==null)createdAt=OffsetDateTime.now();} public UUID getId(){return id;} public RegulatoryDocument getRegulatoryDocument(){return regulatoryDocument;} public void setRegulatoryDocument(RegulatoryDocument v){regulatoryDocument=v;} public String getSectionNumber(){return sectionNumber;} public void setSectionNumber(String v){sectionNumber=v;} public String getHeading(){return heading;} public void setHeading(String v){heading=v;} public String getContent(){return content;} public void setContent(String v){content=v;} public JsonNode getMetadata(){return metadata;} public void setMetadata(JsonNode v){metadata=v;} public OffsetDateTime getCreatedAt(){return createdAt;} }
