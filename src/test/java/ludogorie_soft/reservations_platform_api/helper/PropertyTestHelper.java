package ludogorie_soft.reservations_platform_api.helper;

import ludogorie_soft.reservations_platform_api.dto.PropertyRequestDto;
import ludogorie_soft.reservations_platform_api.entity.Property;

import java.util.UUID;

public class PropertyTestHelper {
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_WEBSITE_URL = "www.test.com";
    private static final int TEST_PROPERTY_PRICE = 20;
    private static final int TEST_PROPERTY_CAPACITY = 6;
    private static final int TEST_MINIMUM_STAY = 1;


    public static Property createProperty() {
        Property property = new Property();
        property.setId(UUID.randomUUID());
        property.setOwner(UserTestHelper.createTestUser());
        property.setWebsiteUrl(TEST_WEBSITE_URL);
        property.setCapacity(TEST_PROPERTY_CAPACITY);
        property.setPrice(TEST_PROPERTY_PRICE);
        property.setMinimumStay(TEST_MINIMUM_STAY);
        return property;
    }

    public static PropertyRequestDto createPropertyRequest() {
        PropertyRequestDto propertyRequestDto = new PropertyRequestDto();
        propertyRequestDto.setOwnerEmail(TEST_EMAIL);
        propertyRequestDto.setWebsiteUrl(TEST_WEBSITE_URL);
        propertyRequestDto.setPrice(TEST_PROPERTY_PRICE);
        propertyRequestDto.setCapacity(TEST_PROPERTY_CAPACITY);
        propertyRequestDto.setMinimumStay(TEST_MINIMUM_STAY);
        return propertyRequestDto;
    }
}
