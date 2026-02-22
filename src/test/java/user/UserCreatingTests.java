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
public class UserCreatingTests extends BaseTest {

    private User user;
    private String uniqueEmail;

    @BeforeEach
    @Override
    public void initClients() {
        super.initClients();
        user = new User();
        uniqueEmail = "user" + System.currentTimeMillis() + "@yandex.ru";
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Регистрация уникального пользователя c корректными данными")
    @Story("Регистрация")
    public void checkCreateUserTest() {
        user = new User("Izym", uniqueEmail, "564Ybsmk937");

        var response = userClient.createUser(user);

        response.then().log().all()
                .assertThat().statusCode(200)
                .and().body("success", Matchers.is(true))
                .and().body("accessToken", Matchers.notNullValue())
                .and().body("refreshToken", Matchers.notNullValue())
                .and().body("user.email", Matchers.is(uniqueEmail))
                .and().body("user.name", Matchers.is("Izym"));
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Регистрация уже зарегистрированного пользователя")
    @Story("Негативные сценарии")
    public void checkRegisteredUserTest() {
        user = new User("Izym", uniqueEmail, "564Ybsmk937");
        userClient.createUser(user);

        var response = userClient.createUser(user);

        response.then().log().all()
                .assertThat().statusCode(403)
                .and().body("success", Matchers.is(false))
                .and().body("message", Matchers.is("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Регистрация пользователя без имени, но с заполненными email и password")
    @Story("Негативные сценарии")
    public void createUserWithoutNameTest() {
        user.setEmail(uniqueEmail);
        user.setPassword("564Ybsmk937");

        var response = userClient.createUser(user);
        userClient.checkFailedRegisterResponse(response);
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Регистрация пользователя без email, но с заполненными именем и паролем")
    @Story("Негативные сценарии")
    public void createUserWithoutEmailTest() {
        user.setName("Izym");
        user.setPassword("564Ybsmk937");

        var response = userClient.createUser(user);
        userClient.checkFailedRegisterResponse(response);
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Регистрация пользователя без пароля, но с заполненными именем и email")
    @Story("Негативные сценарии")
    public void createUserWithoutPasswordTest() {
        user.setEmail(uniqueEmail);
        user.setName("Izym");

        var response = userClient.createUser(user);
        userClient.checkFailedRegisterResponse(response);
    }

    @Test
    @DisplayName("Создание пользователя без имени и email")
    @Description("Регистрация пользователя без имени и email, но с заполненным паролем")
    @Story("Негативные сценарии")
    public void createUserWithoutNameAndEmailTest() {
        user.setPassword("564Ybsmk937");

        var response = userClient.createUser(user);
        userClient.checkFailedRegisterResponse(response);
    }

    @Test
    @DisplayName("Создание пользователя без имени и пароля")
    @Description("Регистрация пользователя без имени и пароля, но с заполненным email")
    @Story("Негативные сценарии")
    public void createUserWithoutNameAndPasswordTest() {
        user.setEmail(uniqueEmail);

        var response = userClient.createUser(user);
        userClient.checkFailedRegisterResponse(response);
    }

    @Test
    @DisplayName("Создание пользователя без email и пароля")
    @Description("Регистрация пользователя без email и пароля, но с заполненным именем")
    @Story("Негативные сценарии")
    public void createUserWithoutEmailAndPasswordTest() {
        user.setName("Izym");

        var response = userClient.createUser(user);
        userClient.checkFailedRegisterResponse(response);
    }

    @Test
    @DisplayName("Создание пользователя без всех полей")
    @Description("Регистрация пользователя без имени, email и пароля")
    @Story("Негативные сценарии")
    public void createUserWithoutAllFieldsTest() {
        var response = userClient.createUser(user);
        userClient.checkFailedRegisterResponse(response);
    }

    @AfterEach
    public void tearDown() {
        if (user.getEmail() != null && user.getPassword() != null) {
            String accessToken = userClient.getAccessToken(user);
            if (accessToken != null) {
                userClient.deleteUser(accessToken);
            }
        }
    }
}