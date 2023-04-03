package src;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final String CUSTOM_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public String marshal(LocalDateTime v) {
        return v.format(ISO_FORMATTER);
    }

    @Override
    public LocalDateTime unmarshal(String v) throws ParseException {
        return LocalDateTime.parse(v);
    }

}