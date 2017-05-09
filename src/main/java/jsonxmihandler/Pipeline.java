/******************************************************************************
 * Copyright 2017 Alicia González Martínez
 * COBHUNI Project, Universität Hamburg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * usage:
 *   $ java -cp target/dependency/*:target/jsonxmihandler-0.0.1-SNAPSHOT.jar jsonxmihandler.Pipeline \
 *      -i ../../data/files/inprogress/altafsir/ -o ../../data/files/prepared/altafsir/ --to json
 * 
 ******************************************************************************/

package jsonxmihandler;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;


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
			 //TODO to add more formats, just in case...
		 }
		 
	}


}
