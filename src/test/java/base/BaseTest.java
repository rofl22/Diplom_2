package base;

import clientStellarBurgers.OrderClient;
import clientStellarBurgers.UserClient;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected static final String BASE_URI = "https://stellarburgers.education-services.ru";
    protected UserClient userClient;
    protected OrderClient orderClient;

    @BeforeAll
    public static void setUpBase() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void initClients() {
        userClient = new UserClient();
        orderClient = new OrderClient();
    }
}