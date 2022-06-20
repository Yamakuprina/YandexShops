package yandex.backendschool.shops.model;

import java.util.List;

public class ShopUnitImportRequest {
    List<ShopUnitImport> items;
    String updateDate;

    public List<ShopUnitImport> getItems() {
        return items;
    }

    public void setItems(List<ShopUnitImport> items) {
        this.items = items;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
