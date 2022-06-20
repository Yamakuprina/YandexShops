package yandex.backendschool.shops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import yandex.backendschool.shops.model.*;
import yandex.backendschool.shops.repository.ShopUnitRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class ShopUnitImportService {

    private final ShopUnitRepository shopUnitRepository;

    public ShopUnitImportService(@Autowired ShopUnitRepository shopUnitRepository) {
        this.shopUnitRepository = shopUnitRepository;
    }

    public void importShopUnits(ShopUnitImportRequest importRequest) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = sdf.parse(importRequest.getUpdateDate());
        //DateISO8601 date = new DateISO8601(importRequest.getUpdateDate());
        List<ShopUnit> units = Converter.requestToShopUnits(importRequest, date);
//        for (ShopUnit unit: units){
//            if (unit.getType().equals(ShopUnitType.CATEGORY) && unit.getPrice()!=null) throw new Exception("Validation Failed");
//            if (unit.getType().equals(ShopUnitType.OFFER) && (unit.getPrice()==null || unit.getPrice()<0)) throw new Exception("Validation Failed");
//        }
        checkConsistency(units);
        //for (ShopUnit unit:units) shopUnitRepository.save(unit);

        shopUnitRepository.saveAll(units);

//        for (ShopUnit unit: units) {
//            if (unit.getType().equals(ShopUnitType.CATEGORY)) {
//                unit.setChildren(shopUnitRepository.findByParentIdEquals(unit.getId()));
//            }
//        }
        updateCategoriesPrices(units);
        updateCategoriesDates(units, date);
    }

    public void deleteUnitById(String id) throws HttpStatusCodeException {
        Optional<ShopUnit> deleteCandidate = shopUnitRepository.findById(id);
        if (deleteCandidate.isEmpty()) throw new HttpServerErrorException(HttpStatus.NOT_FOUND ,"Item not found");
        String parentOfDeletedId = deleteCandidate.get().getParentId();
        Optional<ShopUnit> parentOfDeleted = parentOfDeletedId==null? Optional.empty() : shopUnitRepository.findById(parentOfDeletedId);
        shopUnitRepository.deleteById(id);
        //shopUnitRepository.flush();
        if (parentOfDeleted.isEmpty()) return;
        updateCategoriesPrices(List.of(parentOfDeleted.get()));
    }

    public ShopUnit getUnitById(String id) throws HttpStatusCodeException{
        Optional<ShopUnit> shopUnit = shopUnitRepository.findById(id);
        if (shopUnit.isEmpty()) throw new HttpServerErrorException(HttpStatus.NOT_FOUND ,"Item not found");
        ShopUnit shopUnit1 = shopUnit.get();
        placeNullInOfferChildren(shopUnit1);
        return shopUnit1;
    }

    public ShopUnitStatisticResponse get24hSales(String stringDate) throws ParseException {
        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        //LocalDate date = LocalDate.parse(stringDate, dtf);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = sdf.parse(stringDate);
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        //LocalDate date = sdf.parse(stringDate);
        //LocalDate ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).minusDays(1);
        Date date24hBefore = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        List<ShopUnit> sales = shopUnitRepository.findByTypeEqualsAndDateIsBetween(ShopUnitType.OFFER, date24hBefore, date);
        //ShopUnitStatisticResponse response = Converter.shopUnitsToStatisticResponse(sales);
        return Converter.shopUnitsToStatisticResponse(sales);
    }

//    public ShopUnitStatisticResponse getNodeStatisticBetweenDates(String id, String dateStart, String dateEnd) throws Exception {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//        Date startDate = sdf.parse(dateStart);
//        Date endDate = sdf.parse(dateEnd);
//        if (shopUnitRepository.findById(id).isEmpty()) throw new HttpServerErrorException(HttpStatus.NOT_FOUND ,"Item not found");
//    }

    private void placeNullInOfferChildren(ShopUnit shopUnit){
        if (shopUnit.getType().equals(ShopUnitType.OFFER)){
            shopUnit.setChildren(null);
            return;
        }
        for (ShopUnit shopUnit1 : shopUnit.getChildren()){
            placeNullInOfferChildren(shopUnit1);
        }
    }

    private void checkConsistency(List<ShopUnit> newUnits) throws Exception {
//        List<ShopUnit> allUnits = shopUnitRepository.findAll();
//        allUnits.addAll(newUnits);
        for (ShopUnit unit:newUnits){
            if (unit.getType().equals(ShopUnitType.CATEGORY) && unit.getPrice()!=null) throw new Exception("Validation Failed");
            if (unit.getType().equals(ShopUnitType.OFFER) && (unit.getPrice()==null || unit.getPrice()<0)) throw new Exception("Validation Failed");
            //List<ShopUnit> children = new ArrayList<>();
            if (unit.getParentId()==null) continue;
            Optional<ShopUnit> unitParent = newUnits.stream().filter(shopUnit -> Objects.equals(unit.getParentId(), shopUnit.getId())).findFirst();
            // shopUnitRepository.findById(unit.getParentId());
            if (unitParent.isEmpty()) unitParent=shopUnitRepository.findById(unit.getParentId());
            if (unitParent.isPresent() && unitParent.get().getType().equals(ShopUnitType.OFFER)) throw new Exception("Validation Failed");
//            for (ShopUnit unit2 : allUnits)
//                if (Objects.equals(unit2.getParentId(), unit1.getId())){
//                    if (unit1.getType().equals(ShopUnitType.OFFER)) throw new Exception("Validation Failed");
//                    //children.add(unit2);
//            }
//            if (unit1.getType().equals(ShopUnitType.CATEGORY)){
//                unit1.setChildren(children);
//            }
        }
//        for (ShopUnit shopUnit : allUnits) if (shopUnit.getType().equals(ShopUnitType.CATEGORY)) calculateCategoryPrice(shopUnit);
//        shopUnitRepository.saveAll(allUnits);
    }

    public void updateCategoriesDates(List<ShopUnit> changedUnits, Date date){
        for (ShopUnit shopUnit: changedUnits){
            Optional<ShopUnit> root = Optional.ofNullable(shopUnit);
            while (root.get().getParentId()!=null){
                String rootCandidateId = root.get().getParentId();
                Optional<ShopUnit> rootCandidate = changedUnits.stream().filter(shopUnit1 -> shopUnit1.getId().equals(rootCandidateId)).findFirst();
                if (rootCandidate.isEmpty())rootCandidate = shopUnitRepository.findById(root.get().getParentId());
                if (rootCandidate.isEmpty()) break; else root=rootCandidate;
                root.get().setDate(date);
                shopUnitRepository.save(root.get());
            }
            root.get().setDate(date);
            shopUnitRepository.save(root.get());
        }
    }

    public void updateCategoriesPrices(List<ShopUnit> changedNodes){
        for (ShopUnit shopUnit: changedNodes){
            Optional<ShopUnit> root = Optional.ofNullable(shopUnit);
            while (root.get().getParentId()!=null){
                String rootCandidateId = root.get().getParentId();
                Optional<ShopUnit> rootCandidate = changedNodes.stream().filter(shopUnit1 -> shopUnit1.getId().equals(rootCandidateId)).findFirst();
                if (rootCandidate.isEmpty()) rootCandidate = shopUnitRepository.findById(root.get().getParentId());
                if (rootCandidate.isEmpty()) break; else root=rootCandidate;
            }
            if (root.get().getType().equals(ShopUnitType.OFFER)) continue;
            calculateCategoryPrice(root.get());
            shopUnitRepository.save(root.get());
        }
//        List<ShopUnit> allUnits = shopUnitRepository.findAll();
//        for (ShopUnit shopUnit : allUnits) if (shopUnit.getType().equals(ShopUnitType.CATEGORY)) calculateCategoryPrice(shopUnit);
//        shopUnitRepository.saveAll(allUnits);
    }


    private List<Integer> calculateCategoryPrice(ShopUnit shopUnitCategory){
        //if (shopUnitCategory.getType().equals(ShopUnitType.OFFER)) return List.of(0,0);
        shopUnitCategory.setChildren(shopUnitRepository.findByParentIdEquals(shopUnitCategory.getId()));
        if (shopUnitCategory.getChildren().size()==0){
            shopUnitCategory.setPrice(null);
            return List.of(0,0);
        }
        float sum = 0;
        int offer_count=0;
        for (ShopUnit shopUnit : shopUnitCategory.getChildren()){
            if (shopUnit.getType().equals(ShopUnitType.OFFER)){
                sum += shopUnit.getPrice();
                offer_count+=1;
            } else {
                List<Integer> subcategoryRes=calculateCategoryPrice(shopUnit);
                sum+=subcategoryRes.get(0);
                offer_count+=subcategoryRes.get(1);
                //if (shopUnit.getPrice()==null) count_null_subcategories+=1; else sum+=shopUnit.getPrice();
            }
        }
        int res = (int) Math. floor (sum / offer_count);
        if (res==0 && offer_count==0) shopUnitCategory.setPrice(null); else shopUnitCategory.setPrice(res);
        return List.of((int)sum, offer_count);
    }
}
