import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {

    private final String baseUri = "https://qa-scooter.praktikum-services.ru";
    private String courierId;
    private Courier testCourier;

    @Before
    public void setUp() {
        RestAssured.reset();
        RestAssured.baseURI = baseUri;
        RestAssured.filters(new AllureRestAssured());

        testCourier = new Courier("testLogin" + System.currentTimeMillis(), "1234", "TestUser");
        courierId = createCourier(testCourier);
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Step("Создание курьера")
    private String createCourier(Courier courier) {
        given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", is(true));

        // Получим ID через логин
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract().path("id").toString();
    }

    @Step("Удаление курьера")
    private void deleteCourier(String id) {
        given()
                .when()
                .delete("/api/v1/courier/" + id)
                .then()
                .statusCode(200)
                .body("ok", is(true));
    }

    @Step("Отправка запроса логина")
    private Response loginCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    @Test
    public void loginShouldBeSuccessful() {
        loginCourier(testCourier)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    public void loginWithWrongPasswordShouldFail() {
        Courier wrongPass = new Courier(testCourier.getLogin(), "wrongpass", null);
        loginCourier(wrongPass)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void loginWithWrongLoginShouldFail() {
        Courier wrongLogin = new Courier("noSuchUser", testCourier.getPassword());
        loginCourier(wrongLogin)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    public void loginWithoutLoginShouldReturn400() {
        Courier noLogin = new Courier("", testCourier.getPassword());
        loginCourier(noLogin)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    public void loginWithoutPasswordShouldReturn400() {
        Courier noPass = new Courier(testCourier.getLogin(), "");
        loginCourier(noPass)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
