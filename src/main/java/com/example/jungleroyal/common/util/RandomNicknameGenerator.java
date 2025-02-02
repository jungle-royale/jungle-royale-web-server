package com.example.jungleroyal.common.util;


import com.example.jungleroyal.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class RandomNicknameGenerator {

    private final UserRepository userRepository;

    // 형용사 배열
    private final String[] ADJECTIVES =  {
            "공부하는", "열정적인", "웃고있는", "멋진", "춤추는", "노래하는",
            "고민하는", "성실한", "달리는", "뛰는", "쉬는", "밥먹는",
            "책읽는", "웃음짓는", "생각하는", "코딩하는", "핵심을짚는",
            "상상하는", "독서하는", "즐기는", "몰입하는", "집중하는",
            "게임하는", "노력하는", "도전하는", "방학을즐기는", "출근하는",
            "퇴근하는", "운동하는", "앉아있는", "서있는", "일하는",
            "그리는", "보고있는", "바쁜", "피곤한", "자고있는", "꿈꾸는",
            "노트하는", "행복한", "쓸데없는", "고요한", "우아한",
            "귀여운", "어려운", "쉬운", "힘든", "기다리는", "미소짓는",
            "땀흘리는", "열심히하는", "낙천적인", "완벽한", "여유로운",
            "직진하는", "방향찾는", "도움주는", "반짝이는", "영감을주는",
            "웃으며걷는", "우울한", "흥미로운", "신나는", "충분한",
            "공감하는", "지혜로운", "깨어있는", "현실적인", "재밌는",
            "슬픈", "명쾌한", "적극적인", "호기심가득한", "따뜻한",
            "차가운", "완고한", "꿈많은", "평화로운", "불타는",
            "잔잔한", "흔들리는", "깨닫는", "분석하는", "계획하는",
            "요리하는", "탐구하는", "연구하는", "체크하는", "기록하는",
            "탐험하는", "발견하는", "응원하는", "위로하는", "인정받는",
            "칭찬하는", "고백하는", "기다림을즐기는"
    };
    // 명사 배열
    private final String[] NOUNS = {
            "강아지", "고양이", "사자", "호랑이", "코끼리", "기린", "판다", "여우", "늑대", "곰",
            "토끼", "다람쥐", "햄스터", "돼지", "양", "염소", "닭", "독수리", "펭귄", "물개",
            "돌고래", "상어", "문어", "오징어", "게", "가재", "새우", "말", "소", "치타",
            "하이에나", "코알라", "캥거루", "두더지", "고래", "개구리", "뱀", "이구아나", "두루미", "까치",
            "비둘기", "타조", "공작새", "하늘소", "나비", "잠자리", "개미", "벌", "말벌", "파리"
    };

    private final Random RANDOM = new Random();

    public String generate(){
        int attempts = 0;
        final int maxAttempts = 10; // 닉네임 생성 시도 횟수 제한

        String nickname;
        do {
            // 랜덤 형용사와 명사 조합
            String adjective = ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)];
            String noun = NOUNS[RANDOM.nextInt(NOUNS.length)];
            nickname = MessageFormat.format("{0}{1}", adjective, noun);

            // 중복 체크
            if (!userRepository.existsByUsername(nickname)) {
                return nickname; // 중복되지 않으면 반환
            }

            attempts++;
        } while (attempts < maxAttempts);

        throw new IllegalStateException("닉네임 생성 실패: 중복된 닉네임이 너무 많습니다.");
    }
}
