package ludogorie_soft.reservations_platform_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Ivan");
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("testuser@example.com");
        savedUser = userRepository.save(user);

        Property property = new Property();
        property.setOwner(savedUser);
        property.setWebsiteUrl("http://testproperty.com");
        property.setCapacity(4);
        property.setPetAllowed(true);
        property.setPetRules("Cats only");
        property.setPrice(200);
        savedProperty = propertyRepository.save(property);
    }

    @Test
    void createProperty_ShouldReturnCreatedProperty() throws Exception {
        PropertyRequestDto requestDto = new PropertyRequestDto();
        requestDto.setOwnerEmail(savedUser.getEmail());
        requestDto.setWebsiteUrl("http://newproperty.com");
        requestDto.setCapacity(2);
        requestDto.setPetAllowed(false);
        requestDto.setPrice(150);
        requestDto.setPetRules("No pets allowed");

        mockMvc.perform(post("/api/properties/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.websiteUrl").value("http://newproperty.com"))
                .andExpect(jsonPath("$.capacity").value(2))
                .andExpect(jsonPath("$.petAllowed").value(false))
                .andExpect(jsonPath("$.petRules").value("No pets allowed"))
                .andExpect(jsonPath("$.price").value(150));
    }

    @Test
    void getPropertyById_ShouldReturnProperty_WhenFound() throws Exception {
        mockMvc.perform(get("/api/properties/" + savedProperty.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.websiteUrl").value("http://testproperty.com"))
                .andExpect(jsonPath("$.capacity").value(4));
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
        mockMvc.perform(get("/api/properties/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].websiteUrl").value("http://testproperty.com"));
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
        PropertyRequestDto requestDto = new PropertyRequestDto();
        requestDto.setOwnerEmail(savedUser.getEmail());
        requestDto.setWebsiteUrl("http://updatedproperty.com");
        requestDto.setCapacity(6);
        requestDto.setPetAllowed(true);
        requestDto.setPrice(300);
        requestDto.setPetRules("Pets allowed with prior approval");

        mockMvc.perform(put("/api/properties/" + savedProperty.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.websiteUrl").value("http://updatedproperty.com"))
                .andExpect(jsonPath("$.capacity").value(6));

        Property updatedProperty = propertyRepository.findById(savedProperty.getId()).get();
        assertThat(updatedProperty.getWebsiteUrl()).isEqualTo("http://updatedproperty.com");
        assertThat(updatedProperty.getCapacity()).isEqualTo(6);
    }

    @Test
    void updateProperty_ShouldReturnNotFound_WhenPropertyDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        PropertyRequestDto requestDto = new PropertyRequestDto();
        requestDto.setOwnerEmail("nonexistentuser@example.com");
        requestDto.setWebsiteUrl("http://updatedproperty.com");
        requestDto.setCapacity(6);
        requestDto.setPetAllowed(true);
        requestDto.setPrice(300);
        requestDto.setPetRules("Pets allowed with prior approval");

        mockMvc.perform(put("/api/properties/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Property not found with id: " + nonExistentId));
    }
}
