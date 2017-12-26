package seatsio;

import com.mashape.unirest.http.exceptions.UnirestException;

public interface UnirestExceptionThrowingSupplier<T> {

    public T get() throws UnirestException;
}
