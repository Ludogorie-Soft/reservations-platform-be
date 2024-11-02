package ludogorie_soft.reservations_platform_api.service.impl;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.exception.ResourceNotFoundException;
import ludogorie_soft.reservations_platform_api.helper.PropertyTestHelper;
import ludogorie_soft.reservations_platform_api.helper.UserTestHelper;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileNotFoundException;
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
    private UUID propertyId;
    private PropertyRequestDto propertyRequestDto;
    private PropertyResponseDto propertyResponseDto;

    @BeforeEach
    void setUp() {
        propertyId = PropertyTestHelper.DEFAULT_PROPERTY_ID;
        property = PropertyTestHelper.createDefaultProperty();
        propertyRequestDto = PropertyTestHelper.createDefaultPropertyRequestDto();
        propertyResponseDto = PropertyTestHelper.createDefaultPropertyResponseDto();

        ReflectionTestUtils.setField(propertyService, "icsAirBnbDirectory", "air-bnb-calendar");
        ReflectionTestUtils.setField(propertyService, "icsBookingDirectory", "booking-calendar");
    }

    @Test
    void createProperty_whenValidRequest_returnsPropertyResponseDto() {
        // GIVEN
        when(userService.getUserByEmailOrUsername(anyString(), anyString())).thenReturn(UserTestHelper.createTestUser());
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        when(modelMapper.map(any(Property.class), eq(PropertyResponseDto.class))).thenReturn(propertyResponseDto);

        // WHEN
        PropertyResponseDto result = propertyService.createProperty(propertyRequestDto);

        // THEN
        assertNotNull(result);
        assertEquals(PropertyTestHelper.DEFAULT_PROPERTY_RULES, result.getPropertyRules());
        verify(userService).getUserByEmailOrUsername(anyString(), anyString());
        verify(propertyRepository).save(any(Property.class));
        verify(modelMapper).map(any(Property.class), eq(PropertyResponseDto.class));
    }

    @Test
    void findById_whenPropertyExists_returnsProperty() {
        // GIVEN
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));

        // WHEN
        Property result = propertyService.findById(propertyId);

        // THEN
        assertNotNull(result);
        verify(propertyRepository).findById(propertyId);
    }

    @Test
    void findById_whenPropertyDoesNotExist_throwsIllegalArgumentException() {
        // GIVEN
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> propertyService.findById(propertyId));
    }

    @Test
    void getPropertyById_whenPropertyExists_returnsPropertyResponseDto() {
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
    void getPropertyById_whenPropertyDoesNotExist_throwsResourceNotFoundException() {
        // GIVEN
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // WHEN & THEN
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> propertyService.getPropertyById(propertyId));
        assertEquals("Property not found with id: " + propertyId, exception.getMessage());
        verify(propertyRepository, times(1)).findById(propertyId);
    }

    @Test
    void getAllProperties_whenCalled_returnsListOfPropertyResponseDto() {
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
    void deleteProperty_whenPropertyExists_callsRepositoryMethods() {
        // GIVEN
        when(propertyRepository.existsById(propertyId)).thenReturn(true);

        // WHEN
        propertyService.deleteProperty(propertyId);

        // THEN
        verify(propertyRepository, times(1)).existsById(propertyId);
        verify(propertyRepository, times(1)).deleteById(propertyId);
    }

    @Test
    void deleteProperty_whenPropertyDoesNotExist_throwsResourceNotFoundException() {
        // GIVEN
        when(propertyRepository.existsById(propertyId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () -> propertyService.deleteProperty(propertyId));
        verify(propertyRepository, times(1)).existsById(propertyId);
        verify(propertyRepository, never()).deleteById(propertyId);
    }

    @Test
    void syncPropertiesWithAirBnbUrls_whenValidUrl_syncsCalendar() throws ParserException, IOException, ParseException, URISyntaxException {
        // GIVEN
        property.setAirBnbICalUrl("http://airbnb.com/calendar.ics");
        List<Property> properties = List.of(property);
        when(propertyRepository.findAll()).thenReturn(properties);
        doNothing().when(calendarService).syncAirBnbCalendar(propertyId);

        // WHEN
        propertyService.syncPropertiesWithAirBnbUrls();

        // THEN
        verify(propertyRepository).findAll();
        verify(calendarService).syncAirBnbCalendar(propertyId);
    }

    @Test
    void updateProperty_whenValidRequest_updatesAndReturnsPropertyResponseDto() {
        // GIVEN
        PropertyRequestDto updatedRequestDto = PropertyTestHelper.createUpdatedPropertyRequestDto();
        Property updatedProperty = PropertyTestHelper.createUpdatedProperty(updatedRequestDto);
        PropertyResponseDto updatedResponseDto = PropertyTestHelper.createUpdatedPropertyResponseDto(updatedProperty);

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(updatedProperty);
        when(modelMapper.map(any(Property.class), eq(PropertyResponseDto.class))).thenReturn(updatedResponseDto);

        // WHEN
        PropertyResponseDto result = propertyService.updateProperty(propertyId, updatedRequestDto);

        // THEN
        assertNotNull(result);
        assertEquals(updatedRequestDto.getPropertyRules(), result.getPropertyRules());
        verify(propertyRepository).findById(propertyId);
        verify(propertyRepository).save(property);
        verify(modelMapper).map(any(Property.class), eq(PropertyResponseDto.class));

    }

    @Test
    void updateProperty_whenPropertyDoesNotExist_throwsResourceNotFoundException() {
        // GIVEN
        PropertyRequestDto updatedPropertyRequestDto = PropertyTestHelper.createDefaultPropertyRequestDto();

        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () ->
                propertyService.updateProperty(propertyId, updatedPropertyRequestDto)
        );

        verify(propertyRepository).findById(propertyId);
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void testUpdateAirBnbUrlOfPropertySuccessfully() throws FileNotFoundException {
        //GIVEN
        String airBnbUrl = "https://airbnb.com/calendar.ics";
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        //WHEN
        propertyService.updateAirBnbUrlOfProperty(propertyId, airBnbUrl);

        //THEN
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(propertyRepository, times(1)).save(property);
    }

    @Test
    void testUpdateBookingUrlOfPropertySuccessfully() throws FileNotFoundException {
        //GIVEN
        String bookingUrl = "https://booking.com/calendar.ics";
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);

        //WHEN
        propertyService.updateBookingUrlOfProperty(propertyId, bookingUrl);

        //THEN
        verify(propertyRepository, times(1)).findById(propertyId);
        verify(propertyRepository, times(1)).save(property);
    }
}
