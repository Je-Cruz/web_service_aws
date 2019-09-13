package com.soaint.ejercicioSpring.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaint.ejercicioSpring.services.Eloqua;
import com.soaint.ejercicioSpring.services.RightNow;
import com.soaint.ejercicioSpring.services.SalesCloud;
import com.soaint.ejercicioSpring.utils.UriReplace;

@RestController
@RequestMapping("/conexion")
public class EndPoint {
    
    RightNow rightNow = new RightNow();
    Eloqua eloqua = new Eloqua();
    SalesCloud salesCloud = new SalesCloud();
    UriReplace uriReplace = new UriReplace();
    
    @GetMapping("/delete/{email}")
    public ResponseEntity<String> delete(@PathVariable("email") String email) throws ParseException, IOException {
     	String resultado = email + "<br>";
     	int idRightNow = rightNow.getContactByEmail(email);
     	int idEloqua = eloqua.getContactByEmail(email);
     	ArrayList<Integer> idSalesCloud = salesCloud.getContactByEmail(email);
     	
     	if (idRightNow > 0) {
     		rightNow.deleteContact(idRightNow);
     		resultado += "ELIMINADO de Right Now<br>";
     	} else {
     		resultado += "No existe en Right Now<br>";
     	}
     	
     	if (idEloqua > 0) {
     		eloqua.deleteContact(idEloqua);
     		resultado += "ELIMINADO de Eloqua<br>";
     	} else {
     		resultado += "No existe en Eloqua<br>";
     	}
     	
     	if (!idSalesCloud.isEmpty()) {
     		salesCloud.deleteLead(salesCloud.getLeadByContactPartyNumber(email));
     		salesCloud.deleteContact(idSalesCloud);
     		resultado += "ELIMINADO de Sales Cloud<br>";
     	} else {
     		resultado += "No existe en Sales Cloud<br>";
     	}
     	
     	return new ResponseEntity<>(resultado, HttpStatus.OK);
    }
    
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public String createPerson(@RequestBody String person) throws  ClientProtocolException, IOException {
    	String resultado = "";
	    
	    ObjectMapper om = new ObjectMapper();
		JsonNode nodes = om.readTree(person).get("correo");
		String email = uriReplace.JsonTransformer(nodes.asText());
    	
    	if (rightNow.getContactByEmail(email) > 0) {
    		resultado += "Ya existe en Right Now<br>";
     	} else {
     		rightNow.postContactSave(person);
     		resultado += "Creado en Right Now<br>";
     	}
     	
     	if (eloqua.getContactByEmail(email) > 0) {
     		resultado += "Ya existe en Eloqua<br>";
     	} else {
     		eloqua.postContactSave(person);
     		resultado += "Creado en Eloqua<br>";
     	}
     	
		if (!salesCloud.getContactByEmail(email).isEmpty()) {
     		resultado += "Ya existe en Sales Cloud<br>";
     	} else {
     		salesCloud.postContactSave(person);
     		salesCloud.postSalesLeadSave(email);
     		resultado += "Creado en Sales Cloud<br>";
     	}
    	
		return resultado;
    }
    
}