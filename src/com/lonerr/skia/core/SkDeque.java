package com.lonerr.skia.core;

import java.util.ArrayList;
import java.util.Iterator;

public class SkDeque<E>{
	
	private ArrayList<E> fDeque = new ArrayList<E>();
	public void push_back(E e) {
		fDeque.add(e);
	}

	public int count() {
		return fDeque.size();
	}
	
	public SkIter<E> iter(){
		return new SkIter<E>() {
			private Iterator<E> iterator = fDeque.iterator();
			@Override
			public E next() {
				return iterator.hasNext()?iterator.next():null;
			}
		};
	}

	public void pop_back() {
		fDeque.remove(fDeque.size()-1);
	}

	public E back() {
		if(fDeque.size() == 0)
			return null;
		return fDeque.get(fDeque.size()-1);
	}

	public void clear() {
		fDeque.clear();
	}

	public boolean empty() {
		return fDeque.size() == 0;
	}
}
