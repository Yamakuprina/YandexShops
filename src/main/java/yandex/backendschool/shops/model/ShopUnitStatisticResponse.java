package yandex.backendschool.shops.model;

import java.util.List;

public class ShopUnitStatisticResponse {
    List<ShopUnitStatisticUnit> items;

    public ShopUnitStatisticResponse(List<ShopUnitStatisticUnit> items) {
        this.items = items;
    }

    public List<ShopUnitStatisticUnit> getItems() {
        return items;
    }

    public void setItems(List<ShopUnitStatisticUnit> items) {
        this.items = items;
    }
}
