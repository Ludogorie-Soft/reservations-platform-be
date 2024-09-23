package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;

import java.util.List;

public interface PropertyService {
    PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto);

    PropertyResponseDto updateAirBnbUrlOfProperty(Long id, String url);
    PropertyResponseDto updateBookingUrlOfProperty(Long id, String url);

    Property findById(Long id);
    List<Property> findAll();
}
