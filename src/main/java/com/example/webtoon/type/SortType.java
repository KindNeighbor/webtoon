package com.example.webtoon.type;

import com.example.webtoon.exception.CustomException;
import java.util.Arrays;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SortType {

    NEW("updated_at", "desc"),
    RATE("avg_rate", "desc"),
    VIEW("view_count", "desc");

    private final String column;
    private final String order;

    public static Sort getSort(SortType sortType) {

        Sort sort;
        if (sortType.getOrder().equals("desc")) {
            sort = Sort.by(sortType.getColumn()).descending();
        } else {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ORDER_TYPE_NOT_FOUND);
        }

        return sort;
    }
}
