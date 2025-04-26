import io.qameta.allure.Step;
import model.Courier;
import org.apache.commons.lang3.RandomStringUtils;

public class CourierGenerator {

    @Step("Генерация курьера с случайными логином и паролем")
    public static Courier random() {
        String login = RandomStringUtils.randomAlphabetic(8);
        String password = RandomStringUtils.randomAlphanumeric(10);
        return new Courier(login, password);
    }
}