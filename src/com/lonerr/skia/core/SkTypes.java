package com.lonerr.skia.core;

public class SkTypes {
	public static <E> void SkTSwap (E e1,E e2){
		E tmp = e1;
		e1 = e2;
		e2 = tmp;
	}
}
