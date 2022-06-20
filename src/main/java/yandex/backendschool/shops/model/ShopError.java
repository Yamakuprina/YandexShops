package yandex.backendschool.shops.model;

import org.springframework.http.HttpStatus;

public class ShopError {
    public final Integer code;
    public final String message;

    public ShopError(HttpStatus httpStatus, String message) {
        this.code = httpStatus.value();
        this.message = message;
    }
}
