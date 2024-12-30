package com.example.jungleroyal.domain.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> data; // 페이지 데이터
    private long total; // 총 데이터 개수

}
