package com.hello.shopapi.controller.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Locale;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        // yyyy-MM-dd HH:mm:ss
        return null;
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return "";
    }
}
