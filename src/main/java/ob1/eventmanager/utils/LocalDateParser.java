package ob1.eventmanager.utils;

import ob1.eventmanager.exception.IncorrectDateFormatException;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LocalDateParser {

    private final Pattern pattern = Pattern.compile("((?<d>[0-9]{1,2})[-/.](?<m>[0-9]{1,2})([-/.](?<y>[0-9]{2,4}))? ((?<hrs>[0-9]{1,2}):(?<mnt>[0-9]{1,2})))");

    private final Pattern dayPattern = Pattern.compile("((\\s|^)(?<d>[0-9]{1,2})(\\s|$))");
    private final Pattern monthPattern = Pattern.compile("(?<month>([Яя]нвар[яь])|([Фф]еврал[яь])|([Мм]арт[а]?)|([Аа]прел[яь])|([Мм]а[яй])|([Ии]юн[яь])|([Ии]юл[яь])|([Аа]вгуст[а]?)|([Сс]ентябр[яь])|([Оо]ктябр[яь])|([Нн]оябр[яь])|([Дд]екабр[яь]))");
    private final Pattern yearPattern = Pattern.compile("((\\s|^)(?<y>[0-9]{4})(\\s|$))");

    private final Pattern datePattern = Pattern.compile("(?<d>[0-9]{1,2})[-/.](?<m>[0-9]{1,2})([-/.](?<y>[0-9]{2,4}))?");
    private final Pattern timePattern = Pattern.compile("((?<hrs>[0-9]{1,2})[: ./-](?<mnt>[0-9]{1,2}))");

    private final Map<String, Month> monthByName = new HashMap<>();

    public LocalDateParser() {
        monthByName.put("нвар", Month.JANUARY);
        monthByName.put("ервал", Month.FEBRUARY);
        monthByName.put("ар", Month.MARCH);
        monthByName.put("прел", Month.APRIL);
        monthByName.put("а", Month.MAY);
        monthByName.put("юн", Month.JUNE);
        monthByName.put("юл", Month.JULY);
        monthByName.put("вгус", Month.AUGUST);
        monthByName.put("ентябр", Month.SEPTEMBER);
        monthByName.put("ктябр", Month.NOVEMBER);
        monthByName.put("оябр", Month.OCTOBER);
        monthByName.put("екабр", Month.DECEMBER);
    }

    public LocalDateTime parseDate(String str) {
        final Matcher dateMatcher = datePattern.matcher(str);
        if (dateMatcher.find()) {

            return null;
        }

        LocalDateTime date = LocalDateTime.now();

        final Matcher yearMatcher = yearPattern.matcher(str);
        if (yearMatcher.find()) {
            final String yearStr = yearMatcher.group("y");
            try {
                final int year = Integer.parseInt(yearStr);
                date = date.withYear(year);
            } catch (NumberFormatException | DateTimeException e) {
                throw new IncorrectDateFormatException(str, e);
            }
        }

        final Matcher monthMatcher = monthPattern.matcher(str);
        if (!monthMatcher.find()) {
            throw new IncorrectDateFormatException(str);
        }
        String monthStr = monthMatcher.group("month");
        monthStr = monthStr.substring(1, monthStr.length() - 1);
        final Month month = monthByName.get(monthStr);
        if (month == null) {
            throw new UnsupportedOperationException(str);
        }
        date = date.withMonth(month.getValue());

        final Matcher dayMatcher = dayPattern.matcher(str);
        if (!dayMatcher.find()) {
            throw new IncorrectDateFormatException(str);
        }
        final String dayStr = dayMatcher.group("d");
        try {
            final int day = Integer.parseInt(dayStr);
            date = date.withDayOfMonth(day);
        } catch (NumberFormatException | DateTimeException e) {
            throw new IncorrectDateFormatException(str, e);
        }

        return date;
    }

    public LocalDateTime parseTime(String str) {
        final Matcher timeMatcher = timePattern.matcher(str);
        if (!timeMatcher.find()) {
            throw new IncorrectDateFormatException(str);
        }

        final String hrs = timeMatcher.group("hrs");
        final String mnt = timeMatcher.group("mnt");

        final LocalDateTime time = LocalDateTime.now();

        try {
            return time.withHour(Integer.parseInt(hrs))
                    .withMinute(Integer.parseInt(mnt))
                    .withSecond(0);
        }
        catch (DateTimeException e) {
            throw new IncorrectDateFormatException(str, e);
        }
    }

}
