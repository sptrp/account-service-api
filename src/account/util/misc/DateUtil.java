package account.util.misc;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;

public class DateUtil {
    public static YearMonth stringToYearMonth(String str) {
        String[] split = str.split("-");
        String formatted = String.format("%s-%s", split[1], split[0]);
        return YearMonth.parse(formatted);
    }

    public static String yearMonthToString(YearMonth period) {
        String perStr = period.toString();
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                // month-year
                .appendPattern("yyyy-MM")
                // default value for day
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                // create formatter
                .toFormatter();

        LocalDate localDate = LocalDate.parse(perStr, formatter);
        String[] split = localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)).split(" ");

        return String.format("%s-%s", split[0], split[2]);
    }
}
