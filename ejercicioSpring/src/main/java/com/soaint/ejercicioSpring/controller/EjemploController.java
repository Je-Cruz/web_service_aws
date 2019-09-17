package com.soaint.ejercicioSpring.controller;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.soaint.ejercicioSpring.services.Eloqua;
import com.soaint.ejercicioSpring.services.SalesCloud;

import repository.InterfaceServices;

/**
 * 
 * @author jcruz
 *
 */
@RestController
@RequestMapping("/conexion")
public class EjemploController {
    
	@Autowired
    InterfaceServices rightNow;
	
	@Autowired
    Eloqua eloqua;
    
	@Autowired
    SalesCloud salesCloud;
    
    /**
     * Conecta con las API de Oracle Service Cloud (Right Now), Eloqua, Oracle Sales Cloud.
     * Comprueba si existe el contacto a través del email, obtiendo el id (RN, ELoqua) y el party number (OSC).
     * En caso de que exista comprueba si tiene lead asociados, de ser así los elimina (solo en OSC),
     * y posteriormente elimina el propio contacto. 
     * @param email
     * @return
     * @throws ParseException
     * @throws IOException
     */
    @GetMapping("/delete/{email}")
    public ResponseEntity<String> delete(@PathVariable("email") String email) throws ParseException, IOException {
    	String resultado = rightNow.checkExistenceForDeleteByEmail(email)
    			.concat(eloqua.checkExistenceForDeleteByEmail(email))
    					.concat(salesCloud.checkExistenceForDeleteByEmail(email));
     	return new ResponseEntity<>(resultado, HttpStatus.OK);
    }
    
    /**
     * Conecta con las API de Oracle Service Cloud (Right Now), Eloqua, Oracle Sales Cloud.
     * Comprueba si existe el contacto a través de un json, obtiendo el id (RN, ELoqua) y el party number (OSC).
     * Si el contacto existe, comprueba si tiene un lead asociado (solo en OSC), en caso de no tenerlo, se crea un lead y se asocia al contacto.
     * Si el contacto no existe, crea un contanto nuevo con los datos del json y además le crea y asocia un lead (solo en OSC).
     * @param contactJson
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> create(@RequestBody String contactJson) throws  ClientProtocolException, IOException {
    	String resultado = rightNow.checkExistenceForCreateByJson(contactJson)
    			.concat(eloqua.checkExistenceForCreateByJson(contactJson))
    					.concat(salesCloud.checkExistenceForCreateByJson(contactJson));
		return new ResponseEntity<>(resultado, HttpStatus.OK);
    }
    
}