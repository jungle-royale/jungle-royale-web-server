# Dockerfile 예시
# (Amazon Corretto 17 JDK를 사용합니다)
FROM amazoncorretto:17

# 작업 디렉터리 생성 및 이동
WORKDIR /app

# 빌드된 JAR 파일 복사
# (build/libs 폴더에 JAR 파일이 있다고 가정)
COPY build/libs/*.jar app.jar

# 필요시 포트 열기 (기본 8080 가정)
EXPOSE 8080

# 컨테이너 실행 시 JAR 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]