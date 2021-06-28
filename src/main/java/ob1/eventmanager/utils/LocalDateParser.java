package ob1.eventmanager.utils;

import ob1.eventmanager.exception.IncorrectDateFormatException;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LocalDateParser {

    private final Pattern pattern = Pattern.compile("((?<d>[0-9]{1,2})[-/.](?<m>[0-9]{1,2})([-/.](?<y>[0-9]{2,4}))? ((?<hrs>[0-9]{1,2}):(?<mnt>[0-9]{1,2})))");

    public LocalDateTime parse(String str) {
        final Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            throw new IncorrectDateFormatException(str);
        }

        LocalDateTime date = LocalDateTime.now();

        final String d = matcher.group("d");
        final String m = matcher.group("m");
        final String y = matcher.group("y");

        final String hrs = matcher.group("hrs");
        final String mnt = matcher.group("mnt");

        try {
            if (y != null) date = date.with(ChronoField.YEAR, Integer.parseInt(y));
            return date.withMonth(Integer.parseInt(m))
                    .withDayOfMonth(Integer.parseInt(d))
                    .withHour(Integer.parseInt(hrs))
                    .withSecond(Integer.parseInt(mnt));
        }
        catch (DateTimeException e) {
            throw new IncorrectDateFormatException(str, e);
        }
    }

}
