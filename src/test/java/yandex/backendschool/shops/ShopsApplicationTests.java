package yandex.backendschool.shops;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import yandex.backendschool.shops.controller.Controller;
import yandex.backendschool.shops.model.*;
import yandex.backendschool.shops.repository.ShopUnitRepository;
import yandex.backendschool.shops.repository.ShopUnitStatisticUnitRepository;
import yandex.backendschool.shops.service.ShopUnitServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class ShopsApplicationTests {

    @Mock
    private ShopUnitRepository shopUnitRepository;

    @Mock
    private ShopUnitStatisticUnitRepository shopUnitStatisticUnitRepository;

    @InjectMocks
    private ShopUnitServiceImpl service;

    private Controller controller;
    private SimpleDateFormat sdf;

    @BeforeEach
    public void beforeMethod() {
        controller = new Controller(service);
        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    @Test
    void importTest() throws Exception {
        List<ShopUnitImport> imports = List.of(
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a444", "Оффер", "3fa85f64-5717-4562-b3fc-2c963f66a333", 5, ShopUnitType.OFFER),
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a333", "Категория", null, null, ShopUnitType.CATEGORY)
        );
        String updateDate = "2022-04-28T21:12:01.000Z";
        ShopUnitImportRequest request = new ShopUnitImportRequest(imports, updateDate);
        Assertions.assertEquals(HttpStatus.OK, controller.importUnits(request).getStatusCode());
    }

    @Test
    void importWrongDate(){
        List<ShopUnitImport> imports = List.of(
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a444", "Оффер", "3fa85f64-5717-4562-b3fc-2c963f66a333", 5, ShopUnitType.OFFER),
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a333", "Категория", null, null, ShopUnitType.CATEGORY)
        );
        String updateDate = "2022-04-28";
        ShopUnitImportRequest request = new ShopUnitImportRequest(imports, updateDate);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.importUnits(request).getStatusCode());
    }

    @Test
    void importNotNullCategoryPrice(){
        List<ShopUnitImport> imports = List.of(
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a444", "Оффер", "3fa85f64-5717-4562-b3fc-2c963f66a333", 5, ShopUnitType.OFFER),
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a333", "Категория", null, 5, ShopUnitType.CATEGORY)
        );
        String updateDate = "2022-04-28T21:12:01.000Z";
        ShopUnitImportRequest request = new ShopUnitImportRequest(imports, updateDate);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.importUnits(request).getStatusCode());
    }

    @Test
    void importWrongOfferPrice(){
        List<ShopUnitImport> imports = List.of(
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a444", "Оффер", "3fa85f64-5717-4562-b3fc-2c963f66a333", -5, ShopUnitType.OFFER),
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a333", "Категория", null, null, ShopUnitType.CATEGORY)
        );
        String updateDate = "2022-04-28T21:12:01.000Z";
        ShopUnitImportRequest request = new ShopUnitImportRequest(imports, updateDate);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.importUnits(request).getStatusCode());
    }

    @Test
    void importNullOfferPrice(){
        List<ShopUnitImport> imports = List.of(
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a444", "Оффер", "3fa85f64-5717-4562-b3fc-2c963f66a333", null, ShopUnitType.OFFER),
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a333", "Категория", null, null, ShopUnitType.CATEGORY)
        );
        String updateDate = "2022-04-28T21:12:01.000Z";
        ShopUnitImportRequest request = new ShopUnitImportRequest(imports, updateDate);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.importUnits(request).getStatusCode());
    }

    @Test
    void importZeroOfferPrice(){
        List<ShopUnitImport> imports = List.of(
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a444", "Оффер", "3fa85f64-5717-4562-b3fc-2c963f66a333", 0, ShopUnitType.OFFER),
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a333", "Категория", null, null, ShopUnitType.CATEGORY)
        );
        String updateDate = "2022-04-28T21:12:01.000Z";
        ShopUnitImportRequest request = new ShopUnitImportRequest(imports, updateDate);
        Assertions.assertEquals(HttpStatus.OK, controller.importUnits(request).getStatusCode());
    }

    @Test
    void importWrongId(){
        List<ShopUnitImport> imports = List.of(
                new ShopUnitImport("3fa85f64", "Оффер", "3fa85f63", 5, ShopUnitType.OFFER),
                new ShopUnitImport("3fa85f63", "Категория", null, null, ShopUnitType.CATEGORY)
        );
        String updateDate = "2022-04-28T21:12:01.000Z";
        ShopUnitImportRequest request = new ShopUnitImportRequest(imports, updateDate);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.importUnits(request).getStatusCode());
    }

    @Test
    void importWrongParent(){
        List<ShopUnitImport> imports = List.of(
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a444", "Оффер", null, 5, ShopUnitType.OFFER),
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a333", "Категория", "3fa85f64-5717-4562-b3fc-2c963f66a444", null, ShopUnitType.CATEGORY)
        );
        String updateDate = "2022-04-28T21:12:01.000Z";
        ShopUnitImportRequest request = new ShopUnitImportRequest(imports, updateDate);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.importUnits(request).getStatusCode());
    }

    @Test
    void importCyclicDependency(){
        List<ShopUnitImport> imports = List.of(
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a555", "Подкатегория", "3fa85f64-5717-4562-b3fc-2c963f66a333", null, ShopUnitType.CATEGORY),
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a333", "Категория", "3fa85f64-5717-4562-b3fc-2c963f66a555", null, ShopUnitType.CATEGORY)
        );
        String updateDate = "2022-04-28T21:12:01.000Z";
        ShopUnitImportRequest request = new ShopUnitImportRequest(imports, updateDate);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.importUnits(request).getStatusCode());
    }

    @Test
    void importCyclicParentId(){
        List<ShopUnitImport> imports = List.of(
                new ShopUnitImport("3fa85f64-5717-4562-b3fc-2c963f66a333", "Категория", "3fa85f64-5717-4562-b3fc-2c963f66a333", null, ShopUnitType.CATEGORY)
        );
        String updateDate = "2022-04-28T21:12:01.000Z";
        ShopUnitImportRequest request = new ShopUnitImportRequest(imports, updateDate);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.importUnits(request).getStatusCode());
    }

    @Test
    void getNode() throws ParseException {
        Date date = sdf.parse("2022-04-28T21:12:01.000Z");
        ShopUnit shopUnit = new ShopUnit("3fa85f64-5717-4562-b3fc-2c963f66a444", "Оффер",date, "3fa85f64-5717-4562-b3fc-2c963f66a333", ShopUnitType.OFFER,5, null);
        String id = "3fa85f64-5717-4562-b3fc-2c963f66a444";
        when(shopUnitRepository.findById(id)).thenReturn(Optional.of(shopUnit));
        Assertions.assertEquals(HttpStatus.OK, controller.getUnit(id).getStatusCode());
    }

    @Test
    void getAbsentNode(){
        String id = "3fa85f64-5717-4562-b3fc-2c963f66a111";
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.getUnit(id).getStatusCode());
    }

    @Test
    void getNodeWrongId(){
        String id = "3fa85f64-5717-4562-b3fc-2c963";
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.getUnit(id).getStatusCode());
    }

    @Test
    void deleteAbsentNode(){
        String id = "3fa85f64-5717-4562-b3fc-2c963f66a111";
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.deleteUnit(id).getStatusCode());
    }

    @Test
    void deleteNodeWrongId(){
        String id = "3fa85f64-5717-4562-b3fc-2c963f66a1";
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.deleteUnit(id).getStatusCode());
    }

    @Test
    void getEmptySales(){
        String date = "2022-04-28T21:12:01.000Z";
        Assertions.assertEquals(HttpStatus.OK, controller.getSales(date).getStatusCode());
    }

    @Test
    void getSales() throws ParseException {
        String dateString = "2022-04-28T21:12:01.000Z";
        Date date = sdf.parse("2022-04-28T21:12:01.000Z");
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).minusDays(1);
        Date date24hBefore = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        ShopUnitStatisticUnit statisticUnit = new ShopUnitStatisticUnit("3fa85f64-5717-4562-b3fc-2c963f66a444", "Оффер",date,"3fa85f64-5717-4562-b3fc-2c963f66a333", ShopUnitType.OFFER,5 );
        when(shopUnitStatisticUnitRepository.findByTypeEqualsAndDateIsBetween(ShopUnitType.OFFER,date24hBefore,date)).thenReturn(List.of(statisticUnit));
        Assertions.assertEquals(HttpStatus.OK, controller.getSales(dateString).getStatusCode());
    }

    @Test
    void getSalesWrongDate(){
        String date = "2022-04-28T21:12";
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.getSales(date).getStatusCode());
    }

    @Test
    void getNodeStatistic() throws ParseException {
        String id = "3fa85f64-5717-4562-b3fc-2c963f66a444";
        String dateStartString = "2022-04-28T21:12:01.000Z";
        String dateEndString = "2022-05-28T21:12:01.000Z";
        Date dateStart = sdf.parse(dateStartString);
        Date dateEnd = sdf.parse(dateEndString);
        ShopUnitStatisticUnit statisticUnit = new ShopUnitStatisticUnit("3fa85f64-5717-4562-b3fc-2c963f66a444", "Оффер",dateStart,"3fa85f64-5717-4562-b3fc-2c963f66a333", ShopUnitType.OFFER,5 );
        when(shopUnitStatisticUnitRepository.findAllByDateGreaterThanEqualAndDateLessThanAndUnitIdEquals(dateStart,dateEnd,id)).thenReturn(List.of(statisticUnit));
        when(shopUnitStatisticUnitRepository.countAllByUnitIdEquals(id)).thenReturn(1);
        Assertions.assertEquals(HttpStatus.OK, controller.getNodeStatistic(id, dateStartString, dateEndString).getStatusCode());
    }

    @Test
    void getStatisticWrongId(){
        String id = "3fa85f64-5717-4562-b3fc-2c963f66a1";
        String dateStart = "2022-04-28T21:12:01.000Z";
        String dateEnd = "2022-05-28T21:12:01.000Z";
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.getNodeStatistic(id, dateStart, dateEnd).getStatusCode());
    }

    @Test
    void getStatisticWrongDate(){
        String id = "3fa85f64-5717-4562-b3fc-2c963f66a444";
        String dateStart = "2022-04-28T21:12:01";
        String dateEnd = "2022-05-28T21:12:01";
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, controller.getNodeStatistic(id, dateStart, dateEnd).getStatusCode());
    }

    @Test
    void getAbsentNodeStatistic(){
        String id = "3fa85f64-5717-4562-b3fc-2c963f66a444";
        String dateStart = "2022-04-28T21:12:01.000Z";
        String dateEnd = "2022-05-28T21:12:01.000Z";
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.getNodeStatistic(id, dateStart, dateEnd).getStatusCode());
    }

}
