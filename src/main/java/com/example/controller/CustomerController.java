package com.example.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1")
@AllArgsConstructor
public class CustomerController {

    private final SessionRegistry sessionRegistry;

    @GetMapping("index")
    public ResponseEntity<String> getIndex(){
        return ResponseEntity.ok("index response");
    }

    @GetMapping("index2")
    public ResponseEntity<String> getIndex2(){
        return ResponseEntity.ok("Hello Word Not SECURED");
    }

    @GetMapping("session")
    public ResponseEntity<?> getSession(){
        String sessionId = "";
        User user = null;

        //Obtener las sessiones iniciadas
        List<Object> sessions = sessionRegistry.getAllPrincipals();

        //recorremos las sessiones listadas
        for (Object session : sessions) {
            // verficamos si la session es de un usuario
            if (session instanceof User)
                //igualamos la session instanciada con el usuario
                user = (User) session;

            //obtenemos la informacion de la session indicandole el usuario, indicamos que no queremos usuarios inactivos o guardados en cache
            List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(session, false);

            // recorremos la session informatica
            for (SessionInformation sessionInformation : sessionInformations) {
                sessionId = sessionInformation.getSessionId();
            }
        }

        //mapeamos el objeto obteido

        Map<String, Object> session = new HashMap<>();
        session.put("response", "Hello World");
        session.put("sessionId", sessionId);
        session.put("sessionUser",  user);

        return ResponseEntity.ok(session);

    }
}
