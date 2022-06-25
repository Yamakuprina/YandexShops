package yandex.backendschool.shops.service;

import yandex.backendschool.shops.model.ShopUnit;
import yandex.backendschool.shops.model.ShopUnitImportRequest;
import yandex.backendschool.shops.model.ShopUnitStatisticResponse;

public interface ShopUnitService {
    void importShopUnits(ShopUnitImportRequest importRequest) throws Exception;

    void deleteUnitById(String id) throws Exception;

    ShopUnit getUnitById(String id) throws Exception;

    ShopUnitStatisticResponse get24hSales(String stringDate) throws Exception;

    ShopUnitStatisticResponse getNodeStatisticHistoryBetweenDates(String id, String dateStart, String dateEnd) throws Exception;
}
