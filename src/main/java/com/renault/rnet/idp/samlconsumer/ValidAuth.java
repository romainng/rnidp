package com.renault.rnet.idp.samlconsumer;

import org.joda.time.Instant;

public class ValidAuth {

	/**
	 * Check the signature
	 * 
	 * @param signature
	 * @return
	 */
	public static boolean AUTH_SIGNATURE(String signature) {
		// TODO
		System.out.println("SIGNATURE="+signature);
		System.out.println();
		return true;
	}

	/**
	 * Check the validity before
	 * 
	 * @param before
	 * @return
	 */
	public static boolean AUTH_BEFORE(Instant before) {
		Instant now = Instant.now();
		if(now.isAfter(before)){
			return true;
		}
		System.out.println("KO instant now="+now.toString()+" before="+before.toString());
		return false;
	}

	/**
	 * Check the validity after
	 * 
	 * @param after
	 * @return
	 */
	public static boolean AUTH_AFTER(Instant after) {
		Instant now = Instant.now();
		if(now.isBefore(after)){
			return true;
		}
		System.out.println("KO instant now="+now.toString()+" after="+after.toString());
		return false;
	}

}
