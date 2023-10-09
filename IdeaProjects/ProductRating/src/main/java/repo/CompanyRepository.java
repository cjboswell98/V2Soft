package repo;

import model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CompanyRepository extends MongoRepository<Company, String> {

    List<Company> findByCompanyContainsIgnoreCase(String company);
    List<Company> findByCompanyContainsIgnoreCaseAndOrderAgain(String company, Boolean orderAgain);
    List<Company> findByOrderAgain(Boolean orderAgain);
}

