import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.OrderData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final List<String> color;
    private String track;

    public CreateOrderTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет: {0}")
    public static Collection<Object[]> getColors() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList("BLACK")},
                {Arrays.asList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {null}
        });
    }

    @Before
    public void setup() {
        RestAssured.reset();
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    public void checkCreateOrderWithVariousColors() {
        OrderData order = OrderData.withColor(color);

        Response response = createOrder(order);

        response.then()
                .assertThat()
                .statusCode(201)
                .body("track", notNullValue());

        track = response.body().jsonPath().getString("track");
        System.out.println("Track ID: " + track);
    }

    @Step("Создать заказ с цветом: {order.color}")
    private Response createOrder(OrderData order) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @After
    @Step("Отменить заказ с track: {track}") //тут даже при успешной отмене приходит код 400 (проверно через UI и postman)
    public void cancelOrder() {
        if (track != null) {
            String body = "{\"track\": " + track + "}";
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
}
