import api.OrderNotFoundException;
import model.Company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repo.CompanyRepository;


import java.rmi.MarshalledObject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class CompanyController {

    @Autowired
    private CompanyRepository repo;

    @GetMapping("/todos")
    public ResponseEntity<?> getAllTodos() {
        List<Company> todos = repo.findAll();
        if (todos.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("No todos available", HttpStatus.OK);
        }
    }

    @RequestMapping("/")
    public Map<String, Object> home(){
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status","OK");
        result.put("collections",new String[]{"/companies"});
        return result;
    }

    @GetMapping("/orders")
    public List<Company> readAll(@RequestParam(required = false) String company, @RequestParam(required = false) Boolean orderAgain) {
        if(company != null && !company.isBlank()) {
            if (orderAgain != null) {
                return repo.findByCompanyContainsIgnoreCaseAndOrderAgain(company,orderAgain);
            } else {
                return repo.findByCompanyContainsIgnoreCase(company);
            }
        } else if (orderAgain != null) {
            return repo.findByOrderAgain(orderAgain);
        }else {
            return repo.findAll();
        }
    }

    @ResponseBody
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String characterNotFoundHandler(OrderNotFoundException ex) {
        return ex.getMessage();
    }

}
