package ludogorie_soft.reservations_platform_api.service.impl;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.entity.User;
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

        User user = userService.getUserByEmailOrUsername(propertyRequestDto.getOwnersEmail(), propertyRequestDto.getOwnersEmail());

        Property property = new Property();
        property.setName(propertyRequestDto.getName());
        property.setType(propertyRequestDto.getType());
        property.setOwner(user);

        Property createdProperty = propertyRepository.save(property);
        return modelMapper.map(createdProperty, PropertyResponseDto.class);
    }

    @Override
    public PropertyResponseDto updateAirBnbUrlOfProperty(Long id, String url) throws FileNotFoundException {
        Property property = findById(id);
        property.setAirBnbUrl(url);
        Property updatedProperty = propertyRepository.save(property);
        createIcsFile("airBnbCalendar-" + property.getId() + ".ics", icsAirBnbDirectory);
        return modelMapper.map(updatedProperty, PropertyResponseDto.class);
    }

    @Override
    public PropertyResponseDto updateBookingUrlOfProperty(Long id, String url) throws FileNotFoundException {
        Property property = findById(id);
        property.setBookingUrl(url);
        Property updatedProperty = propertyRepository.save(property);
        createIcsFile("bookingCalendar-" + property.getId() + ".ics", icsBookingDirectory);
        return modelMapper.map(updatedProperty, PropertyResponseDto.class);
    }

    @Override
    public Property findById(Long id) {
        return propertyRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Property not found!")
        );
    }

    @Scheduled(fixedRate = 3600000)
    public void syncPropertiesWithAirBnbUrls() {
        List<Property> properties = propertyRepository.findAll();

        properties.stream()
                .filter(property -> property.getAirBnbUrl() != null && !property.getAirBnbUrl().trim().isEmpty())
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
