package com.soaint.ejercicioSpring.security;

import java.util.ResourceBundle;

import com.soaint.ejercicioSpring.utils.EncoderBase64;

public class PropertiesReader {
	private static final ResourceBundle PROPERTIES = ResourceBundle.getBundle("application");
	// ORACLE RIGHT NOW *************************************
	public static String urlRightNow(){
	    return PROPERTIES.getString("URL.RightNow");
	}
	public static String postUrlRightNow(){
	    return PROPERTIES.getString("URL.RightNow.Post");
	}
	public static String getUrlRightNow(){
	    return PROPERTIES.getString("URL.RightNow.Get");
	}
	public static String getUrlQueryRightNow(){
	    return PROPERTIES.getString("URL.RightNow.Query");
	}
	public static String getCredRightNow(){
	    return PROPERTIES.getString("Credential.RightNow");
	}
	
	// ORACLE ELOQUA *****************************************
	public static String urlEloqua(){
	    return PROPERTIES.getString("URL.Eloqua");
	}
	public static String postUrlEloqua(){
	    return PROPERTIES.getString("URL.Eloqua.Post");
	}
	public static String getUrlEloqua(){
	    return PROPERTIES.getString("URL.Eloqua.Get");
	}
	public static String getUrlQueryEloqua(){
	    return PROPERTIES.getString("URL.Eloqua.Query");
	}
	public static String getCredEloqua(){
		return "Basic "+EncoderBase64.encoderBase64(PROPERTIES.getString("Credential.Eloqua"));
	}
	
	// ORACLE SALES CLOUD ************************************
	public static String urlSalesCloud(){
	    return PROPERTIES.getString("URL.SalesCloud");
	}
	public static String postUrlSalesCloud(){
	    return PROPERTIES.getString("URL.SalesCloud.Post");
	}
	public static String urlSalesCloudLead(){
	    return PROPERTIES.getString("URL.SalesCloud.Lead");
	}
	public static String getUrlSalesCloud(){
	    return PROPERTIES.getString("URL.SalesCloud.Get");
	}
	public static String getUrlQuerySalesCloud(){
	    return PROPERTIES.getString("URL.SalesCloud.Query");
	}
	public static String getUrlQuerySalesCloudLead(){
	    return PROPERTIES.getString("URL.SalesCloud.Query.Lead");
	}
	public static String getCredSalesCloud(){
		return "Basic "+EncoderBase64.encoderBase64(PROPERTIES.getString("Credential.SalesCloud"));
	}
	
}
