package eu.utilo.authorization.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * liefert allgemeine Informationen, wie z. B. die Versionsnummer
 */
@RestController
@Slf4j
@CrossOrigin(origins = "http://127.0.0.1:9010")
@RequestMapping("/main")
public class MainController {

    @Autowired
    private Environment environment;

    @GetMapping("/version")
    public ObjectNode version() {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("version", "0.0.1"); // FIXME: hole aus config
        objectNode.put("profile", environment.getActiveProfiles()[0]);
        return objectNode;

    }

}
