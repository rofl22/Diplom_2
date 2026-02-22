package user;

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
@Feature("User Management")
@ExtendWith(AllureJunit5.class)
public class LoginUserTests extends BaseTest {

    private User user;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        initClients();
        //перед каждым тестом
        user = new User("Izym", "izym" + System.currentTimeMillis() + "@yandex.ru", "564Ybsmk937");
        userClient.createUser(user);
    }

    @Test
    @DisplayName("Авторизация пользователя")
    @Description("Авторизация пользователя под существующим логином")
    @Story("Авторизация")
    public void authorizationTest() {
        var response = userClient.loginUser(user);

        response.then().log().all()
                .assertThat().statusCode(200)
                .and().body("success", Matchers.is(true))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().body("user.email", Matchers.is(user.getEmail()))
                .and().body("user.name", Matchers.is(user.getName()));
    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    @Description("Авторизация пользователя c некорректным логином")
    @Story("Негативные сценарии")
    public void authorizationIncorrectLoginTest() {
        User loginUser = new User(user.getEmail(), user.getPassword());
        loginUser.setEmail("Hdnasjdhbajhnwjdnakljndj2783o127" + user.getEmail());

        var response = userClient.loginUser(loginUser);
        userClient.checkFailedLoginResponse(response);
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    @Description("Авторизация пользователя c некорректным паролем")
    @Story("Негативные сценарии")
    public void authorizationIncorrectPasswordTest() {
        User loginUser = new User(user.getEmail(), user.getPassword());
        loginUser.setPassword("6482HSVbsj" + user.getPassword());

        var response = userClient.loginUser(loginUser);
        userClient.checkFailedLoginResponse(response);
    }

    @Test
    @DisplayName("Авторизация без логина")
    @Description("Авторизация пользователя без логина")
    @Story("Негативные сценарии")
    public void authorizationWithoutLoginTest() {
        User loginUser = new User();
        loginUser.setPassword(user.getPassword());

        var response = userClient.loginUser(loginUser);
        userClient.checkFailedLoginResponse(response);
    }

    @Test
    @DisplayName("Авторизация без пароля")
    @Description("Авторизация пользователя без пароля")
    @Story("Негативные сценарии")
    public void authorizationWithoutPasswordTest() {
        User loginUser = new User();
        loginUser.setEmail(user.getEmail());

        var response = userClient.loginUser(loginUser);
        userClient.checkFailedLoginResponse(response);
    }

    @Test
    @DisplayName("Авторизация без логина и пароля")
    @Description("Авторизация пользователя без логина и пароля")
    @Story("Негативные сценарии")
    public void authorizationWithoutLoginAndPasswordTest() {
        var response = userClient.loginUser(new User());
        userClient.checkFailedLoginResponse(response);
    }

    @AfterEach
    public void tearDown() {

        if (user != null && user.getEmail() != null) {
            try {

                Response loginResponse = userClient.loginUser(user);
                if (loginResponse.statusCode() == 200) {
                    String token = userClient.getAccessTokenFromResponse(loginResponse);
                    if (token != null) {
                        userClient.deleteUser(token);
                    }
                }
            } catch (Exception e) {

                System.err.println("Failed to delete user in tearDown: " + e.getMessage());
            }
        }
    }
}  // test1