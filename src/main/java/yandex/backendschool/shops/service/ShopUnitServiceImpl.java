package yandex.backendschool.shops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import yandex.backendschool.shops.model.*;
import yandex.backendschool.shops.repository.ShopUnitRepository;
import yandex.backendschool.shops.repository.ShopUnitStatisticUnitRepository;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ShopUnitServiceImpl implements ShopUnitService {

    private final ShopUnitRepository shopUnitRepository;
    private final ShopUnitStatisticUnitRepository shopUnitStatisticUnitRepository;

    public ShopUnitServiceImpl(@Autowired ShopUnitRepository shopUnitRepository, @Autowired ShopUnitStatisticUnitRepository statisticUnitRepository) {
        this.shopUnitRepository = shopUnitRepository;
        this.shopUnitStatisticUnitRepository = statisticUnitRepository;
    }

    @Override
    public void importShopUnits(ShopUnitImportRequest importRequest) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = sdf.parse(importRequest.getUpdateDate());
        List<ShopUnit> units = Converter.importRequestToShopUnits(importRequest, date);
        checkConsistency(units);
        shopUnitRepository.saveAll(units);
        updateCategoriesDates(units, date);
        updateCategoriesPrices(units);
    }

    @Override
    @Transactional
    public void deleteUnitById(String id) throws Exception {
        Optional<ShopUnit> deleteCandidate = shopUnitRepository.findById(id);
        if (deleteCandidate.isEmpty()) throw new HttpServerErrorException(HttpStatus.NOT_FOUND, "Item not found");
        String parentOfDeletedId = deleteCandidate.get().getParentId();
        Optional<ShopUnit> parentOfDeleted = parentOfDeletedId == null ? Optional.empty() : shopUnitRepository.findById(parentOfDeletedId);
        shopUnitRepository.deleteById(id);
        deleteCategoryStatistics(deleteCandidate.get());
        if (parentOfDeleted.isEmpty()) return;
        updateCategoriesPrices(List.of(parentOfDeleted.get()));
    }

    private void deleteCategoryStatistics(ShopUnit shopUnit){
        for (ShopUnit child : shopUnit.getChildren()){
            if (child.getType().equals(ShopUnitType.CATEGORY)) deleteCategoryStatistics(child);
            shopUnitStatisticUnitRepository.deleteByUnitIdEquals(child.getId());
        }
        shopUnitStatisticUnitRepository.deleteByUnitIdEquals(shopUnit.getId());
    }

    @Override
    public ShopUnit getUnitById(String id) throws Exception{
        Optional<ShopUnit> shopUnit = shopUnitRepository.findById(id);
        if (shopUnit.isEmpty()) throw new HttpServerErrorException(HttpStatus.NOT_FOUND, "Item not found");
        ShopUnit shopUnit1 = shopUnit.get();
        placeNullInOfferChildren(shopUnit1);
        return shopUnit1;
    }

    @Override
    public ShopUnitStatisticResponse get24hSales(String stringDate) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = sdf.parse(stringDate);
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).minusDays(1);
        Date date24hBefore = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        List<ShopUnitStatisticUnit> sales = shopUnitStatisticUnitRepository.findByTypeEqualsAndDateIsBetween(ShopUnitType.OFFER, date24hBefore, date);
        return new ShopUnitStatisticResponse(sales);
    }

    @Override
    public ShopUnitStatisticResponse getNodeStatisticHistoryBetweenDates(String id, String dateStart, String dateEnd) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date startDate = sdf.parse(dateStart);
        Date endDate = sdf.parse(dateEnd);
        if (shopUnitRepository.findById(id).isEmpty())
            throw new HttpServerErrorException(HttpStatus.NOT_FOUND, "Item not found");
        List<ShopUnitStatisticUnit> units = shopUnitStatisticUnitRepository.findAllByDateGreaterThanEqualAndDateLessThanAndUnitIdEquals(startDate, endDate, id);
        return new ShopUnitStatisticResponse(units);
    }

    private void placeNullInOfferChildren(ShopUnit shopUnit) {
        if (shopUnit.getType().equals(ShopUnitType.OFFER)) {
            shopUnit.setChildren(null);
            return;
        }
        for (ShopUnit shopUnit1 : shopUnit.getChildren()) {
            placeNullInOfferChildren(shopUnit1);
        }
    }

    private void checkConsistency(List<ShopUnit> newUnits) throws Exception {
        for (ShopUnit unit : newUnits) {
            if (unit.getType()==null) throw new Exception("Validation Failed");
            if (unit.getType().equals(ShopUnitType.CATEGORY) && unit.getPrice() != null)
                throw new Exception("Validation Failed");
            if (unit.getType().equals(ShopUnitType.OFFER) && (unit.getPrice() == null || unit.getPrice() < 0))
                throw new Exception("Validation Failed");
            if (unit.getParentId() == null) continue;
            Optional<ShopUnit> unitParent = newUnits.stream().filter(shopUnit -> Objects.equals(unit.getParentId(), shopUnit.getId())).findFirst();
            if (unitParent.isEmpty()) unitParent = shopUnitRepository.findById(unit.getParentId());
            if (unitParent.isPresent() && unitParent.get().getType().equals(ShopUnitType.OFFER))
                throw new Exception("Validation Failed");
            if (unitParent.isPresent() && Objects.equals(unitParent.get().getParentId(), unit.getId()))
                throw new Exception("Validation Failed");
        }
    }

    private void updateCategoriesDates(List<ShopUnit> changedUnits, Date date) {
        for (ShopUnit shopUnit : changedUnits) {
            Optional<ShopUnit> root = Optional.ofNullable(shopUnit);
            while (root.get().getParentId() != null) {
                String rootCandidateId = root.get().getParentId();
                Optional<ShopUnit> rootCandidate = changedUnits.stream().filter(shopUnit1 -> shopUnit1.getId().equals(rootCandidateId)).findFirst();
                if (rootCandidate.isEmpty()) rootCandidate = shopUnitRepository.findById(root.get().getParentId());
                if (rootCandidate.isEmpty()) break;
                else root = rootCandidate;
                root.get().setDate(date);
                shopUnitRepository.save(root.get());
                shopUnitStatisticUnitRepository.save(new ShopUnitStatisticUnit(root.get()));
            }
            root.get().setDate(date);
            shopUnitRepository.save(root.get());
            shopUnitStatisticUnitRepository.save(new ShopUnitStatisticUnit(root.get()));
        }
    }

    private void updateCategoriesPrices(List<ShopUnit> changedNodes) {
        for (ShopUnit shopUnit : changedNodes) {
            Optional<ShopUnit> root = Optional.ofNullable(shopUnit);
            shopUnitStatisticUnitRepository.save(new ShopUnitStatisticUnit(root.get()));
            while (root.get().getParentId() != null) {
                String rootCandidateId = root.get().getParentId();
                Optional<ShopUnit> rootCandidate = changedNodes.stream().filter(shopUnit1 -> shopUnit1.getId().equals(rootCandidateId)).findFirst();
                if (rootCandidate.isEmpty()) rootCandidate = shopUnitRepository.findById(root.get().getParentId());
                if (rootCandidate.isEmpty()) break;
                else root = rootCandidate;
            }
            if (root.get().getType().equals(ShopUnitType.OFFER)) continue;
            calculateCategoryPrice(root.get());
            shopUnitRepository.save(root.get());
            shopUnitStatisticUnitRepository.save(new ShopUnitStatisticUnit(root.get()));
        }
    }


    private List<Integer> calculateCategoryPrice(ShopUnit shopUnitCategory) {
        shopUnitCategory.setChildren(shopUnitRepository.findByParentIdEquals(shopUnitCategory.getId()));
        if (shopUnitCategory.getChildren().size() == 0) {
            shopUnitCategory.setPrice(null);
            return List.of(0, 0);
        }
        float sum = 0;
        int offer_count = 0;
        for (ShopUnit shopUnit : shopUnitCategory.getChildren()) {
            if (shopUnit.getType().equals(ShopUnitType.OFFER)) {
                sum += shopUnit.getPrice();
                offer_count += 1;
            } else {
                List<Integer> subcategoryRes = calculateCategoryPrice(shopUnit);
                sum += subcategoryRes.get(0);
                offer_count += subcategoryRes.get(1);
            }
        }
        int res = (int) Math.floor(sum / offer_count);
        if (res == 0 && offer_count == 0) shopUnitCategory.setPrice(null);
        else shopUnitCategory.setPrice(res);
        shopUnitStatisticUnitRepository.save(new ShopUnitStatisticUnit(shopUnitCategory));
        return List.of((int) sum, offer_count);
    }
}
