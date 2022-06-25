package yandex.backendschool.shops;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import yandex.backendschool.shops.repository.ShopUnitRepository;
import yandex.backendschool.shops.repository.ShopUnitStatisticUnitRepository;
import yandex.backendschool.shops.service.ShopUnitServiceImpl;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class ShopsApplicationTests {

    @Mock
    private ShopUnitRepository shopUnitRepository;

    @Mock
    private ShopUnitStatisticUnitRepository shopUnitStatisticUnitRepository;

    @InjectMocks
    private ShopUnitServiceImpl service;

    @BeforeEach
    public void beforeMethod() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void contextLoads() {
    }

}
