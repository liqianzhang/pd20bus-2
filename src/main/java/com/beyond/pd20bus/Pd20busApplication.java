package com.beyond.pd20bus;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class})
@RestController //既然已经引入了web的支持，把它作为restController
@Slf4j
@ComponentScan("com")
@MapperScan("com.practice.mapper")
public class Pd20busApplication {

	public static void main(String[] args) {
		SpringApplication.run(Pd20busApplication.class, args);
	}

}
