package org.algorirthm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FileInput {

	private static String wsdlFileName=null;
	
public static void setWSDLName(String wsdlsFile){
	wsdlFileName=wsdlsFile;
}

public static Set<String> readInput(){

   //File file = new File("C:/eclipseProjects/TypeEquivFinal/sampleFiles/inputwsdls.txt");
   //File file = new File("C:/eclipseProjects/TypeEquivFinal/sampleFiles/testWSDLUrl.txt");
	File file = new File(wsdlFileName);
    BufferedReader reader=null;
    Set<String> set=new HashSet<String>();
    try {
      
      reader= new BufferedReader(new FileReader(file));

      // dis.available() returns 0 if the file does not have more lines.
      while (reader.read()!=-1) {
    	
    	  String string=reader.readLine();
    	  if(string!=null){
    		  set.add(string);
    	  }       
      }
      // dispose all the resources after using them.
      reader.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return set;
  }
  
}