package seatsio.events;

import seatsio.ApiError;
import seatsio.SeatsioException;

import java.util.List;

public class BestAvailableObjectsNotFoundException extends SeatsioException {

    public BestAvailableObjectsNotFoundException(List<ApiError> errors, String requestId) {
        super(errors, requestId);
    }
}
