package com.example.childrenhabitsserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaRepositories
@ComponentScan("com.example")
public class ChildrenHabitsServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChildrenHabitsServerApplication.class, args);
    }

//    @Autowired
//    PasswordEncoder passwordEncoder;
//    @Override
//    public void run(String... args) throws Exception {
//        // Khi chương trình chạy
//        // Insert vào csdl một user.
//        System.out.println(passwordEncoder.encode("test"));
//    }
}
