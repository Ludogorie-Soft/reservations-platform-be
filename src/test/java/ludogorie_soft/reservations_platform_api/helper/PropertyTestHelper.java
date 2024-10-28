package ludogorie_soft.reservations_platform_api.helper;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;
import ludogorie_soft.reservations_platform_api.entity.User;

import java.util.UUID;

public class PropertyTestHelper {

    public static final String DEFAULT_USER_EMAIL = "userEmailTest@test.com";

    public static final UUID DEFAULT_PROPERTY_ID = UUID.randomUUID();
    public static final String DEFAULT_PROPERTY_RULES = "No smoking allowed";
    public static final String DEFAULT_WEBSITE_URL = "http://example.com";
    public static final int DEFAULT_CAPACITY = 4;
    public static final boolean DEFAULT_PET_ALLOWED = true;
    public static final String DEFAULT_PET_RULES = "Pets must be supervised at all times";
    public static final int DEFAULT_PRICE = 100;

    public static final UUID UPDATED_PROPERTY_ID = UUID.randomUUID();
    public static final String UPDATED_PROPERTY_RULES = "Guests must be quiet after 10 PM";
    public static final String UPDATED_WEBSITE_URL = "http://example_updated.com";
    public static final int UPDATED_CAPACITY = 6;
    public static final boolean UPDATED_PET_ALLOWED = false;
    public static final String UPDATED_PET_RULES = "No pets";
    public static final int UPDATED_PRICE = 200;

    public static User createDefaultUser() {
        User user = new User();
        user.setEmail(DEFAULT_USER_EMAIL);
        return user;
    }

    public static Property createDefaultProperty() {
        Property property = new Property();
        property.setId(DEFAULT_PROPERTY_ID);
        property.setOwner(createDefaultUser());
        property.setWebsiteUrl(DEFAULT_WEBSITE_URL);
        property.setCapacity(DEFAULT_CAPACITY);
        property.setPetAllowed(DEFAULT_PET_ALLOWED);
        property.setPetRules(DEFAULT_PET_RULES);
        property.setPrice(DEFAULT_PRICE);
        property.setPropertyRules(DEFAULT_PROPERTY_RULES);

        return property;
    }

    public static PropertyRequestDto createDefaultPropertyRequestDto() {
        PropertyRequestDto dto = new PropertyRequestDto();
        dto.setOwnerEmail(DEFAULT_USER_EMAIL);
        dto.setWebsiteUrl(DEFAULT_WEBSITE_URL);
        dto.setCapacity(DEFAULT_CAPACITY);
        dto.setPetAllowed(DEFAULT_PET_ALLOWED);
        dto.setPetRules(DEFAULT_PET_RULES);
        dto.setPrice(DEFAULT_PRICE);
        dto.setPropertyRules(DEFAULT_PROPERTY_RULES);
        return dto;
    }

    public static PropertyResponseDto createDefaultPropertyResponseDto() {
        PropertyResponseDto dto = new PropertyResponseDto();
        dto.setWebsiteUrl(DEFAULT_WEBSITE_URL);
        dto.setCapacity(DEFAULT_CAPACITY);
        dto.setPetAllowed(DEFAULT_PET_ALLOWED);
        dto.setPetRules(DEFAULT_PET_RULES);
        dto.setPrice(DEFAULT_PRICE);
        dto.setPropertyRules(DEFAULT_PROPERTY_RULES);
        return dto;
    }

    public static PropertyRequestDto createUpdatedPropertyRequestDto() {
        PropertyRequestDto dto = new PropertyRequestDto();
        dto.setWebsiteUrl(UPDATED_WEBSITE_URL);
        dto.setCapacity(UPDATED_CAPACITY);
        dto.setPetAllowed(UPDATED_PET_ALLOWED);
        dto.setPetRules(UPDATED_PET_RULES);
        dto.setPropertyRules(UPDATED_PROPERTY_RULES);
        dto.setPrice(UPDATED_PRICE);
        return dto;
    }

    public static Property createUpdatedProperty(PropertyRequestDto dto) {
        Property property = new Property();
        property.setId(UPDATED_PROPERTY_ID);
        property.setWebsiteUrl(dto.getWebsiteUrl());
        property.setCapacity(dto.getCapacity());
        property.setPetAllowed(dto.isPetAllowed());
        property.setPetRules(dto.getPetRules());
        property.setPropertyRules(dto.getPropertyRules());
        property.setPrice(dto.getPrice());
        return property;
    }

    public static PropertyResponseDto createUpdatedPropertyResponseDto(Property updatedProperty) {
        PropertyResponseDto response = new PropertyResponseDto();
        response.setId(updatedProperty.getId());
        response.setWebsiteUrl(updatedProperty.getWebsiteUrl());
        response.setCapacity(updatedProperty.getCapacity());
        response.setPetAllowed(updatedProperty.isPetAllowed());
        response.setPetRules(updatedProperty.getPetRules());
        response.setPropertyRules(updatedProperty.getPropertyRules());
        response.setPrice(updatedProperty.getPrice());
        return response;
    }
}
