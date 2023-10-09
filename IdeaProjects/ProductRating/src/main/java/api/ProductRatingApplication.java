package api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import repo.CompanyRepository;
import model.Company;

@SpringBootApplication
@ComponentScan(basePackages = {"api", "repo"})
public class ProductRatingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductRatingApplication.class,args);
    }
}
