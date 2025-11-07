package seatsio.reports.usage;

import static org.apache.commons.lang3.StringUtils.leftPad;

public record Month(int year, int month) {

    public String serialize() {
        return year + "-" + leftPad(Integer.toString(month), 2, "0");
    }
}
