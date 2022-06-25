package yandex.backendschool.shops.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import yandex.backendschool.shops.model.ShopError;
import yandex.backendschool.shops.model.ShopUnitImportRequest;
import yandex.backendschool.shops.service.ShopUnitService;
import yandex.backendschool.shops.service.ShopUnitServiceImpl;

@RestController
@RequestMapping("/")
public class Controller {

    private final ShopUnitService shopUnitService;

    public Controller(@Autowired ShopUnitServiceImpl shopUnitImportService) {
        this.shopUnitService = shopUnitImportService;
    }

    @PostMapping("imports")
    public ResponseEntity<?> importUnits(@RequestBody ShopUnitImportRequest importRequest) {
        try {
            shopUnitService.importShopUnits(importRequest);
            return ResponseEntity.status(200).body("Import Success");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ShopError(HttpStatus.BAD_REQUEST, "Validation Failed"));
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteUnit(@PathVariable(value = "id") String id) {
        try {
            shopUnitService.deleteUnitById(id);
            return ResponseEntity.status(200).body("Delete Success");
        } catch (HttpStatusCodeException f) {
            return ResponseEntity.status(404).body(new ShopError(HttpStatus.NOT_FOUND, "Item not found"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ShopError(HttpStatus.BAD_REQUEST, "Validation Failed"));
        }
    }

    @GetMapping("nodes/{id}")
    public ResponseEntity<?> getUnit(@PathVariable(value = "id") String id) {
        try {
            return ResponseEntity.status(200).body(shopUnitService.getUnitById(id));
        } catch (HttpStatusCodeException f) {
            return ResponseEntity.status(404).body(new ShopError(HttpStatus.NOT_FOUND, "Item not found"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ShopError(HttpStatus.BAD_REQUEST, "Validation Failed"));
        }
    }

    @GetMapping("sales")
    public ResponseEntity<?> getSales(@RequestParam String date) {
        try {
            return ResponseEntity.status(200).body(shopUnitService.get24hSales(date));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ShopError(HttpStatus.BAD_REQUEST, "Validation Failed"));
        }
    }

    @GetMapping("node/{id}/statistic")
    public ResponseEntity<?> getNodeStatistic(@PathVariable(value = "id") String id, @RequestParam String dateStart, @RequestParam String dateEnd) {
        try {
            return ResponseEntity.status(200).body(shopUnitService.getNodeStatisticHistoryBetweenDates(id, dateStart, dateEnd));
        } catch (HttpStatusCodeException f) {
            return ResponseEntity.status(404).body(new ShopError(HttpStatus.NOT_FOUND, "Item not found"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ShopError(HttpStatus.BAD_REQUEST, "Validation Failed"));
        }
    }
}
