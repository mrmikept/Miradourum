package pt.uminho.di.aa.miradourum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import pt.uminho.di.aa.miradourum.repositories.UserRepository;
import pt.uminho.di.aa.miradourum.services.UserService;

@SpringBootApplication
public class MiradourumApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiradourumApplication.class, args);

    }

}
