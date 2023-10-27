package com.product.rating.controller;

import com.product.rating.domain.Client;
import com.product.rating.services.ClientService;
import com.product.rating.services.ClientServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@RestController
public class ClientController {

    @Autowired
    private final ClientService clientService;

    @Autowired
    private ClientServiceImpl clientServiceImpl;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostConstruct
    public void initRolesAndUser() {
        clientServiceImpl.initRolesAndUser();
    }

    // Below are Endpoints for Client
    // REST API endpoint to create a new client
    @PostMapping("/newClient")
    public String newClient(@RequestBody Map<String, String> request) {
        String firstName = request.get("firstName");
        String lastName = request.get("lastName");
        String userName = request.get("userName");
        String userPassword = request.get("userPassword");
        return clientService.createNewClient(firstName, lastName, userName, userPassword);
    }

    @GetMapping({"/forAdmin"})
    @PreAuthorize("hasAnyRole('Admin')")
    public String forAdmin() {return "This URL is only for admin";}

    @GetMapping({"/forUser"})
    @PreAuthorize("hasAnyRole('Admin','user')")
    public String forUser() {return "This URL is only for user";}

    // REST API endpoint to get all clients
    @GetMapping("/viewClients")
    public List<Client> getAllClients() {
        return clientService.findAllClients();
    }

    // REST API endpoint to verify login information
//    @PostMapping("/verifyLogin")
//    public ResponseEntity<String> verifyLogin(@RequestBody Map<String, String> request) {
//        String userName = request.get("userName");
//        String userPassword = request.get("userPassword");
//
//        if (clientService.verifyLoginInformation(userName, userPassword)) {
//            return ResponseEntity.ok("Login successful");
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//        }
//    }
}
