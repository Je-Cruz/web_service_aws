package com.soaint.ejercicioSpring.utils;

import java.util.Base64;

public class EncoderBase64 {
	public static String encoderBase64(String credentials) {
		return Base64.getEncoder().encodeToString(credentials.getBytes());
	}
}
