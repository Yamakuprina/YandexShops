package yandex.backendschool.shops.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class ShopUnitStatisticUnit {
    @Id
    String logId;
    @Column(nullable = false)
    String unitId;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    Date date;
    String parentId;
    @Enumerated
    @Column(nullable = false)
    ShopUnitType type;
    Integer price;

    public ShopUnitStatisticUnit(String id, String name, Date date, String parentId, ShopUnitType type, Integer price) {
        this.logId = id + date.toString();
        this.unitId = id;
        this.name = name;
        this.date = date;
        this.parentId = parentId;
        this.type = type;
        this.price = price;
    }

    public ShopUnitStatisticUnit(ShopUnit shopUnit) {
        this.logId = shopUnit.getId() + shopUnit.getDate();
        this.unitId = shopUnit.getId();
        this.name = shopUnit.getName();
        this.date = shopUnit.getRealDate();
        this.parentId = shopUnit.getParentId();
        this.type = shopUnit.getType();
        this.price = shopUnit.getPrice();
    }

    public ShopUnitStatisticUnit() {
    }

    public String getId() {
        return unitId;
    }

    public void setId(String id) {
        this.unitId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public ShopUnitType getType() {
        return type;
    }

    public void setType(ShopUnitType type) {
        this.type = type;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
