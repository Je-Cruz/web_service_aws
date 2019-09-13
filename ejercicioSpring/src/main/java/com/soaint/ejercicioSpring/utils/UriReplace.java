package com.soaint.ejercicioSpring.utils;

public class UriReplace {
	
	public static String JsonTransformer(String jsonSend) {
		return jsonSend.replace("ñ", "n").replace("Ñ", "N")
        .replace("Á", "A").replace("á", "a").replace("É", "E").replace("é", "e").replace("Í", "I").replace("í", "i").replace("Ó", "O").replace("ó", "o").replace("Ú", "U").replace("ú", "u")
        .replace("Ä", "A").replace("ä", "a").replace("Ë", "E").replace("ë", "e").replace("Ï", "I").replace("ï", "i").replace("Ö", "O").replace("ö", "o").replace("Ü", "U").replace("ü", "u");
    }
}
