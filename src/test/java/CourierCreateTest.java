import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import model.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierCreateTest {

    private String baseUri = "https://qa-scooter.praktikum-services.ru";
    private String courierId;

    @Before
    public void setUp() {
        RestAssured.reset();
        RestAssured.baseURI = baseUri;
        RestAssured.filters(new AllureRestAssured());
    }

    @Step("Генерация случайного курьера для тестирования")
    private Courier generateCourier() {
        int random = new Random().nextInt(99999);
        return new Courier("user" + random, "1234", "TestName");
    }

    @Step("Создание курьера с логином {0}")
    private String createCourier(Courier courier) {
        return requestSpec(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", is(true))
                .extract().path("id");
    }

    @Step("Удаление курьера с ID {0}")
    private void deleteCourier(String courierId) {
        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200)
                .body("ok", is(true));
    }

    // Универсальный метод для формирования запроса
    private RequestSpecification requestSpec(Object body) {
        return given()
                .header("Content-type", "application/json")
                .body(body);
    }

    @Test
    public void shouldCreateCourierSuccessfully() {
        Courier courier = generateCourier();
        courierId = createCourier(courier); // Упрощено
    }

    @Test
    public void shouldNotCreateDuplicateCourier() {
        Courier courier = generateCourier();
        courierId = createCourier(courier);

        requestSpec(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
    }

    @Test
    public void shouldReturn400IfLoginMissing() {
        Courier courier = new Courier(null, "1234", "NoLogin");

        requestSpec(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void shouldReturn400IfPasswordMissing() {
        Courier courier = new Courier("noPassLogin", null, "NoPass");

        requestSpec(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }
}