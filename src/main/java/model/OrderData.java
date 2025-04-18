package model;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderData {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private List<String> color;

    public static OrderData withColor(List<String> color) {
        return new OrderData(
                "Арбуз", "Арбузов", "Москва", "2",
                "87776665544", 1, "2025-04-14T21:00:00.000Z", "Autotest", color
        );
    }
}
