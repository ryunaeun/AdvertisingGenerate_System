package com.example.server;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		// resources 디렉터리에 있는 .env 읽기
		Dotenv dotenv = Dotenv.configure()
				.directory("src/main/resources")
				.ignoreIfMissing() // .env 파일이 없더라도 오류 발생 방지
				.load();

		// DB 연결 정보 가져오기
		String dbUrl = dotenv.get("DB_URL");
		String dbUsername = dotenv.get("DB_USERNAME");
		String dbPassword = dotenv.get("DB_PASSWORD");
		

		// 환경 변수 유효성 검증
		if (dbUrl == null || dbUsername == null || dbPassword == null) {
			System.err.println("Error: Missing database configuration in .env file");
			System.exit(1);
		}

		// System 환경 변수로 전달
		System.setProperty("DB_URL", dbUrl);

		System.setProperty("DB_USERNAME", dbUsername);
		System.setProperty("DB_PASSWORD", dbPassword);
		System.setProperty("SMTP_USERNAME", dotenv.get("SMTP_USERNAME"));
		System.setProperty("SMTP_PASSWORD", dotenv.get("SMTP_PASSWORD"));
		System.out.println("DB_URL : " + dbUrl);
		System.out.println("DB_USERNAME : " + dbUsername);
		System.out.println("DB_PASSWORD : " + dbPassword);
		System.out.println("SMTP_USERNAME : " + dotenv.get("SMTP_USERNAME"));
		System.out.println("SMTP_PASSWORD : " + dotenv.get("SMTP_PASSWORD"));
		SpringApplication.run(ServerApplication.class, args);
	}
}
