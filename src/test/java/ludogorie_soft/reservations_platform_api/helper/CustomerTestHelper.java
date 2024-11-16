package ludogorie_soft.reservations_platform_api.helper;

import ludogorie_soft.reservations_platform_api.entity.Customer;

import java.util.UUID;

public class CustomerTestHelper {

    private static final UUID DEFAULT_ID = UUID.randomUUID();
    private static final String FIRST_NAME = "Petar";
    private static final String LAST_NAME = "Petrov";
    private static final String EMAIL = "test@example.com";
    private static final String PHONE_NUMBER = "+381 123 456 789";


    public static Customer createCustomer(){
        Customer customer = new Customer();
        customer.setId(DEFAULT_ID);
        customer.setFirstName(FIRST_NAME);
        customer.setLastName(LAST_NAME);
        customer.setEmail(EMAIL);
        customer.setPhoneNumber(PHONE_NUMBER);

        return customer;
    }
}
