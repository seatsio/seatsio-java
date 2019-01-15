package seatsio.subaccounts;

import java.util.HashMap;
import java.util.Map;

public class SubaccountListParams {

    private String filter;

    public SubaccountListParams withFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public Map<String, Object> asMap() {
        Map<String, Object> subaccountListParams = new HashMap<>();

        if (filter != null) {
            subaccountListParams.put("filter", filter);
        }

        return subaccountListParams;
    }

}
