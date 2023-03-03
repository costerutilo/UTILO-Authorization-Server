package eu.utilo.authorization.controllers;

import eu.utilo.authorization.entity.OauthClient;
import eu.utilo.authorization.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Controller zur Kommunikation mit dem VUE Client
 * Verwaltung von Clients
 * @author coster
 */
@RestController
@Slf4j
@RequestMapping("/client")
@CrossOrigin(origins = "http://127.0.0.1:9010")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/findAllClients")
    public List<OauthClient> findAllClients() {

        log.debug("findAllClients");
        List<OauthClient> clients = clientRepository.findAll();
        return clients;

    }

}
