package ludogorie_soft.reservations_platform_api.helper;

import ludogorie_soft.reservations_platform_api.entity.Customer;

import java.util.UUID;

public class CustomerTestHelper {

    private static final UUID DEFAULT_ID = UUID.randomUUID();
    private static final String FIRST_NAME = "Petar";
    private static final String LAST_NAME = "Petrov";
    private static final String EMAIL = "test@example.com";
    private static final String PHONE_NUMBER = "0123456789";

    private static final String EXPIRED_FIRST_NAME = "Borko";
    private static final String EXPIRED_LAST_NAME = "Botev";
    private static final String EXPIRED_EMAIL = "testtest@example.com";
    private static final String EXPIRED_PHONE_NUMBER = "0963852741";

    public static Customer createCustomer(){
        Customer customer = new Customer();
        customer.setId(DEFAULT_ID);
        customer.setFirstName(FIRST_NAME);
        customer.setLastName(LAST_NAME);
        customer.setEmail(EMAIL);
        customer.setPhoneNumber(PHONE_NUMBER);

        return customer;
    }

    public static Customer createCustomerForIntegrationTest(){
        Customer customer = new Customer();

        customer.setFirstName(FIRST_NAME);
        customer.setLastName(LAST_NAME);
        customer.setEmail(EMAIL);
        customer.setPhoneNumber(PHONE_NUMBER);

        return customer;
    }

    public static Customer createExpiredCustomerForIntegrationTest() {
        Customer customer = new Customer();

        customer.setFirstName(EXPIRED_FIRST_NAME);
        customer.setLastName(EXPIRED_LAST_NAME);
        customer.setEmail(EXPIRED_EMAIL);
        customer.setPhoneNumber(EXPIRED_PHONE_NUMBER);

        return customer;
    }
}
