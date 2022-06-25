package yandex.backendschool.shops.model;

public class ShopUnitImport {
    String id;
    String name;
    String parentId;
    Integer price;
    ShopUnitType type;

    public ShopUnitImport(String id, String name, String parentId, Integer price, ShopUnitType type) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.price = price;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public ShopUnitType getType() {
        return type;
    }

    public void setType(ShopUnitType type) {
        this.type = type;
    }
}
