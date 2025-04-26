import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.OrderData;
import org.junit.*;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

public class GetOrdersListTest {

    private final OrderApi orderApi = new OrderApi();
    private String createdOrderTrack;

    @Before
    @Step("Создание заказа с цветом BLACK")
    public void setup() {
        createOrder();
    }

    private void createOrder() {
        OrderData order = OrderData.withColor(Collections.singletonList("BLACK"));
        Response response = orderApi.createOrder(order);
        response.then().statusCode(SC_CREATED).body("track", notNullValue());

        createdOrderTrack = response.jsonPath().getString("track");
    }

    @Test
    @Step("Проверка списка заказов")
    public void checkGetOrdersList() {
        Response response = orderApi.getOrders();
        response.then()
                .statusCode(SC_OK)
                .body("orders", notNullValue())
                .body("orders.size()", greaterThan(0));
    }

    @After
    @Step("Отменить заказ с track: {createdOrderTrack}")
    public void cancelOrder() {
        if (createdOrderTrack != null) {
            orderApi.cancelOrderByTrack(createdOrderTrack);
        }
    }
}