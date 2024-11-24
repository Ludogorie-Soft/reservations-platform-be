package ludogorie_soft.reservations_platform_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.helper.PropertyTestHelper;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles({"test"})
@TestPropertySource(properties = {
        "spring.liquibase.enabled=false"
})
class PropertyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Property savedProperty;
    private User savedUser;
    private PropertyRequestDto propertyRequestDto;

    @BeforeEach
    void setUp() {
        savedUser = UserTestHelper.createUserForIntegrationTest();
        userRepository.save(savedUser);

        savedProperty = PropertyTestHelper.createPropertyForIntegrationTest();
        savedProperty.setOwner(savedUser);
        propertyRepository.save(savedProperty);

        propertyRequestDto = PropertyTestHelper.createDefaultPropertyRequestDto();
    }

    @Test
    void createProperty_ShouldReturnCreatedProperty() throws Exception {
        mockMvc.perform(post("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.websiteUrl").value("www.test.com"))
                .andExpect(jsonPath("$.capacity").value(4))
                .andExpect(jsonPath("$.petAllowed").value(true))
                .andExpect(jsonPath("$.petRules").value("Pets must be supervised at all times"))
                .andExpect(jsonPath("$.price").value(20))
                .andExpect(jsonPath("$.minimumStay").value(1))
                .andExpect(jsonPath("$.petPrice").value(10))
                .andExpect(jsonPath("$.propertyRules").value("No smoking allowed"));
    }

    @Test
    void getPropertyById_ShouldReturnProperty_WhenFound() throws Exception {
        mockMvc.perform(get("/api/properties/" + savedProperty.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.websiteUrl").value("www.test.com"))
                .andExpect(jsonPath("$.capacity").value(4))
                .andExpect(jsonPath("$.petAllowed").value(true))
                .andExpect(jsonPath("$.petRules").value("Pets must be supervised at all times"))
                .andExpect(jsonPath("$.price").value(20))
                .andExpect(jsonPath("$.minimumStay").value(1))
                .andExpect(jsonPath("$.petPrice").value(10))
                .andExpect(jsonPath("$.propertyRules").value("No smoking allowed"));
    }

    @Test
    void getPropertyById_ShouldReturnNotFound_WhenPropertyDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/properties/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Property not found with id: " + nonExistentId));
    }

    @Test
    void getAllProperties_ShouldReturnListOfProperties() throws Exception {
        mockMvc.perform(get("/api/properties")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].websiteUrl").value("www.test.com"))
                .andExpect(jsonPath("$[0].capacity").value(4))
                .andExpect(jsonPath("$[0].petAllowed").value(true))
                .andExpect(jsonPath("$[0].petRules").value("Pets must be supervised at all times"))
                .andExpect(jsonPath("$[0].price").value(20))
                .andExpect(jsonPath("$[0].minimumStay").value(1))
                .andExpect(jsonPath("$[0].petPrice").value(10))
                .andExpect(jsonPath("$[0].propertyRules").value("No smoking allowed"));
    }

    @Test
    void deleteProperty_ShouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/properties/" + savedProperty.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Property deleted successfully"));

        assertThat(propertyRepository.findById(savedProperty.getId())).isEmpty();
    }

    @Test
    void deleteProperty_ShouldReturnNotFound_WhenPropertyDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/properties/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Property not found with id: " + nonExistentId));

        assertThat(propertyRepository.findById(savedProperty.getId())).isNotEmpty();
    }

    @Test
    void updateProperty_ShouldReturnUpdatedProperty() throws Exception {
        PropertyRequestDto updatedRequestDto = PropertyTestHelper.createUpdatedPropertyRequestDto();

        mockMvc.perform(put("/api/properties/" + savedProperty.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.websiteUrl").value("http://example_updated.com"))
                .andExpect(jsonPath("$.capacity").value(6))
                .andExpect(jsonPath("$.petAllowed").value(false))
                .andExpect(jsonPath("$.petRules").value("No pets"))
                .andExpect(jsonPath("$.price").value(40))
                .andExpect(jsonPath("$.minimumStay").value(2))
                .andExpect(jsonPath("$.petPrice").value(20))
                .andExpect(jsonPath("$.propertyRules").value("Guests must be quiet after 10 PM"));
        
    }

    @Test
    void updateProperty_ShouldReturnNotFound_WhenPropertyDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        PropertyRequestDto updatedRequestDto = PropertyTestHelper.createUpdatedPropertyRequestDto();

        mockMvc.perform(put("/api/properties/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Property not found with id: " + nonExistentId));
    }
}
