package com.ikonicit.resource.tracker.utils;

import com.ikonicit.resource.tracker.dto.ResourceAttachmentDTO;
import com.ikonicit.resource.tracker.dto.ResourceDTO;
import com.ikonicit.resource.tracker.dto.SendEmailDTO;
import com.ikonicit.resource.tracker.entity.Resource;
import com.ikonicit.resource.tracker.entity.ResourceAttachments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResourceUtils {

    public static List<ResourceDTO> mockResources() {
        List<ResourceDTO> resources = new ArrayList<>();
        ResourceDTO resourceOne = new ResourceDTO(1, 51, null, "Neymar", "Neymar", "JR", "jr@gmail.com", "xyz", "9874563210", "java", "fullTime", new Date(), new Date(), "bench",
                "spring", BigDecimal.valueOf(2.3), null, null, null, null, new Date(), "ram", new Date(), "ram",true,"a",null);
        ResourceDTO resourceTwo = new ResourceDTO(2, 51, null, "crRonaldo", "cr", "ronaldo", "cr@gmail.com", "xyz", "9874563210", "java", "fullTime", new Date(), new Date(), "bench",
                "spring", BigDecimal.valueOf(2.3), null, null, null, null, new Date(), "ram", new Date(), "ram",false,null,null);
        ResourceDTO resourceThree = new ResourceDTO(3, 51, null, "Lmessi", "l", "Messi", "messi@gmail.com", "xyz", "9874563210", "java", "fullTime", new Date(), new Date(), "bench",
                "spring", BigDecimal.valueOf(2.3), null, null, null, null, new Date(), "ram", new Date(), "ram",false,null,null);
        resources.add(resourceOne);
        resources.add(resourceTwo);
        resources.add(resourceThree);
        return resources;
    }

    public static ResourceDTO mockResourceForCreateandUpdate() {
        ResourceDTO resource = new ResourceDTO();
        resource.setFirstName("Cr");
        resource.setLastName("Ronaldo");
        resource.setStatus("Bench");
        resource.setStartDate(new Date());
        resource.setResourceName("CrRonaldo");
        resource.setPhone("9687463210");
        resource.setLinkedin("xyz");
        resource.setEndDate(new Date());
        resource.setTechnology("spring");
        resource.setEmail("cr@gmail.com");
        resource.setComments("abc");
        resource.setResourceAttachments(buildAttchments());
        return resource;
    }

    private static List<ResourceAttachmentDTO> buildAttchments() {
        List<ResourceAttachmentDTO> attchments = new ArrayList<>();
        ResourceAttachmentDTO resourceAttachmentDTO = new ResourceAttachmentDTO();
        resourceAttachmentDTO.setFileName("abc.pdf");
        attchments.add(resourceAttachmentDTO);
        return attchments;
    }

    public static SendEmailDTO sendEmail() {
        SendEmailDTO sendEmailDTO = new SendEmailDTO();
        sendEmailDTO.setEmail("cr@gmail.com");
        sendEmailDTO.setEmailBody("xyz");
        sendEmailDTO.setIsTrue(true);
        sendEmailDTO.setSubject("abc");
        return sendEmailDTO;
    }

    public static ResourceAttachments getAttachment() {
        ResourceAttachments resourceAttachment = new ResourceAttachments();
        resourceAttachment.setAttachmentId(1);
        resourceAttachment.setAttachment(new byte[123255]);
        resourceAttachment.setFileName("abc.pdf");
        return resourceAttachment;
    }

    public static List<ResourceDTO> mockmanagers() {
        List<ResourceDTO> resources = new ArrayList<>();
        ResourceDTO resourceOne = new ResourceDTO(1, null, 2, "Neymar", "Neymar", "JR", "jr@gmail.com", "xyz", "9874563210", "java", "fullTime", new Date(), new Date(), "bench",
                "spring", BigDecimal.valueOf(2.3), null, null, null, null, new Date(), "ram", new Date(), "ram",true,"a",null);
        ResourceDTO resourceTwo = new ResourceDTO(2, null, 2, "crRonaldo", "cr", "ronaldo", "cr@gmail.com", "xyz", "9874563210", "java", "fullTime", new Date(), new Date(), "bench",
                "spring", BigDecimal.valueOf(2.3), null, null, null, null, new Date(), "ram", new Date(), "ram",true,"c",null);
        ResourceDTO resourceThree = new ResourceDTO(3, null, 2, "Lmessi", "l", "Messi", "messi@gmail.com", "xyz", "9874563210", "java", "fullTime", new Date(), new Date(), "bench",
                "spring", BigDecimal.valueOf(2.3), null, null, null, null, new Date(), "ram", new Date(), "ram",true,"b",null);
        resources.add(resourceOne);
        resources.add(resourceTwo);
        resources.add(resourceThree);
        return resources;
    }
}