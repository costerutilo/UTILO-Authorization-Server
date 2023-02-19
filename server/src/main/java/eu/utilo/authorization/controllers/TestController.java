package eu.utilo.authorization.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 * @author coster
 */
@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

    /**
     * Testcontroller für den utilo-client
     * zeigt token nach Redirect von openid Login
     */
    @RequestMapping("/utilo")
    public Object utilo(@RequestParam("code") String code) {

        log.debug("test-code: " + code);
        return "code: " + code;

    }
    @RequestMapping("/authorized")
    public Object authorized() {

        log.debug("test-authorized.");
        return "authorized.";

    }

}
