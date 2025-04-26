import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.with;

public class BaseApi {

    static {
        RestAssured.reset();
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        RestAssured.filters(new AllureRestAssured());
    }

    protected RequestSpecification getBaseSpec() {
        return with()
                .baseUri(RestAssured.baseURI)
                .header("Content-type", "application/json");
    }
}