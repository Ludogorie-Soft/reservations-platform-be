package ludogorie_soft.reservations_platform_api.service;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyService {
    PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto);

    PropertyResponseDto updateAirBnbUrlOfProperty(UUID id, String url) throws FileNotFoundException;

    PropertyResponseDto updateBookingUrlOfProperty(UUID id, String url) throws FileNotFoundException;

    Property findById(UUID id);

    List<PropertyResponseDto> getAllProperties();

    void deleteProperty(UUID id);

    Optional<PropertyResponseDto> getPropertyById(UUID id);

    PropertyResponseDto updateProperty(UUID id, PropertyRequestDto propertyDetails);
}
