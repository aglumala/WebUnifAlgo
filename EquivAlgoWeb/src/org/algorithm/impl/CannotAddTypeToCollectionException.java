package org.algorithm.impl;

public class CannotAddTypeToCollectionException extends Exception{

	//use variable when serializing the object
	private static final long serialVersionUID = 1L;
	private String exceptionType="Cannot Add element to the set";
	public CannotAddTypeToCollectionException(){
		
	}
	@Override
	public String toString() {

		return exceptionType;
	}
}
