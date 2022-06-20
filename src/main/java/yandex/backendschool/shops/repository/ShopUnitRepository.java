package yandex.backendschool.shops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yandex.backendschool.shops.model.ShopUnit;
import yandex.backendschool.shops.model.ShopUnitType;

import java.util.Date;
import java.util.List;

@Repository
public interface ShopUnitRepository extends JpaRepository<ShopUnit, String> {
    //List<ShopUnit> findByParentId(String parentId);
    List<ShopUnit> findByTypeEqualsAndDateIsBetween(ShopUnitType shopUnitType, Date date1, Date date2);
    List<ShopUnit> findByParentIdEquals(String parentId);
    //ShopUnit findByIdAndDateGreaterThanEqualAndDateLessThan(String id, Date date1, Date date2);
}
