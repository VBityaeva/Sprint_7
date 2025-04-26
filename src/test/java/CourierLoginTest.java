import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import org.junit.*;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

public class CourierLoginTest {

    private final CourierApi courierApi = new CourierApi();
    private Courier courier;

    @Before
    public void setUp() {
        courier = CourierGenerator.random();
        courierApi.createAndLogin(courier);
    }

    @After
    public void tearDown() {
        courierApi.deleteByLoginAndPassword(courier);
    }

    @Test
    @Description("Успешный логин с валидными данными")
    public void courierCanLoginWithValidCredentials() {
        ValidatableResponse response = courierApi.login(courier);
        response.statusCode(SC_OK).body("id", notNullValue());
    }

    @Test
    @Description("Ошибка логина с неверным паролем")
    public void courierCannotLoginWithWrongPassword() {
        Courier wrongPasswordCourier = new Courier(courier.getLogin(), "wrongPassword");
        ValidatableResponse response = courierApi.login(wrongPasswordCourier);
        response.statusCode(SC_NOT_FOUND).body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Description("Ошибка при авторизации без логина")
    public void shouldReturn400IfLoginMissingWhenLogin() {
        Courier courierWithoutLogin = new Courier("", "somePassword");

        courierApi.login(courierWithoutLogin)
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для входа"));
    }

    @Test
    @Description("Ошибка при авторизации без пароля")
    public void shouldReturn400IfPasswordMissingWhenLogin() {
        Courier courierWithoutPassword = new Courier("someLogin", "");

        courierApi.login(courierWithoutPassword)
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для входа"));
    }
}