package com.example.jungleroyal.util;

import com.example.jungleroyal.common.util.RandomNicknameGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RandomNicknameGeneratorTest {
    private RandomNicknameGenerator randomNicknameGenerator;

    @BeforeEach
    void setUp() {
        randomNicknameGenerator = new RandomNicknameGenerator();
    }

    @Test
    void testGeneratedNicknameContainsValidAdjectiveAndNoun(){
        // Given
        Set<String> validAdjectives = new HashSet<>(Arrays.asList("코딩하는", "핀토스하는", "웹서버하는", "알고리즘하는", "나만무하는", "자고있는"));
        Set<String> validNouns = new HashSet<>(Arrays.asList("노태호", "이은재", "이지원", "정재명", "최주혁"));

        // When
        String generatedNickname = randomNicknameGenerator.generate();

        // Then
        String[] parts = generatedNickname.split(" ");
        assertThat(parts).hasSize(2); // 형용사와 명사 두 부분으로 나뉘어야 함
        assertThat(validAdjectives).contains(parts[0]); // 형용사가 유효한 목록에 포함되어야 함
        assertThat(validNouns).contains(parts[1]); // 명사가 유효한 목록에 포함되어야 함
    }

    @RepeatedTest(100) // 랜덤성 검증을 위해 100번 반복 실행
    void testGeneratedNicknameIsRandom() {
        // Given
        Set<String> uniqueNicknames = new HashSet<>();

        // When
        for (int i = 0; i < 100; i++) {
            uniqueNicknames.add(randomNicknameGenerator.generate());
        }

        // Then
        assertThat(uniqueNicknames.size()).isGreaterThan(1); // 생성된 닉네임은 중복되지 않아야 함
    }
}
