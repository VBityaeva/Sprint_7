import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.OrderData;

import static org.apache.http.HttpStatus.*;

public class OrderApi extends BaseApi {

    private static final String CREATE_ORDER_URL = "/api/v1/orders";
    private static final String CANCEL_ORDER_URL = "/api/v1/orders/cancel";

    @Step("Создание заказа")
    public Response createOrder(OrderData order) {
        Response response = getBaseSpec()
                .body(order)
                .post(CREATE_ORDER_URL);

        return response;
    }

    @Step("Отмена заказа по треку: {track}")
    public Response cancelOrder(String track) {
        if (track != null) {
            String body = "{\"track\": \"" + track + "\"}";
            return getBaseSpec()
                    .body(body)
                    .put(CANCEL_ORDER_URL);
        }
        return null;
    }

    @Step("Получение списка заказов")
    public Response getOrders() {
        return getBaseSpec()
                .get("/api/v1/orders");
    }

    @Step("Отмена заказа и проверка кода {SC_OK}")
    public void cancelOrderByTrack(String track) {
        if (track != null) {
            cancelOrder(track).then().statusCode(SC_OK);
        }
    }
}