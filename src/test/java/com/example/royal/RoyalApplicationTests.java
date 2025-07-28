package com.example.royal;

import com.example.royal.model.GuildMember;
import com.example.royal.repository.GuildMemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootTest
class RoyalApplicationTests {

	public static void main(String[] args) {
		SpringApplication.run(RoyalApplicationTests.class, args);
	}

	@Bean
	CommandLineRunner initData(GuildMemberRepository repo) {
		return args -> {
			repo.save(new GuildMember(null, "메이플짱", "길마", 15000, LocalDate.now().minusDays(1)));
			repo.save(new GuildMember(null, "부길123", "부길마", 8000, LocalDate.now().minusDays(3)));
			repo.save(new GuildMember(null, "신입1호", "일반", 1200, LocalDate.now().minusDays(7)));
		};
	}
}
