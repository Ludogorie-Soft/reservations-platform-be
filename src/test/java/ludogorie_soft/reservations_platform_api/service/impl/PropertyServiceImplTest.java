package ludogorie_soft.reservations_platform_api.service.impl;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.exception.ResourceNotFoundException;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.service.CalendarService;
import ludogorie_soft.reservations_platform_api.service.UserService;
import net.fortuna.ical4j.data.ParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PropertyServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CalendarService calendarService;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    private Property property;
    private User user;
    private UUID propertyId;
    private PropertyRequestDto propertyRequestDto;
    private PropertyResponseDto propertyResponseDto;

    @BeforeEach
    void setUp() {
        propertyId = UUID.randomUUID();
        user = new User();
        user.setEmail("userEmailTest@test.com");

        property = new Property();
        property.setId(propertyId);
        property.setOwner(user);

        propertyRequestDto = new PropertyRequestDto();
        propertyRequestDto.setOwnerEmail("ownerEmailTest@test.com");

        propertyResponseDto = new PropertyResponseDto();
    }

    @Test
    void testCreateProperty() {
        when(userService.getUserByEmailOrUsername(anyString(), anyString())).thenReturn(user);
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        when(modelMapper.map(any(Property.class), eq(PropertyResponseDto.class))).thenReturn(propertyResponseDto);

        PropertyResponseDto result = propertyService.createProperty(propertyRequestDto);

        assertNotNull(result);
        verify(userService).getUserByEmailOrUsername(anyString(), anyString());
        verify(propertyRepository).save(any(Property.class));
        verify(modelMapper).map(any(Property.class), eq(PropertyResponseDto.class));
    }


    @Test
    void testFindById() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        Property result = propertyService.findById(propertyId);

        assertNotNull(result);
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    void testFindByIdNotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> propertyService.findById(propertyId));
    }

    @Test
    void testGetPropertyById() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(modelMapper.map(any(Property.class), eq(PropertyResponseDto.class))).thenReturn(propertyResponseDto);

        Optional<PropertyResponseDto> result = propertyService.getPropertyById(propertyId);

        assertTrue(result.isPresent(), "The property response should be present");
        assertEquals(propertyResponseDto, result.get(), "The property response should match the expected DTO");
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(modelMapper, times(1)).map(property, PropertyResponseDto.class);
    }

    @Test
    void testGetPropertyByIdNotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> propertyService.getPropertyById(propertyId), "Expected getPropertyById to throw ResourceNotFoundException, but it didn't");

        assertEquals("Property not found with id: " + propertyId, exception.getMessage(), "Exception message should match");
        verify(propertyRepository, times(1)).findById(propertyId);
    }

    @Test
    void testGetAllProperties() {
        List<Property> properties = List.of(property);
        when(propertyRepository.findAll()).thenReturn(properties);
        when(modelMapper.map(any(Property.class), eq(PropertyResponseDto.class))).thenReturn(propertyResponseDto);

        List<PropertyResponseDto> result = propertyService.getAllProperties();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(propertyRepository).findAll();
    }

    @Test
    void testDeleteProperty() {
        doNothing().when(propertyRepository).deleteById(propertyId);

        propertyService.deleteProperty(propertyId);

        verify(propertyRepository).deleteById(propertyId);
    }

    @Test
    void testDeletePropertyNotFound() {
        doThrow(new RuntimeException()).when(propertyRepository).deleteById(propertyId);

        assertThrows(ResourceNotFoundException.class, () -> propertyService.deleteProperty(propertyId));
    }

    @Test
    void testUpdateProperty() {
        PropertyRequestDto updatedRequestDto = new PropertyRequestDto();
        updatedRequestDto.setWebsiteUrl("http://newexample.com");
        updatedRequestDto.setCapacity(6);
        updatedRequestDto.setPetAllowed(false);
        updatedRequestDto.setPetRules("No pets allowed");
        updatedRequestDto.setPrice(200);

        Property existingProperty = new Property();
        existingProperty.setId(propertyId);
        existingProperty.setWebsiteUrl("http://example.com");
        existingProperty.setCapacity(4);
        existingProperty.setPetAllowed(true);
        existingProperty.setPetRules("No large dogs");
        existingProperty.setPrice(150);

        Property updatedProperty = new Property();
        updatedProperty.setId(propertyId);
        updatedProperty.setWebsiteUrl(updatedRequestDto.getWebsiteUrl());
        updatedProperty.setCapacity(updatedRequestDto.getCapacity());
        updatedProperty.setPetAllowed(updatedRequestDto.isPetAllowed());
        updatedProperty.setPetRules(updatedRequestDto.getPetRules());
        updatedProperty.setPrice(updatedRequestDto.getPrice());

        PropertyResponseDto updatedResponseDto = new PropertyResponseDto();
        updatedResponseDto.setId(propertyId);
        updatedResponseDto.setWebsiteUrl(updatedRequestDto.getWebsiteUrl());
        updatedResponseDto.setCapacity(updatedRequestDto.getCapacity());
        updatedResponseDto.setPetAllowed(updatedRequestDto.isPetAllowed());
        updatedResponseDto.setPetRules(updatedRequestDto.getPetRules());
        updatedResponseDto.setPrice(updatedRequestDto.getPrice());

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(existingProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(updatedProperty);
        when(modelMapper.map(any(Property.class), eq(PropertyResponseDto.class))).thenReturn(updatedResponseDto);

        PropertyResponseDto result = propertyService.updateProperty(propertyId, updatedRequestDto);

        assertNotNull(result, "The updated property response should not be null");
        assertEquals(updatedRequestDto.getWebsiteUrl(), result.getWebsiteUrl(), "Website URL should be updated");
        assertEquals(updatedRequestDto.getCapacity(), result.getCapacity(), "Capacity should be updated");
        assertEquals(updatedRequestDto.isPetAllowed(), result.isPetAllowed(), "Pet allowed flag should be updated");
        assertEquals(updatedRequestDto.getPetRules(), result.getPetRules(), "Pet rules should be updated");
        assertEquals(updatedRequestDto.getPrice(), result.getPrice(), "Price should be updated");

        verify(propertyRepository, times(1)).findById(propertyId);
        verify(propertyRepository, times(1)).save(existingProperty);
        verify(modelMapper, times(1)).map(updatedProperty, PropertyResponseDto.class);
    }

    @Test
    void testSyncPropertiesWithAirBnbUrls() throws ParserException, IOException, ParseException, URISyntaxException {
        property.setAirBnbICalUrl("http://airbnb.com/calendar.ics");
        List<Property> properties = List.of(property);
        when(propertyRepository.findAll()).thenReturn(properties);
        doNothing().when(calendarService).syncAirBnbCalendar(propertyId);

        propertyService.syncPropertiesWithAirBnbUrls();

        verify(propertyRepository, times(1)).findAll();
        verify(calendarService, times(1)).syncAirBnbCalendar(propertyId);
    }


}
