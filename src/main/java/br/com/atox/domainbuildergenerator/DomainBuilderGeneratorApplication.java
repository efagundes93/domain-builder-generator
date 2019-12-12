package br.com.atox.domainbuildergenerator;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.common.io.Files;

/**
 * Wellcome to the Jungle!
 * Geralmente acabava criando builders na mão, ou então usando o plugin spark
 * e tendo de desacoplar o buider gerado na mão, até que certo dia precisei gerar builders 
 * de 35 classes, foi então que decidi criar essa belezinha ;)
 * 
 * 
 * Esta classe feia, porém funcional visa auxiliar na geração de classes builders
 * para dominios java de forma desacoplada em um package especifico. 
 * 
 * Está perfeita? Não. Existem diversas melhorias que podem ser feitas, 
 * porém por hora atende o escopo que defini.
 * 
 * - Cria um diretório /builder com base no arg[1]
 * Para cada classe contida em um classPath:
 * 
 * - Gera-se um arquivo . java cujo nome é nomedaclasse + Builder;
 * - Adiciona os imports;
 * - Declara a entidade;
 * - Para cada atributo desta entidade gera-se um metodo de atribuição com o prefixo "with";
 * - Gera o método de build que retorna a nova instancia da entidade; 
 * 
 * 
 * @author Emiliano Fagundes - SouthSystem -ef69036
 *
 */
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
	private static final String 	BUILD  = "build" ;

	
	private static Map<String,String> dic = Stream.of(new String[][] {
		  { "java.lang.String", "random(12, true, false)"}, 
		  {"java.lang.Long", "new Long(random(8, false, true))"}, 
		  {"java.math.BigDecimal", "new BigDecimal(random(10, false, true))"}, 
		  {"java.time.LocalDateTime", "LocalDateTime.now()"}
		}).collect(Collectors.toMap(data -> data[0], data -> data[1]));
	


	public static String getRandomicValueFromType(String type) {
		
		String value = dic.get(type);
		return  null == value ? "null" : value;
	
		
	}

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
																	+SPACE + EQUAL + SPACE + NEW + SPACE + domainName + SPACE + OPEN_PARENTESIS 
																	+ CLOSE_PARENTESIS  + SEMICOLON + NEW_LINE +TAB+ CLOSE_BRACKET;
					
					
					
					
					final String withOtherConstructor =  TAB+PUBLIC_MODIFIER + SPACE + builderClassName + OPEN_PARENTESIS + domainName + SPACE +  domainName.substring(0,1).toLowerCase()+ domainName.substring(1) 
																				+ CLOSE_PARENTESIS + OPEN_BRACKET +NEW_LINE
																				+TAB+TAB+ THIS  + DOT+ ENTITY 
																				+SPACE + EQUAL + SPACE + domainName.substring(0,1).toLowerCase()+ domainName.substring(1)  
																				+ SEMICOLON + NEW_LINE +TAB+ CLOSE_BRACKET;
					
					
					Set<String> imports = new HashSet<String>();
					
					imports.add(IMPORT + SPACE +  javaClass.getPackageName()+DOT+domainName + SEMICOLON + NEW_LINE);
					imports.add("import static org.apache.commons.lang3.RandomStringUtils.random;"+ NEW_LINE);
				

					StringBuilder anyMethod = new StringBuilder(TAB+PUBLIC_MODIFIER + SPACE + builderClassName +SPACE +"any"+OPEN_PARENTESIS + CLOSE_PARENTESIS +OPEN_BRACKET+NEW_LINE+TAB+TAB+RETURN+SPACE +THIS);
					
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
								TAB + CLOSE_BRACKET+NEW_LINE+NEW_LINE;						
						
						anyMethod.append(DOT+WITH+fieldName.substring(0,1).toUpperCase()+ fieldName.substring(1) + OPEN_PARENTESIS+getRandomicValueFromType(field.getType().toString())+CLOSE_PARENTESIS + NEW_LINE + TAB + TAB + TAB + TAB+TAB);
						withMethods.add(method);
					}
				
					anyMethod.append(SEMICOLON+NEW_LINE+TAB+CLOSE_BRACKET);
					
					String buildMethod = TAB  +  PUBLIC_PREFIX + 
							domainName +
							SPACE +
							BUILD +
							OPEN_PARENTESIS +
							CLOSE_PARENTESIS + 
							OPEN_BRACKET + 
							NEW_LINE +
							TAB+TAB  + RETURN +
							SPACE +
							THIS+DOT+ENTITY+SEMICOLON +
							NEW_LINE + 
							TAB + CLOSE_BRACKET+NEW_LINE;
					
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
										 .append(NEW_LINE)
										 .append(withOtherConstructor)
										 .append(NEW_LINE);
					
				
					withMethods.forEach( meth -> builderSource.append(meth) );
					builderSource.append(anyMethod.toString())
										 .append(NEW_LINE)
										 .append(NEW_LINE)
										 .append(buildMethod)
										 .append(NEW_LINE)
										 .append(CLOSE_BRACKET);

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
