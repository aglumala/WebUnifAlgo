package org.algorithm.impl;

public final class FileIsEmptyException extends Exception {

	
	private static final long serialVersionUID = 1L;
	private String description="The file cannot be created: It doesnot exist";
	public FileIsEmptyException(){
		
	}
	public FileIsEmptyException(String desc){
		this.description=desc;
	}
	@Override
	public String toString() {
		
		return description;
	}
}
