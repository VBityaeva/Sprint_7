import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.OrderData;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest extends BaseApi {

    private final List<String> color;
    private String createdOrderTrack;
    private final OrderApi orderApi = new OrderApi();

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

    @Test
    @Step("Проверка создания заказа с разными цветами")
    public void checkCreateOrderWithVariousColors() {
        OrderData order = OrderData.withColor(color);
        Response response = orderApi.createOrder(order);

        response.then()
                .assertThat()
                .statusCode(SC_CREATED)
                .body("track", notNullValue());

        createdOrderTrack = response.body().jsonPath().getString("track");
    }

    @After
    @Step("Отменить заказ с track: {createdOrderTrack}")
    public void cancelOrder() {
        if (createdOrderTrack != null) {
            orderApi.cancelOrderByTrack(createdOrderTrack);
        }
    }
}