package com.example.oauthlogin.common.util;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class RandomNicknameGenerator {

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
            "권상현", "권한비", "김남훈", "김도현", "김수환(A)", "김아람",
            "김정환", "김제림", "김지훈", "김현영", "김희원", "남서하",
            "노태호", "류병현", "박수호", "박자은", "서성진", "여강수",
            "이동인", "이은재", "이지원", "정재명", "채수경", "최민규",
            "최우진", "최재혁", "최주혁", "하다현", "한승찬", "홍용재",
            "김동현", "김수환(B)", "신희섭", "이민영", "최진영"
    };

    private final Random RANDOM = new Random();

    public String generate(){
        // 랜덤 형용사
        String adjective = ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)];
        // 랜덤 명사
        String nouns = NOUNS[RANDOM.nextInt(NOUNS.length)];


        return MessageFormat.format("{0} {1}", adjective, nouns);
    }
}
