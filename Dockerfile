FROM eclipse-temurin:21-jdk AS build

# 작업 위치
WORKDIR /app

# 소스 코드 복사
COPY . .

# 실행 권한 부여
RUN chmod +x ./gradlew

# 프로젝트 빌드
RUN ./gradlew build -x test

FROM eclipse-temurin:21-jre

# 빌드 이미지에서 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]