package order;

import api.stellarburgers.User;
import base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@Epic("Stellar Burgers API")
@Feature("Order Management")
@ExtendWith(AllureJunit5.class)
public class OrderGetTests extends BaseTest {

    private User user;
    private String accessToken;

    @BeforeEach
    @Override
    public void initClients() {
        super.initClients();

        user = new User("Izum", "Izum" + System.currentTimeMillis() + "@yandex.ru", "GHkdjd68362");
        Response createResponse = userClient.createUser(user);
        createResponse.then().statusCode(200);

        accessToken = userClient.getAccessToken(user);
        Assertions.assertNotNull(accessToken, "Access token should not be null");
    }

    @Test
    @DisplayName("Получение списка заказов авторизованного пользователя")
    @Description("Успешная проверка получения списка заказов авторизованного пользователя")
    @Story("Получение заказов")
    public void getUserOrderWithAuthorizationTest() {
        var response = orderClient.getUserOrders(accessToken);

        response.then().log().all()
                .assertThat().statusCode(200)
                .and().body("success", Matchers.is(true))
                .and().body("orders", Matchers.notNullValue())
                .and().body("total", Matchers.any(Integer.class))
                .and().body("totalToday", Matchers.any(Integer.class));
    }

    @Test
    @DisplayName("Получение списка заказов без авторизации")
    @Description("Неуспешная проверка получения списка заказов без авторизации")
    @Story("Получение заказов")
    public void getUserOrderWithoutAuthorizationTest() {
        var response = orderClient.getUserOrdersWithoutAuth();

        response.then().log().all()
                .assertThat().statusCode(401)
                .and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("You should be authorised"));
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}