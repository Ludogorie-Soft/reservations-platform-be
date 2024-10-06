package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.service.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping("/")
    public ResponseEntity<PropertyResponseDto> createProperty(@RequestBody PropertyRequestDto request) {
        PropertyResponseDto response = propertyService.createProperty(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/air-bnb/{id}")
    public ResponseEntity<PropertyResponseDto> updateAirBnbUrl(@PathVariable("id") Long id, @RequestParam("url") String url) throws FileNotFoundException {
        PropertyResponseDto responseDto = propertyService.updateAirBnbUrlOfProperty(id, url);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/booking/{id}")
    public ResponseEntity<PropertyResponseDto> updateBookingUrl(@PathVariable("id") Long id, @RequestParam("url") String url) throws FileNotFoundException {
        PropertyResponseDto responseDto = propertyService.updateBookingUrlOfProperty(id, url);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<PropertyResponseDto>> getAllProperties() {
        List<PropertyResponseDto> propertyDTOs = propertyService.getAllProperties();
        return ResponseEntity.ok(propertyDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> getPropertyById(@PathVariable Long id) {
        Optional<PropertyResponseDto> property = propertyService.getPropertyById(id);
        return property.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok("Property deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> updateProperty(@PathVariable Long id, @RequestBody PropertyRequestDto propertyRequestDto) {
        PropertyResponseDto updatedProperty = propertyService.updateProperty(id, propertyRequestDto);
        return ResponseEntity.ok(updatedProperty);
    }
}
