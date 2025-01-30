package ludogorie_soft.reservations_platform_api.helper;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.dto.PropertyResponseDto;
import ludogorie_soft.reservations_platform_api.entity.Property;

import java.util.UUID;

public class PropertyTestHelper {

    private static final String DEFAULT_USER_EMAIL = "test@email.com";

    private static final UUID DEFAULT_PROPERTY_ID = UUID.randomUUID();
    private static final String DEFAULT_PROPERTY_RULES = "No smoking allowed";
    private static final String DEFAULT_WEBSITE_URL = "http://test.com";
    private static final int DEFAULT_CAPACITY = 4;
    private static final boolean DEFAULT_PET_ALLOWED = true;
    private static final String DEFAULT_PET_RULES = "Pets must be supervised at all times";
    private static final int DEFAULT_PRICE = 20;
    private static final int DEFAULT_PET_PRICE = 10;
    private static final int DEFAULT_MINIMUM_STAY = 1;


    private static final UUID UPDATED_PROPERTY_ID = UUID.randomUUID();
    private static final String UPDATED_PROPERTY_RULES = "Guests must be quiet after 10 PM";
    private static final String UPDATED_WEBSITE_URL = "http://example_updated.com";
    private static final String UPDATED_OWNER_EMAIL = "updated_mail@mail.com";
    private static final int UPDATED_CAPACITY = 6;
    private static final boolean UPDATED_PET_ALLOWED = false;
    private static final String UPDATED_PET_RULES = "No pets";
    private static final int UPDATED_PRICE = 40;
    private static final int UPDATED_PET_PRICE = 20;
    private static final int UPDATED_MINIMUM_STAY = 2;


    public static Property createDefaultProperty() {
        Property property = new Property();
        property.setId(DEFAULT_PROPERTY_ID);
        property.setOwner(UserTestHelper.createTestUser());
        property.setWebsiteUrl(DEFAULT_WEBSITE_URL);
        property.setCapacity(DEFAULT_CAPACITY);
        property.setPetAllowed(DEFAULT_PET_ALLOWED);
        property.setPetRules(DEFAULT_PET_RULES);
        property.setPrice(DEFAULT_PRICE);
        property.setMinimumStay(DEFAULT_MINIMUM_STAY);
        property.setPetPrice(DEFAULT_PET_PRICE);
        property.setPropertyRules(DEFAULT_PROPERTY_RULES);

        return property;
    }

    public static Property createPropertyForIntegrationTest(){
        Property property = new Property();

        property.setWebsiteUrl(DEFAULT_WEBSITE_URL);
        property.setCapacity(DEFAULT_CAPACITY);
        property.setPetAllowed(DEFAULT_PET_ALLOWED);
        property.setPetRules(DEFAULT_PET_RULES);
        property.setPrice(DEFAULT_PRICE);
        property.setMinimumStay(DEFAULT_MINIMUM_STAY);
        property.setPetPrice(DEFAULT_PET_PRICE);
        property.setPropertyRules(DEFAULT_PROPERTY_RULES);

        return property;
    }

    public static Property createExpiredPropertyForIntegrationTest() {
        Property property = new Property();

        property.setWebsiteUrl(UPDATED_WEBSITE_URL);
        property.setCapacity(UPDATED_CAPACITY);
        property.setPetAllowed(UPDATED_PET_ALLOWED);
        property.setPetRules(UPDATED_PET_RULES);
        property.setPrice(UPDATED_PRICE);
        property.setMinimumStay(UPDATED_MINIMUM_STAY);
        property.setPetPrice(UPDATED_PET_PRICE);
        property.setPropertyRules(UPDATED_PROPERTY_RULES);

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
        dto.setMinimumStay(DEFAULT_MINIMUM_STAY);
        dto.setPetPrice(DEFAULT_PET_PRICE);
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
        dto.setMinimumStay(DEFAULT_MINIMUM_STAY);
        dto.setPetPrice(DEFAULT_PET_PRICE);
        dto.setPropertyRules(DEFAULT_PROPERTY_RULES);
        return dto;
    }

    public static PropertyRequestDto createUpdatedPropertyRequestDto() {
        PropertyRequestDto requestDto = new PropertyRequestDto();
        requestDto.setWebsiteUrl(UPDATED_WEBSITE_URL);
        requestDto.setOwnerEmail(UPDATED_OWNER_EMAIL);
        requestDto.setCapacity(UPDATED_CAPACITY);
        requestDto.setPetAllowed(UPDATED_PET_ALLOWED);
        requestDto.setPetRules(UPDATED_PET_RULES);
        requestDto.setPrice(UPDATED_PRICE);
        requestDto.setMinimumStay(UPDATED_MINIMUM_STAY);
        requestDto.setPetPrice(UPDATED_PET_PRICE);
        requestDto.setPropertyRules(UPDATED_PROPERTY_RULES);
        return requestDto;
    }

    public static Property createUpdatedProperty(PropertyRequestDto dto) {
        Property property = new Property();
        property.setId(UPDATED_PROPERTY_ID);
        property.setWebsiteUrl(dto.getWebsiteUrl());
        property.setCapacity(dto.getCapacity());
        property.setPetAllowed(dto.isPetAllowed());
        property.setPetRules(dto.getPetRules());
        property.setPrice(dto.getPrice());
        property.setMinimumStay(dto.getMinimumStay());
        property.setPetPrice(dto.getPetPrice());
        property.setPropertyRules(dto.getPropertyRules());
        return property;
    }

    public static PropertyResponseDto createUpdatedPropertyResponseDto(Property updatedProperty) {
        PropertyResponseDto response = new PropertyResponseDto();
        response.setId(updatedProperty.getId());
        response.setWebsiteUrl(updatedProperty.getWebsiteUrl());
        response.setCapacity(updatedProperty.getCapacity());
        response.setPetAllowed(updatedProperty.isPetAllowed());
        response.setPetRules(updatedProperty.getPetRules());
        response.setPrice(updatedProperty.getPrice());
        response.setMinimumStay(updatedProperty.getMinimumStay());
        response.setPetPrice(updatedProperty.getPetPrice());
        response.setPropertyRules(updatedProperty.getPropertyRules());
        return response;
    }


}
