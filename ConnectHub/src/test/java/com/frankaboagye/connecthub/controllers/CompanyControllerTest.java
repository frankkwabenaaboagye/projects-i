package com.frankaboagye.connecthub.controllers;

import com.frankaboagye.connecthub.entities.Company;
import com.frankaboagye.connecthub.services.CompanyService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    @Test
    void testHandleCompanyRegisteration() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("companyName", "Test Company");
        params.add("companyEmail", "test@example.com");
        params.add("companyPhoneNumber", "1234567890");
        params.add("companyPassword", "testpassword");
        params.add("companyWebsite", "https://test.com");

        mockMvc.perform(post("/register-company").params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("loginCompany"));

        // Capture the Company object passed to the service
        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);

        // Verify that the registerCompany method is called once
        verify(companyService).registerCompany(companyCaptor.capture()); // or verify(companyService, times(1)).registerCompany(companyCaptor.capture());

        Company company = companyCaptor.getValue();
        assertEquals("Test Company", company.getName());
        assertEquals("test@example.com", company.getEmail());
        assertEquals("1234567890", company.getPhonenumber());
        assertEquals("testpassword", company.getPassword());
        assertEquals("https://test.com", company.getWebsite());

    }
}