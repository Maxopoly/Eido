package com.github.fsmi.eido.util;

public final class Guard {

	public static void nullCheck(Object... objects) {
		for (int i = 0; i < objects.length; i++) {
			Object o = objects[i];
			if (o == null) {
				throw new IllegalArgumentException(String.format("Argument %d can not be null", i));
			}
		}
	}

	private Guard() {
	}

}
