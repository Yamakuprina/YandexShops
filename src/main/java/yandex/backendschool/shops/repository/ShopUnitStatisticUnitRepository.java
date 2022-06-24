package yandex.backendschool.shops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yandex.backendschool.shops.model.ShopUnitStatisticUnit;
import yandex.backendschool.shops.model.ShopUnitType;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopUnitStatisticUnitRepository extends JpaRepository<ShopUnitStatisticUnit, String> {
    List<ShopUnitStatisticUnit> findAllByDateGreaterThanEqualAndDateLessThanAndUnitIdEquals(Date dateStart, Date dateEnd, String id);

    List<ShopUnitStatisticUnit> findByTypeEqualsAndDateIsBetween(ShopUnitType shopUnitType, Date date1, Date date2);

    void deleteByUnitIdEquals(String id);

    Optional<ShopUnitStatisticUnit> findByUnitIdEquals(String id);
}
