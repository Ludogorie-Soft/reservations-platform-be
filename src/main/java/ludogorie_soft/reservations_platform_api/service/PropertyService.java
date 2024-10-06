package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

public interface PropertyService {
    PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto);

    PropertyResponseDto updateAirBnbUrlOfProperty(Long id, String url) throws FileNotFoundException;

    PropertyResponseDto updateBookingUrlOfProperty(Long id, String url) throws FileNotFoundException;

    Property findById(Long id);

    List<PropertyResponseDto> getAllProperties();

    void deleteProperty(Long id);

    Optional<PropertyResponseDto> getPropertyById(Long id);

    Property updateProperty(Long id, PropertyRequestDto propertyDetails);
}
