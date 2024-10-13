package ludogorie_soft.reservations_platform_api.service.impl;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.entity.User;
import ludogorie_soft.reservations_platform_api.exception.ResourceNotFoundException;
import ludogorie_soft.reservations_platform_api.repository.PropertyRepository;
import ludogorie_soft.reservations_platform_api.service.CalendarService;
import ludogorie_soft.reservations_platform_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        propertyRequestDto.setOwnersEmail("ownerEmailTest@test.com");

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
}
