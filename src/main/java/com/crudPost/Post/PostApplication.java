package com.crudPost.Post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;

// spring boot 설정파일이면서 동시에 component 이기도 하다.
// -> 즉, bean으로 인식된다.
@SpringBootApplication
public class PostApplication implements CommandLineRunner {

	// main 메소드는 spring이 관리안한다.
	public static void main(String[] args) {
		SpringApplication.run(PostApplication.class, args);
	}

	// Datasource Bean (spring이 관리하는 객체)
	// Autowired 를 통해 의존성 주입
	@Autowired
	DataSource dataSource;

	// implement CommandLineRunner 를 통해 직접 스프링부트에서 제공하는 인터페이스 사용
	// 거기서 run 메서드를 사용함으로써 spring에서 직접 관리하도록 변경

	@Override
	public void run(String... args) throws Exception {
		Connection connection = dataSource.getConnection();
		connection.close();

	}

}
