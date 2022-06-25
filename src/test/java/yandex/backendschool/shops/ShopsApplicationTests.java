package yandex.backendschool.shops;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import yandex.backendschool.shops.controller.Controller;
import yandex.backendschool.shops.model.ShopUnit;
import yandex.backendschool.shops.model.ShopUnitImport;
import yandex.backendschool.shops.model.ShopUnitImportRequest;
import yandex.backendschool.shops.model.ShopUnitType;
import yandex.backendschool.shops.repository.ShopUnitRepository;
import yandex.backendschool.shops.repository.ShopUnitStatisticUnitRepository;
import yandex.backendschool.shops.service.ShopUnitServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        when(shopUnitRepository.findById("3fa85f64-5717-4562-b3fc-2c963f66a444")).thenReturn(Optional.of(shopUnit));
        Assertions.assertEquals(HttpStatus.OK, controller.getUnit(id).getStatusCode());
    }

    @Test
    void getAbsentNode(){
        String id = "3fa85f64-5717-4562-b3fc-2c963f66a111";
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.getUnit(id).getStatusCode());
    }

    @Test
    void deleteAbsentNode(){
        String id = "3fa85f64-5717-4562-b3fc-2c963f66a111";
        Assertions.assertEquals(HttpStatus.NOT_FOUND, controller.deleteUnit(id).getStatusCode());
    }

}
