package api;

import java.io.Serial;

public class OrderNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public OrderNotFoundException(Long id) {
        super("Could not find order with id " + id);
    }
}