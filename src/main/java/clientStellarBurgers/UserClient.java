package clientStellarBurgers;

import api.stellarburgers.User;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class UserClient {


    public String getAccessTokenFromResponse(Response response) {
        if (response.statusCode() == 200) {
            return response.path("accessToken");
        }
        return null;
    }

    @Step("Создание нового пользователя.")
    public Response createUser(User user) {
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Логин пользователя.")
    public Response loginUser(User user) {
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/login");
    }

    @Step("Изменение данных пользователя с авторизацией.")
    public Response updateUserWithAuth(User user, String token) {
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(user)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Изменение данных пользователя без авторизации.")
    public Response updateUserWithoutAuth(User user) {
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .header("Content-Type", "application/json")
                .body(user)
                .when()
                .patch("/api/auth/user");
    }

    @Step("Удаление пользователя.")
    public Response deleteUser(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return given()
                .filter(new AllureRestAssured())
                .header("Authorization", token)
                .when()
                .delete("/api/auth/user");
    }

    @Step("Проверка неуспешного ответа при регистрации.")
    public void checkFailedRegisterResponse(Response response) {
        response.then().log().all()
                .assertThat().statusCode(403)
                .and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("Email, password and name are required fields"));
    }

    @Step("Проверка неуспешного ответа при логине.")
    public void checkFailedLoginResponse(Response response) {
        response.then().log().all()
                .assertThat().statusCode(401)
                .and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("email or password are incorrect"));
    }

    @Step("Проверка успешного ответа при изменении данных.")
    public void checkSuccessUpdateResponse(Response response, String email, String name) {
        response.then().log().all()
                .assertThat()
                .statusCode(200)
                .body("success", Matchers.is(true))
                .and().body("user.email", Matchers.is(email.toLowerCase(Locale.ROOT)))
                .and().body("user.name", Matchers.is(name));
    }

    @Step("Проверка неуспешного ответа при изменении данных.")
    public void checkFailedUpdateResponse(Response response) {
        response.then().log().all()
                .assertThat().statusCode(401)
                .and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("You should be authorised"));
    }

    @Step("Получение access token после логина.")
    public String getAccessToken(User user) {
        Response response = loginUser(user);
        if (response.statusCode() == 200) {
            String token = response.then().extract().path("accessToken");
            // Убираем префикс "Bearer " если он есть
            if (token != null && token.startsWith("Bearer ")) {
                return token.substring(7);
            }
            return token;
        }
        return null;
    }
}