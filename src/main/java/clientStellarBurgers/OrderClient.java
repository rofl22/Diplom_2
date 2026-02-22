package clientStellarBurgers;

import api.stellarburgers.Ingredients;
import api.stellarburgers.Order;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.given;

public class OrderClient {

    @Step("Получение данных об ингредиентах.")
    public Ingredients getIngredient() {
        return given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .log().all()
                .get("/api/ingredients")
                .body()
                .as(Ingredients.class);
    }

    @Step("Создание заказа с авторизацией.")
    public Response createOrderWithAuthorization(Order order, String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Создание заказа без авторизации.")
    public Response createOrderWithoutAuthorization(Order order) {
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Content-Type", "application/json")
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Проверка ответа при создании заказа без ингредиентов.")
    public void checkFailedResponseApiOrders(Response response) {
        response.then().log().all()
                .assertThat().statusCode(400)
                .and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("Ingredient ids must be provided"));
    }

    @Step("Получение списка заказов пользователя.")
    public Response getUserOrders(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .when()
                .get("/api/orders");
    }

    @Step("Получение списка заказов без авторизации.")
    public Response getUserOrdersWithoutAuth() {
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Content-Type", "application/json")
                .when()
                .get("/api/orders");
    }
}