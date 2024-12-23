# 👨‍👩‍👦‍👦 JUNGLE ROYALE WEB SERVER 👨‍👩‍👦‍👦 

> 한 사람만이 살아남는 배틀로얄 게임 👉 (사이트 url)

## ⭐ Main Feature
### 회원가입 및 로그인
- JWT 이용
- Oauth2.0 카카오 로그인

## 🔧 Stack
- **Language**: Java 17
- **Library & Framework** : SpringBoot 3.3.7
- **Database** : AWS RDS (MySQL 8.3.0)
- **ORM** : JPA 3.4.0
- **IDE** : IntellJ 
- **Build Tool** : Gradle 8.11.1
- **Deploy**: AWS EC2


## Project Structure

```markdown
src
├── common
│   ├── config
│   ├── types
│   ├── exceptions
│   └── utils
│
├── controller
├── domain
│   └── dto
├── service
│   └── user
└── repository
```


## 👨‍👩‍👧‍👦 Developer
*  **노태호** ([taehoy](https://github.com/taehoy))

## Convention
> 주요 컨벤션을 정리했다. 자세한 내용은 링크를 참고하자.

### Java
- 블록 들여쓰기 : 4칸
- if, for, catch와 여는 괄호 '(' 사이에 공백
- 변수 선언 : 모든 변수 선언은 하나의 한개만 -> `int a, b;` 는 사용하지 않는다.
- 네이밍
  - 클래스 이름 : `UpperCamelCase` 로 작성한다.
  - 메소드 이름 : `lowerCamelCase` 로 작성한다. 메소드 이름은 동사로 작성한다. `sendMessage stop`
  - 상수 이름 : 상수이름은 `CONSTANT_CASE` 스타일로 작성한다.
  - Parameter, 지역변수 이름 : `lowerCamelCase` 로 작성한다.
  - final, immutable 이더라도 지역변수는 상수로 생각하지 않는다.
- Java Doc
  ``` java
    /**
    * Multiple lines of Javadoc text are written here,
    * wrapped normally...
    */  
    public int method(String p1) { ... }
    ```
  - 블록태그의 정석 순서는 `@param` , `@return` , `@throws` , `@deprecated` 이다.
  - Java Doc은 최소한 모든 public클래스와, public 및 protected 멤버에 다 붙여주어야 한다.
- [참고](https://sihyung92.oopy.io/af26a1f6-b327-45a6-a72b-c6fcb754e219)
- [구글 자바 컨벤션](https://google.github.io/styleguide/javaguide.html)

### SpringBoot
- 변수명, 상수명, 함수명, 클래스명 등 줄여쓰지 않고 명확하게 쓰기
    - 다소 길다는 점을 의식해 IDE의 자동완성 기능을 사용하는 걸 권장합니다.
    - IDE 에서 자동으로 이름을 지을 때 클래스명의 카멜케이스를 알려주는데, 이를 그대로 사용할 수 있습니다.
    - 예시. logs -> homeMatchResultPostScoreLogs, MatchResultPost post -> MatchResultPost matchResultPost
- @Transactional 어노테이션은 클래스에 붙이는 것보다 메서드에 바로 붙임 (메서드 코드 조각만 보고도 바로 파악하기 위함)
    - 서비스 코드에서 조회 메소드는 무조건 @Transactional(readOnly = true) 어노테이션 붙이기 ( 하위 메소드는 선택 )
    - 서비스 코드에서 생성 / 수정 / 삭제 메소드는 무조건 @Transactional 어노테이션 붙이기 ( 하위 메소드는 선택 )
- DTO 는 총 3가지의 종류가 있습니다.
    - 엔티티를 담는 엔티티 DTO, 응답 DTO 에서 사용합니다.
        - 엔티티 DTO 는 '엔티티명 + ResponseDTO' 이름을 사용합니다. ex) CompetitionResponseDTO
        - 엔티티 DTO 는 Lombok 의 Builder 패턴 기능을 사용합니다.
        - 엔티티 DTO 는 Lombok 의 Getter 어노테이션을 사용합니다.
        - from 정적 메소드로 엔티티 정보를 엔티티 DTO 로 변환합니다.
            - 엔티티 DTO 는 여러 응답 DTO 에서 이용합니다.
            - 매 번 엔티티 정보를 엔티티 DTO 로 변환하는 코드가 중복되어 코드량이 길어지는 문제가 있습니다.
            - 엔티티 DTO 에서 표현해야할 값이 많고 많은 연관 관계가 포함된다면 그에 따라 코드량이 길어집니다.
            - 긴 코드량의 중복을 막기 위해 엔티티 DTO 의 정적 메소드로 코드를 분리했습니다.
            - 아래 처럼 from 메소드를 호출하여 단일 값을 변환하거나, 배열로 변환할 때 stream().map() 메소드에 전달할 수 있습니다.
                - 엔티티 -> 엔티티 DTO 변환 예시
                    - ClubResponseDTO.from(club)
                - 엔티티 배열 -> 엔티티 DTO 배열 변환 예시
                    - List<MatchResultPostResponseDTO> matchResultPostResponseDTOs =
                                matchResultPosts.stream()
                                        .map(MatchResultPostResponseDTO::from)
                                        .collect(Collectors.toList());
    - 컨트롤러에서 받는 요청 DTO. 이후 서비스 단으로 보냅니다.
        - 요청 DTO 는 '메소드명 + 엔티티명 + RequestDTO' 이름을 사용합니다. ex) CreateCompetitionRequestDTO
        - 요청 DTO 는 Lombok 의 Getter 어노테이션을 사용합니다.
    - 서비스 단에서 반환할 응답 DTO, 이후 컨트롤러에서 응답으로 보냅니다.
        - 응답 DTO 는 '메소드명 + 엔티티명 + ResponseDTO' 이름을 사용합니다.  ex) CreateCompetitionResponseDTO
        - 응답 DTO 는 Lombok 의 Builder 패턴 기능을 사용합니다.
        - 응답 DTO 는 Lombok 의 Getter 어노테이션을 사용합니다.
        - 응답 DTO 는 2 가지의 종류가 있습니다.
            - 페이지네이션이 없는 응답 DTO. 데이터는 단일 값이 들어가거나 배열이 들어갑니다.
            - 페이지네이션이 있는 응답 DTO. 현재 페이지 수, 총 페이지 수, 현재 페이지 갯수를 포함하고, 데이터는 무조건 배열로 응답합니다.
        - 공통 응답 DTO 형식이 있습니다.
            - code : 응답의 종류를 알려주는 상수, 성공과 실패를 구분하고 어떤 응답인지 알려줍니다. ex) SUCCESS_GET_COMPETITIONS, SUCCESS_DELETE_COMPETITION, USER_NOT_FOUND(예외)
            - message : 코드에 대한 설명을 문장으로 서술합니다. ex) "성공적으로 대회 목록을 조회했습니다."
            - data : 엔티티 객체를 표현하는 엔티티 DTO 를 응답합니다. 하나일 경우 단일 값으로, 여러 개 일 경우 배열로 표현합니다.
            - count : (페이지네이션 응답 시) 현재 페이지의 총 개수를 나타냅니다.
            - totalPage : (페이지네이션 응답 시) 총 페이지 수를 알려줍니다.
            - totalCount : (페이지네이션 응답 시) 총 개수를 알려줍니다.
            - error : (RunTime Exception 발생시) error 값 아래에 code 와 message 값이 들어갑니다.
        - 페이지네이션 시 필드를 통일하기 위해 공통 페이지 BasePageable<xxxxxResponseDTO> 클래스 사용
    - JPA 의 더티 체킹 기능을 의도적으로 사용하지 않습니다. 더티 체킹으로 DB 에 반영된다고 해도 명시적으로 리포지토리의 저장 메소드를 호출합니다.
        - 더티 체킹으로 값이 변경되면 커밋 시점에 영속성 컨텍스트에 있는 1, 2차 캐시의 값과 비교하여, 값이 다르면 캐시 값 업데이트 후 DB 에 반영합니다.
        - 그러나 코드를 파악할 때 트랜잭션이 커밋되는 시점을 한 눈에 파악하기 힘들고, 더티 체킹이 이뤄진다는 걸 파악하기 힘듭니다.
        - DB 에 명시적으로 저장한다는 걸 한 눈에 알기 위해 리포지토리의 save 메소드를 사용합니다.
        - 예시.
            - matchResultPost.updateHomeTeam(
                        awayEntryTeam,
                        updateMatchResultPostRequestDTO.getAwayScore(),
                        updateMatchResultPostRequestDTO.getIsAwayWin(),
                        awayMatchResultPostScoreLogs
              );
          
             matchResultPostRepository.save(matchResultPost);
                
    - 복잡한 쿼리는 JPA 보다 QueryDSL 이용, 코드 재사용 가능하도록 BooleanExpression 을 활용하여 작성
    - Controller 에선 Service 으로 UserId 와 RequestDTO 를 그대로 넘기고 ResponseDTO 를 받아 바로 반환해주는 로직으로 통일
        - 필드 검증은 Spring Bean Validation 어노테이션과 서비스 단에서 검증
    - 공통 조회 로직 (findById 등) 은 UserUtils, CompetitionUtils 등으로 static method 로 분리 ( 불필요한 공통 orElseThrow 코드 반복 방지 )
    - 코드가 여러 곳에서 재사용 된다면 분리
    - Service 에서 검증하는 로직은 다른 메서드 validateXXX(XXX : 보통 Service 메서드명) 으로 분리
        - 해당 메서드 밑에 작성하여 한 눈에 검증 로직이 들어오도록 메서드 위치 조정
    - TODO 주석을 제외하고 개발 중에 일어난 모든 불필요한 주석은 최종 커밋에 반드시 삭제 ( 타인이 그 주석을 삭제하기가 판단 내리기 어려움 )
        - 필요한 주석은 충분한 부연 설명을 포함시키고 코드 가독성을 해치지 않는 선에서 작성
    - 코드 개행은 가로가 너무 길거나, 세로로 읽기가 편할 때 작성
        - Builder 패턴의 경우 Competition.builder() 까지만 작성하고 모든 체이닝 메서드는 개행으로 구분
    - 변수가 선언하는 위치와 사용하는 위치는 최대한 가깝게 위치
    
    참고 : https://itchipmunk.tistory.com/576