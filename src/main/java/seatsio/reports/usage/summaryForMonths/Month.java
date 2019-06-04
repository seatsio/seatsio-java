package seatsio.reports.usage.summaryForMonths;

import static org.apache.commons.lang3.StringUtils.leftPad;

public class Month {

    public int month;
    public int year;

    public Month(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public String serialize() {
        return year + "-" + leftPad(Integer.toString(month), 2, "0");
    }
}
