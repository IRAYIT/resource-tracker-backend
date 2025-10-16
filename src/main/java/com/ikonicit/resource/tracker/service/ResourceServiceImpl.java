package com.ikonicit.resource.tracker.service;

import com.google.gson.Gson;
import com.ikonicit.resource.tracker.dto.ProjectDTO;
import com.ikonicit.resource.tracker.dto.ResourceAttachmentDTO;
import com.ikonicit.resource.tracker.dto.ResourceDTO;
import com.ikonicit.resource.tracker.dto.SendEmailDTO;
import com.ikonicit.resource.tracker.entity.*;
import com.ikonicit.resource.tracker.exception.MailSendFailedException;
import com.ikonicit.resource.tracker.exception.ResourceNotFoundException;
import com.ikonicit.resource.tracker.predicates.Predicates;
import com.ikonicit.resource.tracker.repository.ResourceAttachmentsRepository;
import com.ikonicit.resource.tracker.repository.ResourceRepository;
import com.ikonicit.resource.tracker.utils.Constants;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.internet.AddressException;

import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of Resource Add,Update,Delete and Get Operations
 *
 * @author Parasuram
 * @since 30-10-2020
 */
@Slf4j
@Service
public class ResourceServiceImpl implements ResourceService {
    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceAttachmentsRepository resourceAttachmentsRepository;


    Predicate<Object> isNotNull = Predicates.isNotNull;

    Predicate<Object> isNull = Predicates.isNull;

    @Autowired
    private ObjectFactory<Resource> resourceObjectFactory;

    @Autowired
    private ObjectFactory<Permission> permissionObjectFactory;

    @Autowired
    private ObjectFactory<ResourceAttachments> attachmentsObjectFactory;

    @Autowired
    private ObjectFactory<ResourceAttachmentDTO> attachmentsDTOObjectFactory;

    @Autowired
    private ObjectFactory<ResourceDTO> resourceDTOObjectFactory;

    @Autowired
    private ObjectFactory<ProjectDTO> projectDTOObjectFactory;

    @Autowired
    private ObjectFactory<Credentials> credentialsObjectFactory;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.openingsEmailEnabled}")
    private Boolean openingsEmailEnabled;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.host}")
    private String port;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String enable;

    @Value("${spring.mail.password}")
    private String senderPassword;


    /**
     * Creates the resource.
     *
     * @param attachments it can be resource CV or Cover Letter
     * @param payload     it has all the fields to create resource and converted into string
     * @return ResourceDTO it returns the created resource properties
     */
    @Override
    @Caching(evict = {@CacheEvict(value = "resource", allEntries = true),
            @CacheEvict(value = "resourceByName", allEntries = true),
            @CacheEvict(value = "resources", allEntries = true),
            @CacheEvict(value = "managers", allEntries = true),
            @CacheEvict(value = "managerResources", allEntries = true)})
    public ResourceDTO create(List<MultipartFile> attachments, String payload) throws AddressException {
        ResourceDTO resourceDTO = getResourceDTO(payload);
        Resource resource = resourceRepository.save(createResource(resourceDTO, attachments));
        if (resourceDTO.getPermissionId() != null && resourceDTO.getPermissionId() == 2) {
            assignEmployeesToManager(resource.getId(), resourceDTO.getAssignedResourceIds());
        }
        return sendEmailToResource(resource);
    }

    /**
     * Updates the resource.
     *
     * @param attachments it can be resource CV or Cover Letter
     * @param payload     it has all the fields to update resource and converted into string
     * @return ResourceDTO it returns the updated resource properties
     * @throws ParseException
     */
    @Override
    @Caching(evict = {@CacheEvict(value = "resource", allEntries = true),
            @CacheEvict(value = "resourceByName", allEntries = true),
            @CacheEvict(value = "resources", allEntries = true),
            @CacheEvict(value = "managers", allEntries = true),
            @CacheEvict(value = "managerResources", allEntries = true)})
    public ResourceDTO update(List<MultipartFile> attachments, String payload) throws ParseException {
        ResourceDTO resourceDTO = getResourceDTO(payload);
        ResourceDTO source = null;
        Optional<Resource> resourceOptional = resourceRepository.findById(resourceDTO.getId());
        if (resourceOptional.isPresent()) {
            source = buildResourceDTO(resourceOptional.get());
        }
        Resource resource = resourceRepository.save(createResource(resourceDTO, attachments));
        if (resourceDTO.getPermissionId() != null && resourceDTO.getPermissionId() == 2) {
            assignEmployeesToManager(resource.getId(), resourceDTO.getAssignedResourceIds());
        }
        buildUpdateEmail(resourceDTO, source);
        return buildResourceDTO(resource);
    }


    /**
     * Get the resource.
     *
     * @param id resourceId used to get the resource
     * @return ResourceDTO it returns the given resource by id
     */
    @Override
    @Cacheable("resource")
    public ResourceDTO getResource(Integer id) {
        Optional<Resource> resourceOptional = resourceRepository.findById(id);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException("Resource Not Found in the database");
        }
        return buildResourceDTO(resourceOptional.get());
    }

    /**
     * List of resources.
     *
     * @return List<ResourceDTO> it returns the list of resources
     */
    @Override
    @Cacheable("resources")
    public List<ResourceDTO> getAll() {
        List<Resource> resourceList = resourceRepository.findAll();
        if (!resourceList.isEmpty()) {
            return buildResourcesDTO(resourceList);
        }
        return Collections.emptyList();
    }


    /**
     * Delete the resource.
     *
     * @param id resourceId used to delete the resource
     * @return boolean it returns the true if resource deleted
     */
    @Override
    @Caching(evict = {@CacheEvict(value = "resource", allEntries = true),
            @CacheEvict(value = "resources", allEntries = true),
            @CacheEvict(value = "resourceByName", allEntries = true),
            @CacheEvict(value = "managers", allEntries = true),
            @CacheEvict(value = "managerResources", allEntries = true)})
    public boolean deleteResource(Integer id) {
        Optional<Resource> resourceOptional = resourceRepository.findById(id);
        if (!resourceOptional.isPresent()) {
            throw new ResourceNotFoundException("Resource Not Found in the database");
        }
        Resource resource = resourceOptional.get();
        resource.setStatus(Constants.TERMINATED);
        resourceRepository.save(resource);
        log.info("Resource Deleted Succesfully");
        return true;
    }


    /**
     * Get all resources By Manager.
     *
     * @param managerId used to get the resources by managerId
     * @return List<ResourceDTO> it returns the list of resources of given manager
     */
    @Override
    @Cacheable("managerResources")
    public List<ResourceDTO> resourcesByManager(Integer managerId) {
        List<Resource> managerResources = resourceRepository.findByManagerIdAndStatusNotOrderByIdDesc(managerId, "TERMINATED");
        if (!managerResources.isEmpty()) {
            log.info("Manager resources found", managerResources);
            return buildResourcesDTO(managerResources);
        }
        return Collections.emptyList();
    }

    @Override
    public List<ResourceDTO> getUnassignedResources() {
        List<Resource> unassignedResources = resourceRepository.findByManagerIdIsNull();
        return unassignedResources.stream()
                .map(resource -> {
                    ResourceDTO resourceDTO = new ResourceDTO();
                    BeanUtils.copyProperties(resource,resourceDTO);
                    resourceDTO.setPermissionId(resource.getPermission().getId());
                    return resourceDTO;
                }).toList();
    }

    private void assignEmployeesToManager(Integer managerId, List<Integer> resourceIds) {
        if (resourceIds != null && !resourceIds.isEmpty()) {
            List<Resource> employees = resourceRepository.findAllById(resourceIds);
            for (Resource employee : employees) {
                employee.setManager(new Resource(managerId));
            }
            resourceRepository.saveAll(employees);
        }
    }


    /**
     * Get ResourceAttachment by attachmentId
     *
     * @param attachmentId used to get the attachment
     * @return ResourceAttachments it returns the attachment
     */
    @Override
    @Cacheable("resourceAttachment")
    public ResourceAttachments getAttachment(Integer attachmentId) {
        if (isNull.test(resourceAttachmentsRepository.findById(attachmentId))) {
            throw new ResourceNotFoundException("Attachment Not Found for the given attachmentId");
        }
        log.debug("Attachment Found : {}", attachmentId);
        Optional<ResourceAttachments> optionalResource = resourceAttachmentsRepository.findById(attachmentId);
        return optionalResource.get();
    }

    /**
     * Get allManagers
     *
     * @return List<ResourceDTO> it returns all resources
     */
    @Override
    @Cacheable("managers")
    public List<ResourceDTO> getAllManagers() {
        List<Resource> managers = resourceRepository.findAllByPermissionIdEqualsAndStatusEquals(2, "ACTIVE");
        if (!managers.isEmpty()) {
            log.info("managers Found");
            return buildManagersDTO(managers);
        }
        return Collections.emptyList();
    }

    @Override
    @Cacheable("resourceByName")
    public ResourceDTO findByResourceName(String resourceName) {
        Resource resource = resourceRepository.findByResourceName(resourceName);
        if (isNull.test(resource)) {
            throw new ResourceNotFoundException("Resource Not Found in the database");
        }
        return buildResourceDTO(resource);
    }

    @Override
    public String checkResourceName(String resourceName) {
        Resource resource = resourceRepository.findByResourceName(resourceName);
        if (isNull.test(resource)) {
            return "ResourceName Available";
        }
        return "ResourceName Already Exists";
    }

    @Override
    public String sendEmail(SendEmailDTO sendEmailDTO) {
        String msg = "";
        if (openingsEmailEnabled) {
            if (sendEmailDTO.getIsTrue()) {
              List<String> emails = resourceRepository.findEmails(Constants.TERMINATED);
                if (!emails.isEmpty() && isNotNull.test(emails)) {
                    String subject = sendEmailDTO.getSubject();
                    String emailBody = sendEmailDTO.getEmailBody();
                    emails.forEach(email -> {
                        sendMail(email, subject, emailBody);
                    });
                }
                msg = "Mail Sent to all Resources";
            }
            else {
                sendMail(sendEmailDTO.getEmail(), sendEmailDTO.getSubject(),
                        sendEmailDTO.getEmailBody());
                msg = "Mail Sent";
            }
            return msg;
        }
        return msg;
    }

    private void sendMail(String email, String subject, String emailBody) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setBcc("parasuramyerramsetty@gmail.com");
            message.setSubject(subject);
            message.setText(emailBody);
            javaMailSender.send(message);
        } catch (MailSendFailedException exception) {
            log.error("Exception occurred during sending an email to the applicant\t{}", email);
            throw new MailSendFailedException("Exception occurred during sending an email to the applicant might be Email Wrong or Mail Server Down");
        }
    }

    @Override
    public String checkEmail(String email) {
        Resource resource = resourceRepository.findByEmail(email);
        if (isNull.test(resource)) {
            return "Email Available";
        }
        return "Email Already Exists";
    }

    private ResourceDTO getResourceDTO(String payload) {
        Gson gson = new Gson();
        ResourceDTO resourceDTO = gson.fromJson(payload, ResourceDTO.class);
        return resourceDTO;
    }

    private Resource createResource(ResourceDTO resourceDTO, List<MultipartFile> attachments) {
        Resource resource = resourceObjectFactory.getObject();
        BeanUtils.copyProperties(resourceDTO, resource);
        resource.setLinkedin(resourceDTO.getLinkedin());
        resource.setClient(resourceDTO.getClient());
        resource.setAttachments(buildAttachments(resource, attachments));
        Credentials credentials = buildCredentials(resourceDTO.getEmail(), resourceDTO.getCreatedAt(), resourceDTO.getCreatedBy(),
                resourceDTO.getUpdatedAt(), resourceDTO.getUpdatedBy());
        credentials.setId(resource.getId());
        resource.setCredentials(credentials);
        credentials.setResource(resource);
        Permission permission = permissionObjectFactory.getObject();
        permission.setId(resourceDTO.getPermissionId());
        resource.setPermission(permission);
        resource.setCreatedBy("ADMIN");
        resource.setUpdatedBy("ADMIN");
        List<Integer> assignedResources = resourceRepository.findByManagerIdIsNull()
                .stream()
                .map(Resource::getId)
                .toList();
        resourceDTO.setAssignedResourceIds(assignedResources);
        Integer managerId = resourceDTO.getManagerId();
        if (Predicates.isIdNotEmpty.test(managerId)) {
            Resource manager = resourceObjectFactory.getObject();
            manager.setId(managerId);
            resource.setManager(manager);
        } else {
            resource.setManager(null);
        }
        if (Boolean.TRUE.equals(resourceDTO.getClient())) {
            resource.setResourceType("Client");
        } else {
            resource.setResourceType(null);
        }
        return resource;
    }

    private List<ResourceDTO> buildResourcesDTO(List<Resource> resources) {
        List<ResourceDTO> managerResourcesDTO = new ArrayList<>();
        resources.forEach(resource -> {
            ResourceDTO resourceDTO = resourceDTOObjectFactory.getObject();
            resourceDTO.setPermissionId(resource.getPermission().getId());
            if (isNotNull.test(resource.getManager())) {
                resourceDTO.setManagerId(resource.getManager().getId());
            }
            //   resourceDTO.setResourceAttachments(buildAttachments(resource.getAttachments()));
            BeanUtils.copyProperties(resource, resourceDTO);
            resourceDTO.setClient(resource.getClient());
            resourceDTO.setResourceType(resource.getResourceType());
            managerResourcesDTO.add(resourceDTO);
        });
        return managerResourcesDTO;
    }

    private List<ResourceDTO> buildManagersDTO(List<Resource> managers) {
        List<ResourceDTO> managersDTO = new ArrayList<>();
        managers.forEach(manager -> {
            ResourceDTO resourceDTO = resourceDTOObjectFactory.getObject();
            BeanUtils.copyProperties(manager, resourceDTO);
            ResourceDTO managerDto = resourceDTOObjectFactory.getObject();
            if (isNotNull.test(manager.getManager())) {
                BeanUtils.copyProperties(manager.getManager(), managerDto);
                resourceDTO.setManager(managerDto);
            }
            resourceDTO.setPermissionId(manager.getPermission().getId());
            managersDTO.add(resourceDTO);
        });
        return managersDTO;
    }

    private List<ResourceAttachmentDTO> buildAttachments(List<ResourceAttachments> attachments) {
        List<ResourceAttachmentDTO> attachmentDTOS = new ArrayList<>();
        attachments.forEach(resourceAttachments -> {
            ResourceAttachmentDTO resourceAttachmentDTO = attachmentsDTOObjectFactory.getObject();
            resourceAttachmentDTO.setAttachmentId(resourceAttachments.getAttachmentId());
            resourceAttachmentDTO.setFileName(resourceAttachments.getFileName());
            attachmentDTOS.add(resourceAttachmentDTO);

        });
        return attachmentDTOS;
    }

    private Resource updateResource(ResourceDTO resourceDTO, List<MultipartFile> attachments) throws ParseException {
        Resource updateResource = null;
        if (resourceRepository.findById(resourceDTO.getId()).isPresent()) {
            updateResource = resourceRepository.findById(resourceDTO.getId()).get();
        }
        BeanUtils.copyProperties(resourceDTO, updateResource);
        updateResource.setLinkedin(resourceDTO.getLinkedin());
        updateResource.setAttachments(buildAttachments(updateResource, attachments));
       /* Credentials credentials = buildCredentials(resourceDTO.getEmail(), resourceDTO.getCreatedAt(), resourceDTO.getCreatedBy(),
                resourceDTO.getUpdatedAt(), resourceDTO.getUpdatedBy());
        updateResource.setCredentials(credentials);
        credentials.setResource(updateResource);*/
        Permission permission = permissionObjectFactory.getObject();
        permission.setId(resourceDTO.getPermissionId());
        updateResource.setPermission(permission);
        if (Predicates.isIdNotEmpty.test(resourceDTO.getManagerId())) {
            Resource manager = resourceObjectFactory.getObject();
            manager.setId(resourceDTO.getManagerId());
            updateResource.setManager(manager);
        } else {
            updateResource.setManager(null);
        }
        return updateResource;
    }

    private Credentials buildCredentials(String email, Date createdAt, String createdBy, Date updatedAt,
                                         String updatedBy) {
        Credentials credentials = credentialsObjectFactory.getObject();
        credentials.setEmail(email);
        credentials.setPassword("RESour$@!ce9");
        credentials.setCreatedAt(createdAt);
        credentials.setCreatedBy(createdBy);
        credentials.setUpdatedAt(updatedAt);
        credentials.setUpdatedBy(updatedBy);
        return credentials;
    }

    private ResourceDTO sendEmailToResource(Resource resource) throws AddressException {
        String to = resource.getEmail();
        String from = senderEmail;
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.enable", enable);
        properties.put("mail.smtp.auth", auth);
        Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Thanks For Applying At Sunshine");
            ByteArrayOutputStream outputStream = null;
            MimeBodyPart textBodyPart = new MimeBodyPart();
            outputStream = new ByteArrayOutputStream();
            writePdf(outputStream, resource);
            byte[] bytes = outputStream.toByteArray();
            DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
            MimeBodyPart pdfBodyPart = new MimeBodyPart();
            pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            pdfBodyPart.setFileName("ResourceDetails.pdf");
            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(textBodyPart);
            mimeMultipart.addBodyPart(pdfBodyPart);
            message.setContent(mimeMultipart);
//            Transport.send(message);
            log.info("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        } finally {
            return buildResourceDTO(resource);
        }
    }

    private void writePdf(ByteArrayOutputStream outputStream, Resource resource) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        document.addTitle("Test PDF");
        document.addSubject("Testing email PDF");
        document.addKeywords("iText, email");
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(resource.getFirstName() + " Thanks for Applying Technology:" + resource.getTechnology()  + "Skill:" + resource.getSkill() + "Experience:" + resource.getExperience() + " Years Position in Sunshine Creative Labs \n"
                + "Please Use these credentials to login Resource Tracker \n"
                + "URL : http://www.resourcetracker.sscreativelabs.com/login" + "\n" + "UserName :" + resource.getEmail() + "\n"
                + "Password : " + resource.getCredentials().getPassword()));
        document.add(paragraph);
        document.close();
    }

    private void buildUpdateEmail(ResourceDTO resourceDTO, ResourceDTO source) {
        StringBuilder email = new StringBuilder();
        if (!source.getSkill().equalsIgnoreCase(resourceDTO.getSkill())) {
            email.append("Skill:" + resourceDTO.getSkill() + "\n");
        }
        if (!source.getTechnology().equalsIgnoreCase(resourceDTO.getTechnology())) {
            email.append("Technology:" + resourceDTO.getTechnology() + "\n");
        }
        if (!source.getComments().equalsIgnoreCase(resourceDTO.getComments())) {
            email.append("Comments:" + resourceDTO.getComments() + "\n");
        }
        if (!source.getEmail().equalsIgnoreCase(resourceDTO.getEmail())) {
            email.append("Email:" + resourceDTO.getEmail() + "\n");
        }
        if (!source.getEmploymentType().equalsIgnoreCase(resourceDTO.getEmploymentType())) {
            email.append("EmploymentType:" + resourceDTO.getEmploymentType() + "\n");
        }
        if (!source.getEndDate().equals(resourceDTO.getEndDate())) {
            email.append("EndDate:" + resourceDTO.getEndDate() + "\n");
        }
        if (!source.getExperience().equals(resourceDTO.getExperience())) {
            email.append("Experience:" + resourceDTO.getExperience() + "\n");
        }
        if (!source.getLinkedin().equalsIgnoreCase(resourceDTO.getLinkedin())) {
            email.append("LinkedIn:" + resourceDTO.getLinkedin() + "\n");
        }
        if (!source.getPhone().equalsIgnoreCase(resourceDTO.getPhone())) {
            email.append("Phone:" + resourceDTO.getPhone() + "\n");
        }
        if (!source.getResourceName().equalsIgnoreCase(resourceDTO.getResourceName())) {
            email.append("Resource Name:" + resourceDTO.getResourceName() + "\n");
        }
        if (!source.getStartDate().equals(resourceDTO.getStartDate())) {
            email.append("StratDate:" + resourceDTO.getStartDate() + "\n");
        }
        if (!source.getStatus().equalsIgnoreCase(resourceDTO.getStatus())) {
            email.append("Status:" + resourceDTO.getStatus() + "\n");
        }
        String to = resourceDTO.getEmail();
        String from = senderEmail;
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.enable", enable);
        properties.put("mail.smtp.auth", auth);
        Session session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        String emailBody= email.toString();
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Profile Update");
            ByteArrayOutputStream outputStream = null;
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText("Your Profile Got Updated");
            outputStream = new ByteArrayOutputStream();
            writePdfForUpdate(outputStream,emailBody);
            byte[] bytes = outputStream.toByteArray();
            DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
            MimeBodyPart pdfBodyPart = new MimeBodyPart();
            pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            pdfBodyPart.setFileName("UpdatedFields.pdf");
            MimeMultipart mimeMultipart = new MimeMultipart();
            mimeMultipart.addBodyPart(textBodyPart);
            mimeMultipart.addBodyPart(pdfBodyPart);
            message.setContent(mimeMultipart);
            Transport.send(message);
            log.info("Sent message successfully....");
        } catch (MessagingException | DocumentException mex) {
            mex.printStackTrace();
        }
    }

    private void writePdfForUpdate(ByteArrayOutputStream outputStream,String emailBody) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        document.addTitle("Test PDF");
        document.addSubject("Testing email PDF");
        document.addKeywords("iText, email");
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(emailBody));
        document.add(paragraph);
        document.close();
    }


    private ResourceDTO buildResourceDTO(Resource resource) {
        if (isNotNull.test(resource)) {
            ResourceDTO resourceDTO = resourceDTOObjectFactory.getObject();
            BeanUtils.copyProperties(resource, resourceDTO);
            if (isNotNull.test(resource.getManager())) {
                resourceDTO.setManagerId(resource.getManager().getId());
            }
            resourceDTO.setPermissionId(resource.getPermission().getId());
            resourceDTO.setLinkedin(resource.getLinkedin());
            resourceDTO.setClient(resource.getClient());
            resourceDTO.setResourceType(resource.getResourceType());
            // resourceDTO.setProjects(buildProjectsDTO(resource.getProjects()));
            return resourceDTO;
        }
        return null;
    }


    private List<ProjectDTO> buildProjectsDTO(List<Project> projects) {
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        if (projects != null) {
            projects.forEach(project -> {
                ProjectDTO projectDTO = projectDTOObjectFactory.getObject();
                BeanUtils.copyProperties(project, projectDTO);
                projectDTOS.add(projectDTO);
            });
        }
        return projectDTOS;
    }

    private List<ResourceAttachments> buildAttachments(Resource resource, List<MultipartFile> attachments) {
        List<ResourceAttachments> resourceAttachments = new ArrayList<>();
        if (!CollectionUtils.isEmpty(attachments)) {
            attachments.stream().forEach(resourceAttachment -> {
                ResourceAttachments attachment = attachmentsObjectFactory.getObject();
                attachment.setFileName(resourceAttachment.getOriginalFilename());
                try {
                    attachment.setAttachment(resourceAttachment.getBytes());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                attachment.setResource(resource);
                attachment.setContentType(resourceAttachment.getContentType());
                attachment.setCreatedAt(new Date());
                if (Predicates.isIdNotEmpty.test(resource.getId()))
                    attachment.setCreatedBy(resource.getUpdatedBy());
                else
                    attachment.setCreatedBy(resource.getCreatedBy());
                resourceAttachments.add(attachment);

            });
        }
        return resourceAttachments;
    }
}
