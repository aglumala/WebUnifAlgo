/**
 * 
 */
package org.algorithm.servlets;

import java.io.PrintWriter;

import org.algorithm.impl.AlgorithmEngine;

/**
 * this runnable parses only one file
 *
 */
public class TypeEquivalanceThread implements Runnable {
	private String planName=null;
	private String wsdlFileName=null;
	PrintWriter out=null;
	
	public TypeEquivalanceThread(String planName, String wsdlFileName, PrintWriter out){
		 this.planName=planName;
		 this.wsdlFileName=wsdlFileName;
		 this.out=out; 	 
	}
	@Override
	public void run() {
		synchronized (out) { //every thread must print all this out		
			AlgorithmEngine.enter(planName, wsdlFileName, out); //run type equivalence algorithms on the wsdl		
		}
	}

}
