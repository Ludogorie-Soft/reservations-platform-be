package ludogorie_soft.reservations_platform_api.controller;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyControllerTest {

    @Mock
    private PropertyService propertyService;

    @InjectMocks
    private PropertyController propertyController;

    @Test
    void createProperty_ShouldReturnCreatedProperty() {
        // GIVEN
        PropertyRequestDto requestDto = new PropertyRequestDto();
        PropertyResponseDto responseDto = new PropertyResponseDto();
        when(propertyService.createProperty(requestDto)).thenReturn(responseDto);

        // WHEN
        ResponseEntity<PropertyResponseDto> response = propertyController.createProperty(requestDto);

        // THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(propertyService, times(1)).createProperty(requestDto);
    }

    @Test
    void getAllProperties_ShouldReturnListOfProperties() {
        // GIVEN
        List<PropertyResponseDto> propertyResponseDtos = List.of(new PropertyResponseDto(), new PropertyResponseDto());
        when(propertyService.getAllProperties()).thenReturn(propertyResponseDtos);

        // WHEN
        ResponseEntity<List<PropertyResponseDto>> response = propertyController.getAllProperties();

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(propertyResponseDtos, response.getBody());
        verify(propertyService, times(1)).getAllProperties();
    }

    @Test
    void getPropertyById_ShouldReturnProperty_WhenFound() {
        // GIVEN
        UUID propertyId = UUID.randomUUID();
        PropertyResponseDto responseDto = new PropertyResponseDto();
        when(propertyService.getPropertyById(propertyId)).thenReturn(Optional.of(responseDto));

        // WHEN
        ResponseEntity<PropertyResponseDto> response = propertyController.getPropertyById(propertyId);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(propertyService, times(1)).getPropertyById(propertyId);
    }

    @Test
    void getPropertyById_ShouldReturnNotFound_WhenNotFound() {
        // GIVEN
        UUID propertyId = UUID.randomUUID();
        when(propertyService.getPropertyById(propertyId)).thenReturn(Optional.empty());

        // WHEN
        ResponseEntity<PropertyResponseDto> response = propertyController.getPropertyById(propertyId);

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(propertyService, times(1)).getPropertyById(propertyId);
    }

    @Test
    void deleteProperty_ShouldReturnSuccessMessage() {
        // GIVEN
        UUID propertyId = UUID.randomUUID();
        doNothing().when(propertyService).deleteProperty(propertyId);

        // WHEN
        ResponseEntity<String> response = propertyController.deleteProperty(propertyId);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Property deleted successfully", response.getBody());
        verify(propertyService, times(1)).deleteProperty(propertyId);
    }

    @Test
    void updateProperty_ShouldReturnUpdatedProperty() {
        // GIVEN
        UUID propertyId = UUID.randomUUID();
        PropertyRequestDto requestDto = new PropertyRequestDto();
        PropertyResponseDto responseDto = new PropertyResponseDto();
        when(propertyService.updateProperty(propertyId, requestDto)).thenReturn(responseDto);

        // WHEN
        ResponseEntity<PropertyResponseDto> response = propertyController.updateProperty(propertyId, requestDto);

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(propertyService, times(1)).updateProperty(propertyId, requestDto);
    }

    @Test
    void updateAirBnbUrl_ShouldReturnUpdatedProperty_WhenSuccessful() throws FileNotFoundException {
        // GIVEN
        UUID propertyId = UUID.randomUUID();
        String airBnbUrl = "https://www.airbnb.com/sample";
        PropertyResponseDto responseDto = new PropertyResponseDto();
        when(propertyService.updateAirBnbUrlOfProperty(propertyId, airBnbUrl)).thenReturn(responseDto);

        // WHEN
        ResponseEntity<PropertyResponseDto> response = propertyController.updateAirBnbUrl(propertyId, airBnbUrl);

        // THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(propertyService, times(1)).updateAirBnbUrlOfProperty(propertyId, airBnbUrl);
    }

    @Test
    void updateAirBnbUrl_ShouldThrowFileNotFoundException_WhenFileNotFound() throws FileNotFoundException {
        // GIVEN
        UUID propertyId = UUID.randomUUID();
        String airBnbUrl = "https://www.airbnb.com/sample";
        when(propertyService.updateAirBnbUrlOfProperty(propertyId, airBnbUrl)).thenThrow(new FileNotFoundException());

        // WHEN
        // THEN
        assertThrows(FileNotFoundException.class, () -> propertyController.updateAirBnbUrl(propertyId, airBnbUrl));
        verify(propertyService, times(1)).updateAirBnbUrlOfProperty(propertyId, airBnbUrl);
    }

    @Test
    void updateBookingUrl_ShouldReturnUpdatedProperty_WhenSuccessful() throws FileNotFoundException {
        // GIVEN
        UUID propertyId = UUID.randomUUID();
        String bookingUrl = "https://www.booking.com/sample";
        PropertyResponseDto responseDto = new PropertyResponseDto();
        when(propertyService.updateBookingUrlOfProperty(propertyId, bookingUrl)).thenReturn(responseDto);

        // WHEN
        ResponseEntity<PropertyResponseDto> response = propertyController.updateBookingUrl(propertyId, bookingUrl);

        // THEN
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDto, response.getBody());
        verify(propertyService, times(1)).updateBookingUrlOfProperty(propertyId, bookingUrl);
    }

    @Test
    void updateBookingUrl_ShouldThrowFileNotFoundException_WhenFileNotFound() throws FileNotFoundException {
        // GIVEN
        UUID propertyId = UUID.randomUUID();
        String bookingUrl = "https://www.booking.com/sample";
        when(propertyService.updateBookingUrlOfProperty(propertyId, bookingUrl)).thenThrow(new FileNotFoundException());

        // WHEN
        // THEN
        assertThrows(FileNotFoundException.class, () -> propertyController.updateBookingUrl(propertyId, bookingUrl));
        verify(propertyService, times(1)).updateBookingUrlOfProperty(propertyId, bookingUrl);
    }
}
