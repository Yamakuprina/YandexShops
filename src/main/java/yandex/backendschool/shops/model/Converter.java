package yandex.backendschool.shops.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Converter {
    public static List<ShopUnit> importRequestToShopUnits(ShopUnitImportRequest importRequest, Date updateDate) throws Exception {
        List<ShopUnit> units = new ArrayList<>();
        for (ShopUnitImport unitImport : importRequest.getItems()) {
            ShopUnit shopUnit = new ShopUnit(unitImport.getId(), unitImport.getName(), updateDate, unitImport.getParentId(), unitImport.getType(), unitImport.getPrice(), null);
            if (shopUnit.getType() == null) throw new Exception("Validation Failed");
            if (shopUnit.getName() == null) throw new Exception("Validation Failed");
            if (shopUnit.getId() == null) throw new Exception("Validation Failed");
            if (shopUnit.getType().equals(ShopUnitType.CATEGORY)) shopUnit.setChildren(new ArrayList<>());
            units.add(shopUnit);
        }
        return units;
    }

    public static List<ShopUnitStatisticUnit> shopUnitsToStatisticUnits(List<ShopUnit> shopUnits) {
        List<ShopUnitStatisticUnit> statisticUnits = new ArrayList<>();
        for (ShopUnit shopUnit : shopUnits) {
            ShopUnitStatisticUnit statisticUnit = new ShopUnitStatisticUnit(shopUnit);
            statisticUnits.add(statisticUnit);
        }
        return statisticUnits;
    }
}
