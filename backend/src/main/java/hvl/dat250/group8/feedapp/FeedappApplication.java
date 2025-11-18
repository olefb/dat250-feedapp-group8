package hvl.dat250.group8.feedapp;

import hvl.dat250.group8.feedapp.service.PollsDataInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FeedappApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedappApplication.class, args);
    }

    @Bean
    public CommandLineRunner runDataInitialization(PollsDataInitializer initializer) {
        return args -> {
            initializer.initializeData();
        };
    }
}