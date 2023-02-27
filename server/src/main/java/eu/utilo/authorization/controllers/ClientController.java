package eu.utilo.authorization.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller zur Kommunikation mit dem VUE Client
 * @author coster
 */
@RestController
@Slf4j
@RequestMapping("/client")
public class ClientController {

//    /**
//     * Umweg über lokalen Controller, da es mir nicht gelungen ist
//     * als redirect_uri den localhost, 127.0.0.1 oder lokalen Host zu verwenden.
//     */
//    @RequestMapping("/utilo")
//    public Object utilo(
//            HttpServletResponse httpServletResponse,
//            @RequestParam("code") String code
//    ) {
//
//        log.debug("test-code: " + code);
//        // return "code: " + code;
//        httpServletResponse.setHeader("Location", "http://127.0.0.1:9010/admin?code=" + code);
//        httpServletResponse.setStatus(302);
//        return true;
//
//    }

}
