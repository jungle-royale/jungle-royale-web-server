package com.example.jungleroyal.service.repository;

import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository {
    Boolean existsByRefresh(String refresh);
    @Transactional
    void deleteByRefresh(String refresh);
}
