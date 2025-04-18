import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Courier;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CourierApi extends BaseApi {

    @Step("Создание курьера")
    public ValidatableResponse create(Courier courier) {
        return given()
                .spec(getBaseSpec())
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then();
    }

    @Step("Логин курьера")
    public ValidatableResponse login(Courier courier) {
        return given()
                .spec(getBaseSpec())
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then();
    }

    @Step("Создать курьера и сразу войти с ним")
    public int createAndLogin(Courier courier) {
        create(courier)
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        return loginAndGetId(courier);
    }

    @Step("Удаление курьера по ID")
    public ValidatableResponse delete(int courierId) {
        return given()
                .spec(getBaseSpec())
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then();
    }

    @Step("Получение ID курьера по логину и паролю")
    public int loginAndGetId(Courier courier) {
        return login(courier)
                .statusCode(SC_OK)
                .extract()
                .path("id");
    }

    @Step("Удаление курьера по логину и паролю")
    public void deleteByLoginAndPassword(Courier courier) {
        int id = loginAndGetId(courier);
        delete(id).statusCode(SC_OK);
    }
}