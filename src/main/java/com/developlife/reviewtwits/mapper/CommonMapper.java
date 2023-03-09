package com.developlife.reviewtwits.mapper;

/**
 * @author ghdic
 * @since 2023/03/08
 */

import com.developlife.reviewtwits.exception.common.DateParseException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface CommonMapper {
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @Named("toDate")
    default Date toDate(String date) {
        try {
            return dateFormatter.parse(date);
        } catch (ParseException e) {
            throw new DateParseException("날짜 형식이 올바르지 않습니다.");
        }
    }
}
