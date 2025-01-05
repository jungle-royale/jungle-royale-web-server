package com.example.jungleroyal.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class TimeUtils {
    public static LocalDateTime convertUtcToKst(LocalDateTime utcTime) {
        return utcTime.atOffset(ZoneOffset.UTC) // UTC로 해석
                .atZoneSameInstant(ZoneId.of("Asia/Seoul")) // KST로 변환
                .toLocalDateTime(); // LocalDateTime 반환
    }

    /**
     * KST(Asia/Seoul)를 UTC로 변환
     *
     * @return 변환된 UTC LocalDateTime
     */
    public static LocalDateTime createUtc() {
        LocalDateTime kstTime = LocalDateTime.now();
        return kstTime.atZone(ZoneId.of("Asia/Seoul")) // KST로 해석
                .withZoneSameInstant(ZoneOffset.UTC) // UTC로 변환
                .toLocalDateTime(); // LocalDateTime 반환
    }
}
