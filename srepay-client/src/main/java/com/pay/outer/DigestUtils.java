package com.pay.outer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {


	public static String md5DigestAsHex(byte[] bytes) { //
		byte[] digest;
		try {
			digest = MessageDigest.getInstance("MD5").digest(bytes);
			return toHex(digest).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Could not find MessageDigest with algorithm \"" + "MD5" + "\"", e);
		}
	}



	private static String toHex(byte[] input) { //
		if (input == null) return null;
		StringBuffer output = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			int current = input[i] & 0xFF;
			if (current < 16) output.append("0");
			output.append(Integer.toString(current, 16));
		}

		return output.toString();
	}
}
