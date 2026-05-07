package fr.univcotedazur.skimaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SimpleSkiMasterServer {

    public static void main(String[] args) {
        SpringApplication.run(SimpleSkiMasterServer.class, args);
    }

}
