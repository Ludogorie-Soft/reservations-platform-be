package ludogorie_soft.reservations_platform_api.controller;

import lombok.RequiredArgsConstructor;
import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.service.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<PropertyResponseDto> createProperty(@RequestBody PropertyRequestDto request) {
        PropertyResponseDto response = propertyService.createProperty(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/air-bnb/{id}")
    public ResponseEntity<PropertyResponseDto> updateAirBnbUrl(@PathVariable("id") UUID id, @RequestParam("url") String url) throws FileNotFoundException {
        PropertyResponseDto responseDto = propertyService.updateAirBnbUrlOfProperty(id, url);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/booking/{id}")
    public ResponseEntity<PropertyResponseDto> updateBookingUrl(@PathVariable("id") UUID id, @RequestParam("url") String url) throws FileNotFoundException {
        PropertyResponseDto responseDto = propertyService.updateBookingUrlOfProperty(id, url);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponseDto>> getAllProperties() {
        List<PropertyResponseDto> propertyDTOs = propertyService.getAllProperties();
        return ResponseEntity.ok(propertyDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> getPropertyById(@PathVariable UUID id) {
        Optional<PropertyResponseDto> property = propertyService.getPropertyById(id);
        return property.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable UUID id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.ok("Property deleted successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponseDto> updateProperty(@PathVariable UUID id, @RequestBody PropertyRequestDto propertyRequestDto) {
        PropertyResponseDto updatedProperty = propertyService.updateProperty(id, propertyRequestDto);
        return ResponseEntity.ok(updatedProperty);
    }
}
