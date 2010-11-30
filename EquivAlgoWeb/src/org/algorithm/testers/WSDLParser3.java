package org.algorithm.testers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.algorirthm.util.FileInput;
import org.algorirthm.util.Log4JExample;
import org.algorithm.impl.AllComplexType;
import org.algorithm.impl.CannotAddTypeToCollectionException;
import org.algorithm.impl.ChoiceType;
import org.algorithm.impl.MappingDictionary;
import org.algorithm.impl.SequenceType;
import org.algorithm.impl.SimpleTypeImp;
import org.algorithm.impl.TypeMap;
import org.algorithm.intf.Type;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

//Main Parser class for wsdl
public class WSDLParser3 {
	
	 //map contains all the Types obtained from a given wsdl.
	private Map<String, Type> localMap=null;
	public static final Log4JExample log4JExample;
	
	static{
		 log4JExample=new Log4JExample();
	}
	
	public  Map<String, Type> getTypeMap(){
		return localMap;
	}
	public void process(Element element, Map<String, Type> localMap) {
	
		inspectHybrid(element, localMap);
	    
	    List<?> content = element.getContent();
	    Iterator<?> iterator = content.iterator();
	    
	    while (iterator.hasNext()) {
	      Object o = iterator.next();
	      if (o instanceof Element) {
	        Element child = (Element) o;
	        process(child, localMap);     
	      }
	    }
  }
	//assumption:the elements in the each original complex type are all simple types
	public List<Type> getAllTNS(Map<String, Type> localMap, String tnsType){
	
		List<Type> types=new ArrayList<Type>();
		if(tnsType.startsWith("tns:")){
			
			String tnsName=tnsType.substring(tnsType.indexOf(":")+1);
			
			  if(localMap.containsKey(tnsName.trim())){
					
					Type originalComplex=localMap.get(tnsName.trim());
					if(originalComplex instanceof SequenceType){
						
						SequenceType t1=(SequenceType)originalComplex;
						
						return t1.getElements();
					}
					if(originalComplex instanceof ChoiceType){
						
						ChoiceType t2=(ChoiceType)originalComplex;
						
						return t2.getElements();
					}
					if(originalComplex instanceof AllComplexType){
						
						AllComplexType t3=(AllComplexType)originalComplex;
						
						return t3.getElements();
					}
			  }
			  
		}
		 
		return types; //will always be an empty set.
	
	}
	public  void inspectHybrid(Element element, Map<String, Type> localMap) { 
	
	  	//SimpleTypeImp simple=null;
	  	SequenceType seqComplex=null;
	    
	    String type=null;
	    Namespace namespace = element.getNamespace();
	    try{ //all adElementType() methods will throws  a CannotAddTypeToCollectionException		
	    	
	    if (namespace != Namespace.NO_NAMESPACE) {
	      
	      String elementName=element.getAttributeValue("name");
	      
	      Element parentElement=element.getParent();
	     
	      //handle sequence element <schema><complex><sequence><element>...</element></sequence></complex>....</schema>
	      if(parentElement!=null && parentElement.getName().equals("sequence")){
	    	  
	    	  type=getTypeAttribute(element);
	    	  
	    	  Element parent2=parentElement.getParent();
	    	  
	    	  if(parent2!=null && parent2.getName().equals("complexType")){
	    		  
	    		  Element parent3=parent2.getParent();
	    		  
	    		  if(parent3!=null && parent3.getName().equals("element")){
	    			  
	    			  String complexTypeName=parent3.getAttributeValue("name");
	    			  
	    			if(type!=null){
	    				
	    				if(localMap.containsKey(complexTypeName)){
	    					
	    					  Type complexType=localMap.get(complexTypeName);
	    					  if(complexType instanceof SequenceType){
	    						 
	    						  List<Type> elements=getAllTNS(localMap, type);
	    						  
	    						  if(elements!=null){

	    							  for (Type simpleType : elements) {
	    								  if(simpleType instanceof SimpleTypeImp){
	    									  SimpleTypeImp simp=(SimpleTypeImp)simpleType;
	    									  
	    									  ((SequenceType)complexType).addElementType(simp.getTypeName(), simp.getName());
	    								  }
	    							}
	    						  }else{
	    						  		System.out.println("TYPE TYPE "+type);
	    						  	}
	    						  ((SequenceType)complexType).addElementType(type, elementName);
								localMap.put(complexTypeName, complexType);
	    					  }
	    					  
	    				 }else{	  
	    					seqComplex=new SequenceType();
	    			    	seqComplex.setName(complexTypeName);
	    			    	 //check for TNS first
	    			    	List<Type> elements=getAllTNS(localMap, type);
	    			    	
	    			    	if(elements!=null){
  							  for (Type simpleType : elements) {
  								  if(simpleType instanceof SimpleTypeImp){
  									  SimpleTypeImp simp=(SimpleTypeImp)simpleType;
  									  seqComplex.addElementType(simp.getTypeName(), simp.getName());
  								  }
  							  }
  						  	}else{
  						  		System.out.println("TYPE TYPE "+type);
  						  	}
	    			    	seqComplex.addElementType(type, elementName);
							localMap.put(complexTypeName, seqComplex);
	    			    	  
	    				  }
	    			  
	    				}
	    			
	    		  }else if(parent3!=null && parent3.getName().equals("schema")){
	    			  
	    			  String complexTypeName=parent2.getAttributeValue("name");    			  
	    			 
	    			  if(type!=null){
	    				  
	    				if(localMap.containsKey(complexTypeName)){
	    					
	    					  Type complexType=localMap.get(complexTypeName);
	    					  
	    					  if(complexType instanceof SequenceType){
	    						  
	    						  SequenceType seq=(SequenceType)complexType;
	    						  seq.addElementType(type, elementName);
	    						  
	    						  //check for TNS first
	    						  List<Type> elements=getAllTNS(localMap, type);
	    						
	    						  if(elements!=null){
	    							  for (Type simpleType : elements) {
	    								  if(simpleType instanceof SimpleTypeImp){
	    									  SimpleTypeImp simp=(SimpleTypeImp)simpleType;
	    									  seq.addElementType(simp.getSimple().getTypeName(), simp.getName());
	    									  
	    								  }
	    							}
	    					 
	    							  for(Type type2 : seq.getElements()) {
	    								  SimpleTypeImp simp2=(SimpleTypeImp)type2;
	    								  System.out.println(seq.getName()+" ELEMENT ( "+simp2.getName()+") TYPE ("+simp2.getSimple().getTypeName()+")");		 
	    							  }  
	    						  }
	    						
	    						  localMap.put(complexTypeName, complexType);
	    					  }
	    				  }else{	  
	    					seqComplex=new SequenceType();
	    			    	seqComplex.setName(complexTypeName);
	    			    	 //check for TNS first
	    			    	List<Type> elements=getAllTNS(localMap, type);
	    			    	
  						  	if(elements!=null){
  							  for (Type simpleType : elements) {
  								  
  								  if(simpleType instanceof SimpleTypeImp){
  									  SimpleTypeImp simp=(SimpleTypeImp)simpleType;
  									  seqComplex.addElementType(simp.getSimple().getTypeName(), simp.getName());
  								  }
  							  }
  						  	}
	    			    	seqComplex.addElementType(type, elementName);
							localMap.put(complexTypeName, seqComplex);			    	  
	    				  }		
		    			}  
	    		  }
	    	}else if(parent2!=null && parent2.getName().equals("extension")){
	    		  
	    		  Element parentExt=parent2.getParent();
	    		  if(parentExt!=null && parentExt.getName().equals("complexContent")){
	    			  
	    			  Element parentCmplxContent=parentExt.getParent();
	    			  
	    			  if(parentCmplxContent!=null && parentCmplxContent.getName().equals("complexType")){
	    				  	
	    				  String complexTypeName=parentCmplxContent.getAttributeValue("name");
	    				  if(type!=null){
	    					  if(localMap.containsKey(complexTypeName)){
		    					  Type complexType=localMap.get(complexTypeName);
		    					  if(complexType instanceof SequenceType){
		    						  //check for TNS first
		    						  List<Type> elements=getAllTNS(localMap, type);
		    						  if(elements!=null){
		    							  for (Type simpleType : elements) {
		    								  if(simpleType instanceof SimpleTypeImp){
		    									  SimpleTypeImp simp=(SimpleTypeImp)simpleType;
		    									  ((SequenceType)complexType).addElementType(simp.getSimple().getTypeName(), simp.getName());
		    								  }
		    							}
		    						  }
		    						  ((SequenceType)complexType).addElementType(type, elementName);
		    						  localMap.put(complexTypeName, complexType);
		    					  }
		    				  }else{	  
		    					seqComplex=new SequenceType();
		    			    	seqComplex.setName(complexTypeName);
		    			    	
		    			    	 //check for TNS first
		    			    	List<Type> elements=getAllTNS(localMap, type);
	    						  if(elements!=null){
	    							  for (Type simpleType : elements) {
	    								  if(simpleType instanceof SimpleTypeImp){
	    									  SimpleTypeImp simp=(SimpleTypeImp)simpleType;
	    									  seqComplex.addElementType(simp.getSimple().getTypeName(), simp.getName());
	    								  }
	    							}
	    						  }
		    			    	seqComplex.addElementType(type, elementName);
		    			    	localMap.put(complexTypeName, seqComplex);
		    			    	 
		    				  }
			    			}
	    				  
	    			  }
	    		  }
	    	  }
	     }
	     
	      handleStandaloneElement(element, localMap);
	      handleChoiceElement(element, localMap);
	      handleAllComplex(element, localMap);
	    }
	    
  		} catch (CannotAddTypeToCollectionException e) {
		
  			e.printStackTrace();
  		}
	   
	  }
	private void handleAllComplex(Element element, Map<String, Type> localMap){
	  String type=null;
	  String elementName=null;
	  Element parentElement=element.getParent();
	  AllComplexType allComplex=null;

 	 try {
	  if(element.getName().equals("element")
			  && parentElement!=null
			  && parentElement.getName().equals("all")
			  && parentElement.getParent().getName().equals("complexType") 
			  && parentElement.getParent().getParent().getName().equals("schema")){
		  
		  String allComplexTypeName=parentElement.getParent().getAttributeValue("name");
		  type = getTypeAttribute(element);
		  elementName=element.getAttributeValue("name"); //the element name is not stored in the simpletype 4now
		
		  if(type!=null){
			
			if(localMap.containsKey(allComplexTypeName)){
				  Type complexType=localMap.get(allComplexTypeName);
				  if(complexType instanceof AllComplexType){
					 List<Type> elements=getAllTNS(localMap, type);
						  if(elements!=null){
							  for (Type simpleType : elements) {
								  if(simpleType instanceof SimpleTypeImp){
									  SimpleTypeImp simp=(SimpleTypeImp)simpleType;
									  ((AllComplexType)complexType).addElementType(simp.getSimple().getTypeName(), simp.getName());
								  }
							}
					 }
						  
					((AllComplexType)complexType).addElementType(type, elementName);
					
					 localMap.put(allComplexTypeName, complexType);
				  }
			  }else{
				  allComplex=new AllComplexType();
				  allComplex.setName(allComplexTypeName);
				  List<Type> elements=getAllTNS(localMap, type);
				  
				  if(elements!=null){
					  for (Type simpleType : elements) {
						  if(simpleType instanceof SimpleTypeImp){
							  SimpleTypeImp simp=(SimpleTypeImp)simpleType;
							  allComplex.addElementType(simp.getSimple().getTypeName(), simp.getName());
						  }
					}
				  }
		    	allComplex.addElementType(type, elementName);
				localMap.put(allComplexTypeName, allComplex);
		    }
		}
	 }
	  } catch (CannotAddTypeToCollectionException e) {
  	 }
  }
	private void handleStandaloneElement(Element element, Map<String, Type> localMap){
	  
	  String type=null;
	  Element parentElement=element.getParent();
	  SimpleTypeImp simpleType=null;
	  
	//handle standalone element without restrictions
      if(element.getName().equals("element")&& parentElement!=null && parentElement.getName().equals("schema")&& element.getChildren().isEmpty()){
    	  
    	  String name=element.getAttributeValue("name");
    	  type=getTypeAttribute(element);
    	  //create a simple type
    	  simpleType=new SimpleTypeImp(type, name);
    	  //add it to the type set.
    	  localMap.put(name, simpleType);  
      }
      if(parentElement!=null && parentElement.getName().equals("restriction")){
    	  
    	  type=parentElement.getAttributeValue("base");
    	  
    	  Element restrictionParent=parentElement.getParent();
    	  if(restrictionParent!=null && restrictionParent.getName().equals("simpleType")){
    		  
    		  String name=restrictionParent.getAttributeValue("name");
    		  if(restrictionParent.getParent()!=null && restrictionParent.getParent().getName().equals("schema")){
    			  //create a simple type with restrictions
    			  simpleType=new SimpleTypeImp(type, name);
        		  localMap.put(name, simpleType);  
    		  }
    	  }
    }
  }
	private void handleChoiceElement(Element element, Map<String, Type> localMap){	
	  String name=element.getAttributeValue("name");
	  //SimpleType simple=null;
	  ChoiceType choiceComplex=null;
	  String type=null;
	  Element parentElement=element.getParent();
	  
	  try { 
	  
      if(element.getName().equals("element")&& parentElement!=null && parentElement.getName().equals("choice")){
    	  
    	  choiceComplex=new ChoiceType();
    	  type=getTypeAttribute(element);
    	  Element parent2=parentElement.getParent();
    	  if(parent2!=null && parent2.getName().equals("complexType")){
    		  Element parent3=parent2.getParent();
    		  
    		  if(parent3!=null && parent3.getName().equals("element")){
    			String complexTypeName=parent3.getAttributeValue("name");
    			  if(type!=null){
	    			//Create Sequence type
	    			List<Type> elements=getAllTNS(localMap, type);
	   				  
	   				 if(elements!=null){
	   					  for (Type simpleType : elements) {
	   						  if(simpleType instanceof SimpleTypeImp){
	   							  SimpleTypeImp simp=(SimpleTypeImp)simpleType;
	   							  choiceComplex.addElementType(simp.getSimple().getTypeName(), simp.getName());
	   						  }
	   					}
	   				  }
					choiceComplex.addElementType(type, name);
					
	    			choiceComplex.setName(complexTypeName);
	    			//add the choice complex to the map
	    			localMap.put(complexTypeName, choiceComplex);
	    		}
    			  
    		}else if(parent3!=null && parent3.getName().equals("schema")){
    			  String complexTypeName=parent2.getAttributeValue("name");
    			  
    			  getAllTNS(localMap, type);
    			  List<Type> elements=getAllTNS(localMap, type);
  				  
  				  if(elements!=null){
  					  for (Type simpleType : elements) {
  						  if(simpleType instanceof SimpleTypeImp){
  							  SimpleTypeImp simp=(SimpleTypeImp)simpleType;
  							  choiceComplex.addElementType(simp.getSimple().getTypeName(), simp.getName());
  						  }
  					}
  				  }
				choiceComplex.addElementType(type, name);
    			//add the choice complex to the set
    			localMap.put(complexTypeName, choiceComplex);		  
    		}
    	  }
      }
      } catch (CannotAddTypeToCollectionException e) {
      }
  }

	private  String getTypeAttribute(Element element){
	  
	  List<Attribute> attributes = element.getAttributes();
	  String value=null;
	    if (!attributes.isEmpty()) {
	      Iterator<Attribute> iterator = attributes.iterator();
	      while (iterator.hasNext()) {
	        Attribute attribute = iterator.next();
	        String name = attribute.getName();
	        value = attribute.getValue();
	        
	        Namespace attributeNamespace = attribute.getNamespace();
	        
	        if (attributeNamespace == Namespace.NO_NAMESPACE) {
	        
	        	if("type".equals(name)){
	        	  //get the type of the element
	        	 return value;
	        	}
	        }
	        else {
	         
	          if("type".equals(name)){
	        	  
	        	  return value;      	   
	          }	          
	        }
	      }
	    }
	    
	    return value;
  }
	
	public static void entryPoint(String planName, String wsdlName, PrintWriter out){
     out.println("<br /> In Method entryPoint");
    final Set<TypeMap> parsedWsdlSet=new HashSet<TypeMap>();
    List<String> wsdlURL=new ArrayList<String>();
     wsdlURL.add(planName);

     //read wsdls files for parsing
     FileInput.setWSDLName(wsdlName);
     for (Iterator<String> iterator = FileInput.readInput().iterator(); iterator.hasNext();) {
         String string=iterator.next();
         wsdlURL.add(string);
    }

    System.out.println("...STARTING TO PARSE ALL WSDLs");
      for (String url : wsdlURL) {
          if(!url.equals(planName)){
                WSDLParser3 parser=new WSDLParser3();
                System.out.println("PROCESS WSDL");
                Map<String, Type> newlocalMap=new HashMap<String, Type>();
                parser.setLocalMap(newlocalMap);

                TypeMap WSDLTypeMap=parser.processWSDL(url, newlocalMap);
                    parsedWsdlSet.add(WSDLTypeMap);

            }else{
                WSDLParser3 parser2=new WSDLParser3();
                System.out.println("PROCESS PLAN");
                Map<String, Type> localMap2=new HashMap<String, Type>();
                parser2.setLocalMap(localMap2);
                TypeMap planTypeMap=parser2.processWSDL(url, localMap2);
                  parsedWsdlSet.add(planTypeMap);
            }
      }	//end for loop
    System.out.println("...PARSING WSDLs HAS ENDED");

    processWSDLSet(parsedWsdlSet, planName);
    for (Iterator<TypeMap> iterator = parsedWsdlSet.iterator(); iterator.hasNext();) {
        TypeMap typeMap = iterator.next();

    //get the plan from the list
    if(typeMap.getFileName().equals(planName)){

            System.out.println("Plan File NAME "+typeMap.getFileName());

            Map<String, Type> map=typeMap.getLocalMap();
            Set<String>keys=typeMap.getLocalMap().keySet();
            for (String string : keys) {
                    Type obj=map.get(string);
                    if (obj instanceof SequenceType) {
                        SequenceType type=(SequenceType)map.get(string);
                        List<Type> list=type.getElements();

                        if(!list.isEmpty()){
                            System.out.println("SEQUENCE IN PLAN ("+string+")");
                            for (Type type2 : list) {
                                SimpleTypeImp stype=(SimpleTypeImp)type2;
                                System.out.println(stype.getSimple().getTypeName());
                            }
                        }else{
                                System.out.println("SEQUENCE ("+string+")IN THE PLAN HAS NO SIMPLE TYPES");
                        }
                    }else if (obj instanceof ChoiceType) {

                        System.out.println("CHOICE IN PLAN("+string+")");
                        ChoiceType type=(ChoiceType)map.get(string);
                        if(!type.getElements().isEmpty()){
                            for (Type type2 : type.getElements()) {
                                SimpleTypeImp stype=(SimpleTypeImp)type2;
                                System.out.println(stype.getSimple().getTypeName());
                            }
                        }

                    }else if (obj instanceof AllComplexType) {
                        System.out.println("ALL COMPLEX IN PLAN("+string+")");
                        AllComplexType type=(AllComplexType)map.get(string);
                        if(!type.getElements().isEmpty()){
                            for (Type type2 : type.getElements()) {
                                SimpleTypeImp stype=(SimpleTypeImp)type2;
                                System.out.println(stype.getSimple().getTypeName());
                            }
                        }
                    }
                    }
            }
        }
        System.out.println("...ALL PROCESSING (PARSING & COMPARISON) HAS ENDED...");
   
  }
  public static void main(String[] args) {
	  	
//String plan="file:///home/lumala/Desktop/unifalgo/TypeEquivFinal/sampleFiles/TravelScenario2.xsd";
    String plan="C:/eclipseProjects/TypeEquivFinal/sampleFiles/TravelScenario2.xsd";

    Set<TypeMap> parsedWsdlSet=new HashSet<TypeMap>();
    List<String> wsdlURL=new ArrayList<String>();
    wsdlURL.add(plan);

     //read wsdls files for parsing testWSDLUrl.txt
     //FileInput.setWSDLName("C:/eclipseProjects/TypeEquivFinal/sampleFiles/inputwsdls.txt");
    FileInput.setWSDLName("C:/eclipseProjects/TypeEquivFinal/sampleFiles/testWSDLUrl.txt");
     for (Iterator<String> iterator = FileInput.readInput().iterator(); iterator.hasNext();) {
         String string=iterator.next();
         wsdlURL.add(string);
    }

     //parse all wsdls in the array
    String CurLine = ""; // Line read from standard in

    System.out.println("Enter 'q' to exit: Enter any other letter to continue ");
    InputStreamReader converter = new InputStreamReader(System.in);
    BufferedReader in = new BufferedReader(converter);
    try {

    while (!((CurLine=in.readLine()).equals("q"))){

    System.out.println("You typed: " + CurLine);
    System.out.println("...STARTING TO PARSE ALL WSDLs");
      for (String url : wsdlURL) {
    	  //parse each wsdl file one at time sequentially
          if(!url.equals(plan)){
                WSDLParser3 parser=new WSDLParser3();
                System.out.println("PROCESS WSDL");
                Map<String, Type> newlocalMap=new HashMap<String, Type>();
                parser.setLocalMap(newlocalMap);

                TypeMap WSDLTypeMap=parser.processWSDL(url, newlocalMap);
                    parsedWsdlSet.add(WSDLTypeMap);

            }else{
                WSDLParser3 parser2=new WSDLParser3();
                System.out.println("PROCESS PLAN");
                Map<String, Type> localMap2=new HashMap<String, Type>();
                parser2.setLocalMap(localMap2);
                TypeMap planTypeMap=parser2.processWSDL(url, localMap2);
                  parsedWsdlSet.add(planTypeMap);
            }
      }	//end for loop
      	System.out.println("...PARSING WSDLs HAS ENDED");
      	System.out.println("...Enter q TO RUN MATCHING ALGORITHMS...");
        }//end while loop

        processWSDLSet(parsedWsdlSet, plan);
        for (Iterator<TypeMap> iterator = parsedWsdlSet.iterator(); iterator.hasNext();) {
        	TypeMap typeMap = iterator.next();

        	//get the plan from the list
        	if(typeMap.getFileName().equals(plan)){

            System.out.println("Plan File NAME "+typeMap.getFileName());
            Map<String, Type> map=typeMap.getLocalMap();
            Set<String>keys=typeMap.getLocalMap().keySet();
            for (String string : keys) {
                    Type obj=map.get(string);
                    if (obj instanceof SequenceType) {

                            SequenceType type=(SequenceType)map.get(string);
                            List<Type> list=type.getElements();

                            if(!list.isEmpty()){
                                System.out.println("SEQUENCE IN PLAN ("+string+")");
                                for (Type type2 : list) {
                                    SimpleTypeImp stype=(SimpleTypeImp)type2;
                                    System.out.println(stype.getSimple().getTypeName());
                                }
                            }else{
                                    System.out.println("SEQUENCE ("+string+")IN THE PLAN HAS NO SIMPLE TYPES");
                            }
                    }else if (obj instanceof ChoiceType) {

                            System.out.println("CHOICE IN PLAN("+string+")");
                            ChoiceType type=(ChoiceType)map.get(string);
                        if(!type.getElements().isEmpty()){
                            for (Type type2 : type.getElements()) {
                                SimpleTypeImp stype=(SimpleTypeImp)type2;
                                System.out.println(stype.getSimple().getTypeName());
                            }
                        }

                    }else if (obj instanceof AllComplexType) {
                        System.out.println("ALL COMPLEX IN PLAN("+string+")");
                        AllComplexType type=(AllComplexType)map.get(string);
                        if(!type.getElements().isEmpty()){
                            for (Type type2 : type.getElements()) {
                                SimpleTypeImp stype=(SimpleTypeImp)type2;
                                System.out.println(stype.getSimple().getTypeName());
                            }
                        }
                    }
                    }
            }
    }
        System.out.println("...ALL PROCESSING (PARSING & COMPARISON) HAS ENDED...");
    } catch (IOException e) {
    }
	    
  }
  //method to be used in the servlet in the callback method by the thread
  public static  void runTypeEquivalanceAlgorithm(Set<TypeMap> parsedWsdlSet, String plan, PrintWriter out){
	
	  TypeMap planWSDL=null;

	  	for (Iterator<TypeMap> iterator = parsedWsdlSet.iterator(); iterator.hasNext();) {
	  		TypeMap typeMap = iterator.next();  		
	  		if(typeMap.getFileName().equals(plan)){
	  			planWSDL=typeMap;
	  			break;
	  		}	
	  	}  	
	  out.println("<br /> Inside the type Equivalance algorithm");
	  out.println("<br /> Size of Parsed WSDL set "+parsedWsdlSet.size());
  	for (Iterator<TypeMap> iterator = parsedWsdlSet.iterator(); iterator.hasNext();) {
  		
  		TypeMap typeMap = iterator.next();
  		if(plan!=null){
  			Map<String, Type> planTypeMap=planWSDL.getLocalMap();
  			Set<String> keySet=planTypeMap.keySet();
  			out.println("<br /> Obtained Plan in Callback Method "+plan);
  			if(!typeMap.getFileName().equals(plan)){
  				
  				//obtain all other parsed wsdls except the plan
  				Map<String, Type> otherTypes=typeMap.getLocalMap();
  				Set<String> otherTypesKEYS=otherTypes.keySet();
  				for (String key : keySet) {		
  					//get each type in the plan and compare it with each type in the parsed WSDL
						Type planType=planTypeMap.get(key);
						
						for (String otherWSDLKey : otherTypesKEYS) {
							
							Type wsdlType=otherTypes.get(otherWSDLKey);
							out.println("<br /> WSDL Types "+wsdlType.getName());
							if(MappingDictionary.complexTypeEquivalence(planType, wsdlType)){
								
								out.println("<br />Complex ("+planType.getName()+") Matched ("+wsdlType.getName()+
										") From WSDL ("+typeMap.getFileName());
							
							}else if(MappingDictionary.isSimpleComplexTypeEquivalent(planType, wsdlType)){
								
								out.println("<br />Simple Complex ("+planType.getName()+") Matched ("+wsdlType.getName()+
										") From WSDL ("+typeMap.getFileName());
							}else if(MappingDictionary.simpleTypeEquivalence(planType, wsdlType)){
								
								out.println("<br />Simple Simple ("+planType.getName()+") Matched ("+wsdlType.getName()+
										") From WSDL ("+typeMap.getFileName());
							}														
						}
					}
  					}
  				}
  			}
	
		}
  
  public static void processWSDLSet(Set<TypeMap> parsedWsdlSet, String plan){
	  TypeMap planWSDL=null;
  	
  	for (Iterator<TypeMap> iterator = parsedWsdlSet.iterator(); iterator.hasNext();) {
  		TypeMap typeMap = iterator.next();
  		
  		if(typeMap.getFileName().equals(plan)){
  			planWSDL=typeMap;
  			System.out.println("PLAN "+typeMap.getFileName());
  			break;
  		}
		
  	}
  	
  	for (Iterator<TypeMap> iterator = parsedWsdlSet.iterator(); iterator.hasNext();) {
  		
  		TypeMap typeMap = iterator.next();
  		
  		System.out.println(typeMap.getFileName());
  		
  		if(plan!=null){
  			Map<String, Type> planTypeMap=planWSDL.getLocalMap();
  			Set<String> keySet=planTypeMap.keySet();
  			
  			if(!typeMap.getFileName().equals(plan)){
  				//obtain all other parsed wsdls except the plan
  				Map<String, Type> otherTypes=typeMap.getLocalMap();
  				Set<String> otherTypesKEYS=otherTypes.keySet();
  				for (String key : keySet) {
  					
  					//get each type in the plan and compare it with each type in the parsed WSDL
						Type planType=planTypeMap.get(key);
						
						for (String otherWSDLKey : otherTypesKEYS) {
							
							Type wsdlType=otherTypes.get(otherWSDLKey);
							
							if(MappingDictionary.complexTypeEquivalence(planType, wsdlType)){
								log4JExample.log("Complex ("+planType.getName()+") Matched ("+wsdlType.getName()+
										") From WSDL ("+typeMap.getFileName());
							
							}else if(MappingDictionary.isSimpleComplexTypeEquivalent(planType, wsdlType)){
								
								log4JExample.log("Simple Complex ("+planType.getName()+") Matched ("+wsdlType.getName()+
										") From WSDL ("+typeMap.getFileName());
								
							}else if(MappingDictionary.simpleTypeEquivalence(planType, wsdlType)){
								log4JExample.log("Simple Simple ("+planType.getName()+") Matched ("+wsdlType.getName()+
										") From WSDL ("+typeMap.getFileName());							
							}														
						}
					}
  			}
  		}
  	}
  }
  public  TypeMap processWSDL(String url, Map<String, Type> localMap){
	  TypeMap typemap=null;
	  SAXBuilder parser = new SAXBuilder();
	  Document document;
	  try {
		  document = parser.build(url);
		  process(document.getRootElement(), localMap);
		} catch (JDOMException e) {
			e.printStackTrace();
		} 
		/*
    	Map<String, Type> map= getTypeMap();
    	    
    	if(!map.isEmpty()){
    	 Set<String> keys=map.keySet();
    	   for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
    			String string = (String) iterator.next();
    				//System.out.println("KEY "+string);
    			Type type=map.get(string);
    			displaySequenceTypes(type);
    			displayChoiceTypes(type);
    			displaySimpleTypes(type);
    			}
    	 	}
    	 	
    	typemap=new TypeMap(url, map);
    	*/
    	parser=null;
    	document=null;
    	return typemap;
  }
  //compare all the Types acquired from the WSDL
  public  void compareTypes(Map<String, Type> map){
	  if(!map.isEmpty()){
		  
	    	Set<String> keys=map.keySet();
	    	Type type =  map.get("GetLargestDeclines");
	    	SequenceType one = (SequenceType) type;
	    	for (String key : keys) {
	    		//if(key.equals("Variation")){
	    			Type type2 =  map.get(key);
			    	if (type2 instanceof SequenceType) {
			    		
			    		SequenceType complexType2 = (SequenceType) type2;
			    	}
	    		
	    	}
	    }
}
  
//handle all SequenceTypes in the wsdl
  /*
  public  void displaySequenceTypes(Object object){
	  if(object!=null && object instanceof SequenceType) {

			SequenceType type = (SequenceType) object;
			List<Type> list=type.getElements();
				
			if(!list.isEmpty()){
				for (Type type2 : list) {
					SimpleTypeImp simpleType=(SimpleTypeImp)type2;
					
					if(simpleType.getSimple().getTypeName()!=null){
						System.out.println( "Sequence Name ("+type.getName()+")" +
								" Element Type("+simpleType.getSimple().getTypeName()+")");
					}else{
						System.out.println("User Defined Element in Sequence Named "+type.getName());
					}
				}
			}else{
				System.out.println("No Elements in the sequence "+type.getName());
			}
		}
  }
  
  public  void displayChoiceTypes(Object object){
	  
	  if(object!=null && object instanceof ChoiceType) {

			ChoiceType type = (ChoiceType) object;
			List<Type> set=type.getElements();
			
			
			if(!set.isEmpty()){
				for (Type type2 : set) {
					SimpleTypeImp simpleType=(SimpleTypeImp)type2;
					if(simpleType.getSimple()!=null){
						
						System.out.println( "Choice Type Name ("+type.getName()+")" +
								" Element ("+simpleType.getSimple().getTypeName()+")");
					}else{
						System.out.println("User defined Element Simple Type  for Choice Type "+type.getName());
					}
					
				}
					
			}else{
				System.out.println("No Elements in the Choice Type "+type.getName());
			}
		}
  }
  public void displaySimpleTypes(Object object){
	  if(object!=null && object instanceof SimpleType) {

			SimpleTypeImp type = (SimpleTypeImp) object;
			
			Simple simple=type.getSimple();
			if(simple!=null){
				System.out.println("Simple Type "+simple);
			}else{
				System.out.println("User defined Simple Type ");
			}
			
		}
  }
  public void displayAllComplexTypes(Object object){
	  if(object!=null && object instanceof AllComplexType) {

			AllComplexType type = (AllComplexType) object;
			
			List<Type> set=type.getElements();
			if(!set.isEmpty()){
				for (Type type2 : set) {
					SimpleTypeImp simpleType=(SimpleTypeImp)type2;
					System.out.println( "All Complex Type Name ("+type.getName()+")" +
							" Element ("+simpleType.getSimple().getTypeName()+")");	
				}
					
			}else{
				System.out.println("No Elements in the All Complex Type "+type.getName());
			}
		}
  }
  */
  public  void setLocalMap(Map<String, Type> localMap) {
	this.localMap = localMap;
  }
}
