package seatsio.util;

import kong.unirest.UnirestException;

public interface UnirestExceptionThrowingSupplier<T> {

    T get() throws UnirestException;
}
