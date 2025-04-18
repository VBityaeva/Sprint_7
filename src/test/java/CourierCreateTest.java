import io.qameta.allure.Description;
import model.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CourierCreateTest {

    private final CourierApi courierApi = new CourierApi();
    private Courier courier;

    @Before
    public void setUp() {
        courier = CourierGenerator.random();
    }

    @After
    public void tearDown() {
        if (courier != null) {
            try {
                courierApi.deleteByLoginAndPassword(courier);
            } catch (AssertionError ignored) {
            }
        }
    }

    @Test
    @Description("Успешное создание курьера")
    public void shouldCreateCourierSuccessfully() {
        courierApi.create(courier)
                .statusCode(SC_CREATED)
                .body("ok", is(true));
    }

    @Test
    @Description("Нельзя создать дубликат курьера с тем же логином")
    public void shouldNotCreateDuplicateCourier() {
        courierApi.create(courier)
                .statusCode(SC_CREATED)
                .body("ok", is(true));

        courierApi.create(courier)
                .statusCode(SC_CONFLICT)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Test
    @Description("Ошибка при создании курьера без логина")
    public void shouldReturn400IfLoginMissing() {
        Courier courierWithoutLogin = new Courier(null, "1234");

        courierApi.create(courierWithoutLogin)
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Description("Ошибка при создании курьера без пароля")
    public void shouldReturn400IfPasswordMissing() {
        Courier courierWithoutPassword = new Courier("noPassLogin", null);

        courierApi.create(courierWithoutPassword)
                .statusCode(SC_BAD_REQUEST)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }
}