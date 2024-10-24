package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.exception.ResourceNotFoundException;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.service.CalendarService;
import ludogorie_soft.reservations_platform_api.service.PropertyService;
import ludogorie_soft.reservations_platform_api.service.UserService;
import net.fortuna.ical4j.data.ParserException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final UserService userService;
    private final PropertyRepository propertyRepository;
    private final ModelMapper modelMapper;
    private final CalendarService calendarService;

    @Value("${booking.ics.airBnb.directory}")
    private String icsAirBnbDirectory;

    @Value("${booking.ics.booking.directory}")
    private String icsBookingDirectory;

    @Override
    public PropertyResponseDto createProperty(PropertyRequestDto propertyRequestDto) {

        User user = userService.getUserByEmailOrUsername(propertyRequestDto.getOwnerEmail(), propertyRequestDto.getOwnerEmail());

        Property property = new Property();
        property.setOwner(user);
        property.setWebsiteUrl(propertyRequestDto.getWebsiteUrl());
        property.setCapacity(propertyRequestDto.getCapacity());
        property.setPetAllowed(propertyRequestDto.isPetAllowed());
        property.setPetRules(propertyRequestDto.getPetRules());
        property.setPrice(propertyRequestDto.getPrice());
        property.setMinimumStay(propertyRequestDto.getMinimumStay());
        property.setPetPrice(propertyRequestDto.getPetPrice());

        Property createdProperty = propertyRepository.save(property);
        return modelMapper.map(createdProperty, PropertyResponseDto.class);
    }

    @Override
    public PropertyResponseDto updateAirBnbUrlOfProperty(UUID id, String url) throws FileNotFoundException {
        Property property = findById(id);
        property.setAirBnbICalUrl(url);
        Property updatedProperty = propertyRepository.save(property);
        createIcsFile("airBnbCalendar-" + property.getId() + ".ics", icsAirBnbDirectory);
        return modelMapper.map(updatedProperty, PropertyResponseDto.class);
    }

    @Override
    public PropertyResponseDto updateBookingUrlOfProperty(UUID id, String url) throws FileNotFoundException {
        Property property = findById(id);
        property.setBookingICalUrl(url);
        Property updatedProperty = propertyRepository.save(property);
        createIcsFile("bookingCalendar-" + property.getId() + ".ics", icsBookingDirectory);
        return modelMapper.map(updatedProperty, PropertyResponseDto.class);
    }

    @Override
    public Property findById(UUID id) {
        return propertyRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Property not found!")
        );
    }

    @Override
    public List<PropertyResponseDto> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        return properties.stream()
                .map(property -> modelMapper.map(property, PropertyResponseDto.class))
                .toList();
    }

    @Override
    public Optional<PropertyResponseDto> getPropertyById(UUID id) {
        return Optional.ofNullable(propertyRepository.findById(id)
                .map(property -> modelMapper.map(property, PropertyResponseDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id)));
    }

    @Override
    public void deleteProperty(UUID id) {
        if (!propertyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Property not found with id: " + id);
        }
        propertyRepository.deleteById(id);
    }

    @Override
    public PropertyResponseDto updateProperty(UUID id, PropertyRequestDto oldProperty) {
        Property updatedProperty = propertyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));

        updatedProperty.setWebsiteUrl(oldProperty.getWebsiteUrl());
        updatedProperty.setCapacity(oldProperty.getCapacity());
        updatedProperty.setPetAllowed(oldProperty.isPetAllowed());
        updatedProperty.setPetRules(oldProperty.getPetRules());
        updatedProperty.setPrice(oldProperty.getPrice());
        updatedProperty.setMinimumStay(oldProperty.getMinimumStay());
        updatedProperty.setPetPrice(oldProperty.getPetPrice());

        propertyRepository.save(updatedProperty);
        return modelMapper.map(updatedProperty, PropertyResponseDto.class);
    }


    @Scheduled(fixedRate = 3600000)
    public void syncPropertiesWithAirBnbUrls() {
        List<Property> properties = propertyRepository.findAll();

        properties.stream()
                .filter(property -> property.getAirBnbICalUrl() != null && !property.getAirBnbICalUrl().trim().isEmpty())
                .forEach(property -> {
                    try {
                        calendarService.syncAirBnbCalendar(property.getId());
                    } catch (ParserException | IOException | ParseException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private FileOutputStream createIcsFile(String filename, String path) throws FileNotFoundException {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = path + File.separator + filename;
        return new FileOutputStream(filePath);
    }
}
