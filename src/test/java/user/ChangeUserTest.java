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
public class ChangeUserTest extends BaseTest {

    private User user;
    private String accessToken;

    private final String modifiedName = "DGHsan";
    private final String modifiedEmail = "DGHann" + System.currentTimeMillis() + "@yandex.ru";
    private final String modifiedPassword = "1645sv";

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
    @DisplayName("Изменение имени пользователя с авторизацией")
    @Description("Успешное изменение имени пользователя с авторизацией")
    @Story("Изменение данных пользователя")
    public void changeUserNameWithAuthorizationTest() {
        User updateUser = new User();
        updateUser.setName(modifiedName);
        user.setName(modifiedName);

        var response = userClient.updateUserWithAuth(updateUser, accessToken);

        response.then().log().all()
                .assertThat().statusCode(200)
                .and().body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    @Description("Успешное изменение email пользователя с авторизацией")
    @Story("Изменение данных пользователя")
    public void changeUserEmailWithAuthorizationTest() {
        User updateUser = new User();
        updateUser.setEmail(modifiedEmail);
        user.setEmail(modifiedEmail);

        var response = userClient.updateUserWithAuth(updateUser, accessToken);

        response.then().log().all()
                .assertThat().statusCode(200)
                .and().body("success", Matchers.is(true));
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией")
    @Description("Успешное изменение пароля пользователя с авторизацией")
    @Story("Изменение данных пользователя")
    public void changeUserPasswordWithAuthorizationTest() {
        User updateUser = new User();
        updateUser.setPassword(modifiedPassword);
        user.setPassword(modifiedPassword);

        var response = userClient.updateUserWithAuth(updateUser, accessToken);

        userClient.checkSuccessUpdateResponse(response, user.getEmail(), user.getName());
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}