package com.github.doodler.amber.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import com.github.doodler.common.utils.MapUtils;

/**
 * @Description: RequestParameterUtils
 * @Author: Fred Feng
 * @Date: 21/12/2023
 * @Version 1.0.0
 */
@Component
public class RequestParameterUtils {

    private static final DateTimeFormatter defaultDtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getQueryString(HttpServletRequest request) {
        String queryStr = request.getQueryString();
        Map<String, String> kwargs = MapUtils.splitAsMap(queryStr, "&", "=");
        kwargs.remove("pageNumber");
        return MapUtils.toString(kwargs, "&", "=");
    }

    public static String formatDateWithTimeZone(Date date, String timeZone) {
        if (date == null) {
            return "-";
        }
        return date.toInstant().atZone(ZoneId.of(timeZone)).format(defaultDtf);
    }

    public static String formatDateWithTimeZone(LocalDateTime date, String timeZone) {
        if (date == null) {
            return "-";
        }
        return date.atZone(ZoneId.of(timeZone)).format(defaultDtf);
    }
}