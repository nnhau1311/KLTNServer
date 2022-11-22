package com.example.childrenhabitsserver;

import com.example.childrenhabitsserver.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableJpaRepositories
@ComponentScan("com.example")
@CrossOrigin()
public class ChildrenHabitsServerApplication implements CommandLineRunner{
    public static void main(String[] args) {
        SpringApplication.run(ChildrenHabitsServerApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.init();
//            storageService.deleteAll();
        };
    }

    @Override
    public void run(String... args) throws Exception {

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
