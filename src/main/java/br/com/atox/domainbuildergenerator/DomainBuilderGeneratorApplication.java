package br.com.atox.domainbuildergenerator;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.common.io.Files;


@SpringBootApplication
public class DomainBuilderGeneratorApplication {

	private static final String JAVA_EXTENSION = "java";
	private static final String CLASS_EXTENSION = "class";
	private static final String OPEN_BRACKET = "{";
	private static final String CLOSE_BRACKET = "}";
	private static final String SPACE = " ";
	private static final String NEW_LINE = System.lineSeparator();
	private static final String OPEN_PARENTESIS = "(";
	private static final String CLOSE_PARENTESIS = ")";
	private static final String PUBLIC_MODIFIER = "public";
	private static final String PRIVATE_MODIFIER = "private";
	private static final String PUBLIC_PREFIX = PUBLIC_MODIFIER + SPACE ;
	private static final String 	WITH = "with" ;
	private static final String 	RETURN = "return" ;
	private static final String 	THIS = "this" ;
	private static final String 	PACKAGE = "package" ;
	private static final String 	SEMICOLON = ";" ;
	private static final String 	ENTITY = "entity" ;
	private static final String 	DOT = "." ;
	private static final String 	EQUAL  = "=" ;
	private static final String 	NEW  = "new" ;
	private static final String 	TAB  = "\t" ;
	private static final String 	IMPORT  = "import" ;



	public static void main(String[] args) {
		
		
		SpringApplication.run(DomainBuilderGeneratorApplication.class, args);
		
		String domainClassPath = args[0];
		File domainClasspathDirectory = new File(domainClassPath);
		String builderDirectoryPath = args[1].concat("\\builder");
		File 	builderDirectory = new File(builderDirectoryPath);
		builderDirectory.mkdir();		
		
		String [] directoryContents = domainClasspathDirectory.list();		

		for(String fileName: directoryContents) {
		    
		    String extension = Files.getFileExtension(fileName);
		      
		    if(CLASS_EXTENSION.equals(extension) && !fileName.contains("$")) {

		    	try {
		    		
		    		File javaFile = new File(domainClasspathDirectory+"\\"+fileName);
		    
		    		String domainName = fileName.replace(".".concat(CLASS_EXTENSION), "");
					String builderClassName = domainName.concat("Builder");
					JavaClass javaClass = new ClassParser(javaFile.getPath()).parse();

					String builderPackage = javaClass.getPackageName().concat(".builder");
							
							
					String builderFileClassName =builderClassName.concat("."+JAVA_EXTENSION);
					
		    		
					final String builderClassHeader = PACKAGE + SPACE +builderPackage + SEMICOLON;
					final String classDeclaration = PUBLIC_MODIFIER + SPACE + CLASS_EXTENSION +SPACE  +builderClassName+OPEN_BRACKET;
					final String entityDeclaration = PRIVATE_MODIFIER + SPACE + domainName + SPACE + ENTITY + SEMICOLON ;
					
					final String voidConstructor =  TAB+PUBLIC_MODIFIER + SPACE + builderClassName + OPEN_PARENTESIS
																	+ CLOSE_PARENTESIS + OPEN_BRACKET +NEW_LINE
																	+TAB+TAB+ THIS  + DOT+ ENTITY 
																	+SPACE + EQUAL + SPACE + NEW + domainName + SPACE + OPEN_PARENTESIS 
																	+ CLOSE_PARENTESIS  + SEMICOLON + NEW_LINE +TAB+ CLOSE_BRACKET;
					
					
					
					
					
					Set<String> imports = new HashSet<String>();
					Set<String> withMethods = new HashSet<String>();

					for (Field field : javaClass.getFields()){
						String fieldName = field.getName();
						
						if("serialVersionUID".equals(fieldName)) {
							continue;
						}
						String fieldTypeString = field.getType().toString();
						
						String[] splitted = fieldTypeString.split("\\.");
						String fieldTypeSimpleName = splitted[splitted.length - 1];
						fieldTypeString = IMPORT + SPACE + fieldTypeString + SEMICOLON+NEW_LINE;
						imports.add(fieldTypeString);
						
						String method = TAB  +  PUBLIC_PREFIX + 
								builderClassName +
								SPACE +
								WITH + 
								fieldName.substring(0,1).toUpperCase()+ fieldName.substring(1) +
								OPEN_PARENTESIS +
								fieldTypeSimpleName + 
								SPACE + 
								fieldName + 
								CLOSE_PARENTESIS + 
								OPEN_BRACKET + 
								NEW_LINE +
								TAB+TAB +THIS + DOT + ENTITY + DOT + "set"+fieldName.substring(0,1).toUpperCase()+ fieldName.substring(1) +
								OPEN_PARENTESIS + fieldName+CLOSE_PARENTESIS + SEMICOLON +
								NEW_LINE +
								TAB+TAB  + RETURN +
								SPACE +
								THIS+SEMICOLON +
								NEW_LINE + 
								TAB + CLOSE_BRACKET+NEW_LINE;
						withMethods.add(method);
					}
					
					StringBuilder builderSource = new StringBuilder();
					builderSource.append(builderClassHeader)
										 .append(NEW_LINE)
										 .append(NEW_LINE);
					imports.forEach(imp -> builderSource.append(imp));
					builderSource.append(NEW_LINE)
										 .append(NEW_LINE)
										 .append(classDeclaration)
										 .append(NEW_LINE)
										 .append(NEW_LINE)
										 .append(TAB)
										 .append(entityDeclaration)
										 .append(NEW_LINE)
										 .append(NEW_LINE)
										 .append( voidConstructor)
										 .append(NEW_LINE);
					
				
					withMethods.forEach( meth -> builderSource.append(meth) );
					builderSource.append(CLOSE_BRACKET);
					FileWriter myWriter = new FileWriter(builderDirectory+"\\"+builderFileClassName);
		    		myWriter.write(builderSource.toString());
		    	    myWriter.close();
					
		    	} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		    }
		}
		
	
	}

}
