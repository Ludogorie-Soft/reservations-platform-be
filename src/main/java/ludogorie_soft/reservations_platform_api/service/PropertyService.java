package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;

import java.io.FileNotFoundException;

public interface PropertyService {
    PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto);

    PropertyResponseDto updateAirBnbUrlOfProperty(Long id, String url) throws FileNotFoundException;

    PropertyResponseDto updateBookingUrlOfProperty(Long id, String url) throws FileNotFoundException;

    Property findById(Long id);

    String getPropertySyncUrl(Long id);
}
