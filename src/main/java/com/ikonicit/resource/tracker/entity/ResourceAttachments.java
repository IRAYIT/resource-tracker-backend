package com.ikonicit.resource.tracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Component
@Scope(scopeName = "prototype")
@Table(name = "resource_attachments")
public class ResourceAttachments implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name="attachment_id")
   private Integer attachmentId;

   @ManyToOne(fetch=FetchType.EAGER)
   @JoinColumn(name="resource_id", referencedColumnName="id")
   @JsonBackReference
   private Resource resource;

   @Column(name = "file_name")
   private String fileName;
   @Column(name = "content_type")
   private String contentType;
   @Lob
   private byte[] attachment;
   @Column(name = "created_at")
   private Date createdAt;
   @Column(name = "created_by")
   private String createdBy;
   @Column(name = "updated_at")
   private Date updatedAt;
   @Column(name = "updated_by")
   private String updatedBy;





}
