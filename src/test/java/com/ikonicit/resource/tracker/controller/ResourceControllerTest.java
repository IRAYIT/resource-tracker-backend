package com.ikonicit.resource.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikonicit.resource.tracker.service.ResourceService;
import com.ikonicit.resource.tracker.utils.ResourceUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ResourceControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private ResourceService resourceService;

    @Before
    public void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    /**
     * Test to create Item service call
     */
    @Test
    public void getResource() throws Exception {
        when(resourceService.getResource(1)).thenReturn(ResourceUtils.mockResources().get(0));
        String uri = "/api/v1/resource/1";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
              .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void getAllResource() throws Exception {
        when(resourceService.getAll()).thenReturn(ResourceUtils.mockResources());
        String uri = "/api/v1/resource";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    /*
    @Test
    @Ignore
    public void createResource() throws Exception {
        List<MultipartFile> attachments = null;
        String payload = "";
        when(resourceService.create(attachments,payload)).thenReturn(ResourceUtils.mockResourceForCreateandUpdate());
        String uri = "/api/v1/resource";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }*/

    @Test
    public void updateResource() throws Exception {
        List<MultipartFile> attachments = null;
        String payload = "";
        when(resourceService.update(attachments,payload)).thenReturn(ResourceUtils.mockResourceForCreateandUpdate());
        String uri = "/api/v1/resource";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void getResourceByName() throws Exception {
        when(resourceService.findByResourceName("crRonaldo")).thenReturn(ResourceUtils.mockResources().get(1));
        String uri = "/api/v1/resource/byName/crRonaldo";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void resourceCheckByName() throws Exception {
        when(resourceService.checkResourceName("crRonaldo")).thenReturn("ResourceName Available");
        String uri = "/api/v1/resource/nameCheck/crRonaldo";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void resourceCheckByEmail() throws Exception {
        when(resourceService.checkEmail("cr@gmail.com")).thenReturn("Email Available");
        String uri = "/api/v1/resource/emailCheck/cr@gmail.com";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void sendUpdateEmails() throws Exception {
        when(resourceService.sendEmail(ResourceUtils.sendEmail())).thenReturn("Mail Sent");
        String uri = "/api/v1/resource/sendEmail";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void deleteResource() throws Exception {
        when(resourceService.deleteResource(1)).thenReturn(true);
        String uri = "/api/v1/resource/1";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void getAllResourcesByManagerId() throws Exception {
        when(resourceService.resourcesByManager(51)).thenReturn(ResourceUtils.mockResources());
        String uri = "/api/v1/resource/getAllResourcesByManagerId/51";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void getAttachment() throws Exception {
        when(resourceService.getAttachment(1).getFileName()).thenReturn(ResourceUtils.getAttachment().getFileName());
        when(resourceService.getAttachment(1).getAttachment()).thenReturn(ResourceUtils.getAttachment().getAttachment());
        String uri = "/api/v1/resource/attachment/1";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void getAllManagers() throws Exception {
        when(resourceService.getAllManagers()).thenReturn(ResourceUtils.mockmanagers());
        String uri = "/api/v1/resource/managers";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatus());
    }
}
