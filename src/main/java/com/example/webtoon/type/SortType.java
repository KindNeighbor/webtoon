package com.example.webtoon.type;

import com.example.webtoon.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SortType {

    NEW("new", "updated_at", "desc"),
    RATE("rate", "avg_rate", "desc"),
    VIEW("view","view_count", "desc");

    private final String type;
    private final String column;
    private final String order;

    public static Sort getSort(SortType order) {

        Sort sort;
        if (order.getOrder().equals("desc")) {
            sort = Sort.by(order.getColumn()).descending();
        } else {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ORDER_TYPE_NOT_FOUND);
        }

        return sort;
    }
}
