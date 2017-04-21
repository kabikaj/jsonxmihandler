package jsonxmihandler;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiWriter;

import de.tudarmstadt.ukp.dkpro.core.io.xml.XmlReader;
import de.tudarmstadt.ukp.dkpro.core.io.xml.XmlWriterInline;

import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextWriter;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/* 
 * usage:
 *   $ java -cp target/dependency/*:target/jsonxmihandler-0.0.1-SNAPSHOT.jar jsonxmihandler.Pipeline \
 *      -i ../../data/files/inprogress/altafsir/ -o ../../data/files/prepared/altafsir/ --to json
 */

public class Pipeline {
	
	static public String JSON = "json";
	static public String XMI = "xmi"; 
	
	static public class FormatAllowed implements IParameterValidator {
		public void validate(String name, String value) throws ParameterException {
			if (!value.equals(JSON) && !value.equals(XMI)) {
				throw new ParameterException("Parameter " + name + " must be \"" + JSON + "\" or \"" + XMI + "\" (found " + value +")");
			}
		}
	}
	
	@Parameter(names={"--inpath", "-i"}, required=true, description="input directory to read files")
	String inpath;
	
	@Parameter(names={"--outpath", "-o"}, required=true, description="output directory to dump files")
    String outpath;
	
	@Parameter(names = "--to", required=true, validateWith=FormatAllowed.class, description="output format")
	String writer;


	public static void main(String ... args) throws Exception {
		
		 Pipeline pipeline = new Pipeline();
		 new JCommander(pipeline, args);
		 
		 if (pipeline.writer.equals(JSON)) {
			 
			 /*System.out.println("[1]" + pipeline.inpath);  //DEBUG
			 
			 File[] files = new File(pipeline.inpath).listFiles();

			 for (File file : files)
			 {
				 System.out.println("[2]" + pipeline.outpath+"/"+file.getName());  //DEBUG
				 if (file.isFile())
				 {
					 System.out.println("File " + file.getAbsolutePath());
					 
					 runPipeline(
							 createReaderDescription(XmiReader.class,
									 				 XmiReader.PARAM_SOURCE_LOCATION, file.getAbsolutePath(),
									 				 XmiReader.PARAM_LANGUAGE, "en",
									 				 XmiReader.PARAM_LENIENT, true),
				        
							 createEngineDescription(JsonWriter.class,
				        		                	 JsonWriter.PARAM_STRIP_EXTENSION, true,
				        		                	 JsonWriter.PARAM_TARGET_LOCATION, (String) pipeline.outpath+"/"+file.getName(), 
							                         JsonWriter.PARAM_USE_DOCUMENT_ID, false));   //DEBUG checking if it fixes the name of the outfile
				 }
				
			 }*/
		 		 		
			 runPipeline(
					 createReaderDescription(XmiReader.class,
							 				 XmiReader.PARAM_SOURCE_LOCATION, pipeline.inpath,
							 				 XmiReader.PARAM_LANGUAGE, "en",
							 				 XmiReader.PARAM_LENIENT, true),
		        
					 createEngineDescription(JsonWriter.class,
		        		                	 JsonWriter.PARAM_STRIP_EXTENSION, true,
		        		                	 JsonWriter.PARAM_TARGET_LOCATION, pipeline.outpath));
			 
		 }
		 else {
			 //TODO probably not...
		 }
		 
	}


}
