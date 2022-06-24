package yandex.backendschool.shops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yandex.backendschool.shops.model.ShopUnit;

import java.util.List;

@Repository
public interface ShopUnitRepository extends JpaRepository<ShopUnit, String> {
    List<ShopUnit> findByParentIdEquals(String parentId);
}
