package com.example.jungleroyal.common.util;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ValidationUtil {
    private ValidationUtil(){
        // 유틸리티 클래스 생성 방지
    }

    /**
     * 검증 조건을 만족하지 않을 경우 지정된 예외를 발생시킵니다.
     *
     * @param target       검증 대상 객체
     * @param condition    검증 조건
     * @param exceptionSupplier 예외를 생성하는 Supplier
     * @param <T>          검증 대상 객체 타입
     * @throws RuntimeException 조건을 만족하지 않을 경우 발생
     */
    public static <T> void validateOrThrow(T target, Predicate<T> condition, Supplier<? extends RuntimeException> exceptionSupplier) {
        if (condition.test(target)) {
            throw exceptionSupplier.get();
        }
    }
}
