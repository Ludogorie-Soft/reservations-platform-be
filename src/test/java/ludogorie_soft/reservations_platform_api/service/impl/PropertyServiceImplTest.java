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
import static org.mockito.Mockito.never;
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
        property.setPropertyRules("No smoking allowed");

        propertyRequestDto = new PropertyRequestDto();
        propertyRequestDto.setOwnerEmail("ownerEmailTest@test.com");
        propertyRequestDto.setPropertyRules("No smoking allowed");

        propertyResponseDto = new PropertyResponseDto();
        propertyResponseDto.setPropertyRules("No smoking allowed");
    }

    @Test
    void testCreateProperty() {
        // GIVEN
        when(userService.getUserByEmailOrUsername(anyString(), anyString())).thenReturn(user);
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        when(modelMapper.map(any(Property.class), eq(PropertyResponseDto.class))).thenReturn(propertyResponseDto);

        // WHEN
        PropertyResponseDto result = propertyService.createProperty(propertyRequestDto);

        // THEN
        assertNotNull(result);
        assertEquals("No smoking allowed", result.getPropertyRules());
        verify(userService).getUserByEmailOrUsername(anyString(), anyString());
        verify(propertyRepository).save(any(Property.class));
        verify(modelMapper).map(any(Property.class), eq(PropertyResponseDto.class));
    }

    @Test
    void testFindById() {
        // GIVEN
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // WHEN
        Property result = propertyService.findById(propertyId);

        // THEN
        assertNotNull(result);
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    void testFindByIdNotFound() {
        // GIVEN
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> propertyService.findById(propertyId));
    }

    @Test
    void testGetPropertyById() {
        // GIVEN
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(modelMapper.map(any(Property.class), eq(PropertyResponseDto.class))).thenReturn(propertyResponseDto);

        // WHEN
        Optional<PropertyResponseDto> result = propertyService.getPropertyById(propertyId);

        // THEN
        assertTrue(result.isPresent());
        assertEquals(propertyResponseDto, result.get());
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(modelMapper, times(1)).map(property, PropertyResponseDto.class);
    }

    @Test
    void testGetPropertyByIdNotFound() {
        // GIVEN
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // WHEN & THEN
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> propertyService.getPropertyById(propertyId));
        assertEquals("Property not found with id: " + propertyId, exception.getMessage());
        verify(propertyRepository, times(1)).findById(propertyId);
    }

    @Test
    void testGetAllProperties() {
        // GIVEN
        List<Property> properties = List.of(property);
        when(propertyRepository.findAll()).thenReturn(properties);
        when(modelMapper.map(any(Property.class), eq(PropertyResponseDto.class))).thenReturn(propertyResponseDto);

        // WHEN
        List<PropertyResponseDto> result = propertyService.getAllProperties();

        // THEN
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(propertyRepository).findAll();
    }

    @Test
    void deleteProperty_ShouldCallRepository_WhenPropertyExists() {
        // GIVEN
        when(propertyRepository.existsById(propertyId)).thenReturn(true);

        // WHEN
        propertyService.deleteProperty(propertyId);

        // THEN
        verify(propertyRepository, times(1)).existsById(propertyId);
        verify(propertyRepository, times(1)).deleteById(propertyId);
    }

    @Test
    void deleteProperty_ShouldThrowResourceNotFoundException_WhenPropertyDoesNotExist() {
        // GIVEN
        when(propertyRepository.existsById(propertyId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () -> propertyService.deleteProperty(propertyId));
        verify(propertyRepository, times(1)).existsById(propertyId);
        verify(propertyRepository, never()).deleteById(propertyId);
    }

    @Test
    void testSyncPropertiesWithAirBnbUrls() throws ParserException, IOException, ParseException, URISyntaxException {
        // GIVEN
        property.setAirBnbICalUrl("http://airbnb.com/calendar.ics");
        List<Property> properties = List.of(property);
        when(propertyRepository.findAll()).thenReturn(properties);
        doNothing().when(calendarService).syncAirBnbCalendar(propertyId);

        // WHEN
        propertyService.syncPropertiesWithAirBnbUrls();

        // THEN
        verify(propertyRepository, times(1)).findAll();
        verify(calendarService, times(1)).syncAirBnbCalendar(propertyId);
    }

    @Test
    void testUpdateProperty() {
        // GIVEN
        final var updatedRequestDto = getExistingPropertyRequestDto();
        final var existingProperty = getExistingProperty();
        final var updatedProperty = getUpdatedProperty(updatedRequestDto);
        final var updatedResponseDto = getUpdatedPropertyResponseDto(updatedRequestDto);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(existingProperty));
        when(propertyRepository.save(any(Property.class))).thenReturn(updatedProperty);
        when(modelMapper.map(any(Property.class), eq(PropertyResponseDto.class))).thenReturn(updatedResponseDto);

        // WHEN
        PropertyResponseDto result = propertyService.updateProperty(propertyId, updatedRequestDto);

        // THEN
        assertNotNull(result);
        assertEquals(updatedRequestDto.getWebsiteUrl(), result.getWebsiteUrl());
        assertEquals(updatedRequestDto.getCapacity(), result.getCapacity());
        assertEquals(updatedRequestDto.isPetAllowed(), result.isPetAllowed());
        assertEquals(updatedRequestDto.getPetRules(), result.getPetRules());
        assertEquals(updatedRequestDto.getPropertyRules(), result.getPropertyRules());
        assertEquals(updatedRequestDto.getPrice(), result.getPrice());

        verify(propertyRepository, times(1)).findById(propertyId);
        verify(propertyRepository, times(1)).save(existingProperty);
        verify(modelMapper, times(1)).map(updatedProperty, PropertyResponseDto.class);
    }

    private static PropertyRequestDto getExistingPropertyRequestDto() {
        PropertyRequestDto updatedRequestDto = new PropertyRequestDto();
        updatedRequestDto.setWebsiteUrl("http://newexample.com");
        updatedRequestDto.setCapacity(6);
        updatedRequestDto.setPetAllowed(false);
        updatedRequestDto.setPetRules("No pets allowed");
        updatedRequestDto.setPropertyRules("Guests must be quiet after 10 PM");
        updatedRequestDto.setPrice(200);
        return updatedRequestDto;
    }

    private Property getExistingProperty() {
        Property existingProperty = new Property();
        existingProperty.setId(propertyId);
        existingProperty.setWebsiteUrl("http://example.com");
        existingProperty.setCapacity(4);
        existingProperty.setPetAllowed(true);
        existingProperty.setPetRules("No large dogs");
        existingProperty.setPropertyRules("No smoking allowed");
        existingProperty.setPrice(150);
        return existingProperty;
    }

    private PropertyResponseDto getUpdatedPropertyResponseDto(PropertyRequestDto updatedRequestDto) {
        PropertyResponseDto updatedResponseDto = new PropertyResponseDto();
        updatedResponseDto.setId(propertyId);
        updatedResponseDto.setWebsiteUrl(updatedRequestDto.getWebsiteUrl());
        updatedResponseDto.setCapacity(updatedRequestDto.getCapacity());
        updatedResponseDto.setPetAllowed(updatedRequestDto.isPetAllowed());
        updatedResponseDto.setPetRules(updatedRequestDto.getPetRules());
        updatedResponseDto.setPropertyRules(updatedRequestDto.getPropertyRules());
        updatedResponseDto.setPrice(updatedRequestDto.getPrice());
        return updatedResponseDto;
    }

    private Property getUpdatedProperty(PropertyRequestDto updatedRequestDto) {
        Property updatedProperty = new Property();
        updatedProperty.setId(propertyId);
        updatedProperty.setWebsiteUrl(updatedRequestDto.getWebsiteUrl());
        updatedProperty.setCapacity(updatedRequestDto.getCapacity());
        updatedProperty.setPetAllowed(updatedRequestDto.isPetAllowed());
        updatedProperty.setPetRules(updatedRequestDto.getPetRules());
        updatedProperty.setPropertyRules(updatedRequestDto.getPropertyRules());
        updatedProperty.setPrice(updatedRequestDto.getPrice());
        return updatedProperty;
    }
}
