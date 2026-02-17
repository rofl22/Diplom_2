package user;

import api.stellarburgers.User;
import base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.junit5.AllureJunit5;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

@Epic("Stellar Burgers API")
@Feature("User Management")
@ExtendWith(AllureJunit5.class)
public class LoginUserTests extends BaseTest {

    private User user;

    @BeforeEach
    public void setUp() {
        initClients();
        user = new User("Izym", "izymizymizym@yandex.ru", "564Ybsmk937");
    }

    @Test
    @DisplayName("Авторизация пользователя")
    @Description("Авторизация пользователя под существующим логином")
    @Story("Авторизация")
    public void authorizationTest() {
        userClient.createUser(user);

        var response = userClient.loginUser(user);

        response.then().log().all()
                .assertThat().statusCode(200)
                .and().body("success", Matchers.is(true))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().body("user.email", Matchers.notNullValue())
                .and().body("user.name", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    @Description("Авторизация пользователя c некорректным логином")
    @Story("Негативные сценарии")
    public void authorizationIncorrectLoginTest() {
        userClient.createUser(user);

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
        userClient.createUser(user);

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
        userClient.createUser(user);

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
        userClient.createUser(user);

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
        userClient.createUser(user);

        var response = userClient.loginUser(new User());
        userClient.checkFailedLoginResponse(response);
    }

    @AfterEach
    public void tearDown() {
        String accessToken = userClient.getAccessToken(user);
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}