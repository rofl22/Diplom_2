package order;

import api.stellarburgers.Ingredients;
import api.stellarburgers.Order;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Epic("Stellar Burgers API")
@Feature("Order Management")
@ExtendWith(AllureJunit5.class)
public class CreateOrderTest extends BaseTest {

    private User user;
    private String accessToken;
    private List<String> ingredientIds;
    private Order order;

    @BeforeEach
    @Override
    public void initClients() {
        super.initClients();

        // Создание тестового пользователя с уникальными данными
        user = new User("Izum", "Izum" + System.currentTimeMillis() + "@yandex.ru", "GHkdjd68362");
        Response createResponse = userClient.createUser(user);
        createResponse.then().statusCode(200); // Проверяем, что пользователь создан

        accessToken = userClient.getAccessToken(user);
        Assertions.assertNotNull(accessToken, "Access token should not be null");

        ingredientIds = new ArrayList<>();
        order = new Order(ingredientIds);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Успешное создание заказа с авторизацией")
    @Story("Создание заказа")
    public void createOrderWithAuthorizationTest() {
        Ingredients ingredients = orderClient.getIngredient();
        ingredientIds.add(ingredients.getData().get(1).get_id());
        ingredientIds.add(ingredients.getData().get(2).get_id());
        ingredientIds.add(ingredients.getData().get(3).get_id());
        ingredientIds.add(ingredients.getData().get(4).get_id());
        ingredientIds.add(ingredients.getData().get(5).get_id());
        ingredientIds.add(ingredients.getData().get(7).get_id());
        ingredientIds.add(ingredients.getData().get(8).get_id());

        var response = orderClient.createOrderWithAuthorization(order, accessToken);

        response.then().log().all()
                .assertThat().statusCode(200)
                .and().body("success", Matchers.is(true))
                .and().body("name", Matchers.notNullValue())
                .and().body("order.number", Matchers.any(Integer.class));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Успешное создание заказа без авторизации")
    @Story("Создание заказа")
    public void createOrderWithoutAuthorizationTest() {
        Ingredients ingredients = orderClient.getIngredient();
        ingredientIds.add(ingredients.getData().get(1).get_id());
        ingredientIds.add(ingredients.getData().get(2).get_id());
        ingredientIds.add(ingredients.getData().get(3).get_id());

        var response = orderClient.createOrderWithoutAuthorization(order);

        response.then().log().all()
                .assertThat().statusCode(200)
                .and().body("success", Matchers.is(true))
                .and().body("name", Matchers.notNullValue())
                .and().body("order.number", Matchers.any(Integer.class));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов и без авторизации")
    @Description("Проверка создания заказа без ингредиентов и без авторизации")
    @Story("Негативные сценарии")
    public void createEmptyOrderWithoutAuthorization() {
        var response = orderClient.createOrderWithoutAuthorization(order);
        orderClient.checkFailedResponseApiOrders(response);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов с авторизацией")
    @Description("Проверка создания заказа без ингредиентов с авторизацией")
    @Story("Негативные сценарии")
    public void createEmptyOrderWithAuthorization() {
        var response = orderClient.createOrderWithAuthorization(order, accessToken);
        orderClient.checkFailedResponseApiOrders(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хэшем ингредиентов")
    @Description("Проверка создания заказа без авторизации с неверным хэшем ингредиентов")
    @Story("Негативные сценарии")
    public void createOrderWithoutAuthorizationWithWrongHashTest() {
        Ingredients ingredients = orderClient.getIngredient();
        ingredientIds.add(ingredients.getData().get(0).get_id() + "jkhiujkjnhf8");
        ingredientIds.add(ingredients.getData().get(1).get_id() + "9876jknklhnj2");

        var response = orderClient.createOrderWithoutAuthorization(order);
        response.then().log().all()
                .statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией с неверным хешем ингредиентов")
    @Description("Проверка создания заказа с авторизацией с неверным хешем ингредиентов")
    @Story("Негативные сценарии")
    public void createOrderWithAuthorizationWithWrongHashTest() {
        Ingredients ingredients = orderClient.getIngredient();
        ingredientIds.add(ingredients.getData().get(1).get_id() + "khilunjlknjkbyg9876");
        ingredientIds.add(ingredients.getData().get(2).get_id() + "op9iuojuigtyfjkuhh");

        var response = orderClient.createOrderWithAuthorization(order, accessToken);
        response.then().log().all()
                .statusCode(500);
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}