import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.OrderData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;

public class GetOrdersListTest {

    private String createdOrderTrack;

    @Before
    public void setup() {
        RestAssured.reset();
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        RestAssured.filters(new AllureRestAssured());
        createOrder();
    }

    @Step("Создать заказ с цветом BLACK")
    private void createOrder() {
        OrderData order = OrderData.withColor(Collections.singletonList("BLACK"));

        Response response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post("/api/v1/orders");

        createdOrderTrack = response.body().jsonPath().getString("track");
    }

    @Test
    @Step("Проверка списка заказов")
    public void checkGetOrdersList() {
        Response response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/orders");

        response.then()
                .assertThat()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0));
    }

    @After
    @Step("Отменить заказ с track: {track}") //тут даже при успешной отмене приходит код 400 (проверно через UI и postman)
    public void cancelOrder() {
        String body = "{\"track\": " + createdOrderTrack + "}";

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put("/api/v1/orders/cancel")
                .then()
                .assertThat()
                .statusCode(200);
    }
}
