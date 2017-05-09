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
 ******************************************************************************/

package jsonxmihandler;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.TagsetDescription;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

import webanno.custom.Page;
import webanno.custom.Section;
import webanno.custom.SemanticLayer;

@TypeCapability(
        inputs={
                "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData"})

/**
 * UIMA CAS consumer writing the CAS document text in custom format.
 * 
 * Code adapted from TextWriter. https://github.com/dkpro/dkpro-core.
 * Original author Richard Eckart de Castilho. Ubiquitous Knowledge Processing (UKP) Lab. Technische Universität Darmstadt.
 *
 * @author Alicia Gonzalez Martinez
 */
public class JsonWriter extends JCasFileWriter_ImplBase {

	
    public static final String PARAM_FILENAME_SUFFIX = "filenameSuffix";
    
    @ConfigurationParameter(name = PARAM_FILENAME_SUFFIX, mandatory = true, defaultValue = ".json")
    private String filenameSuffix;    
    
    private static String KEY_CONTENT;
    
    private static String KEY_PERSONS;
    private static String KEY_MOTIVES;
    private static String KEY_METAMOTIVES;
    private static String KEY_ERRORS;
    
    private static String ANN_KEY_VALUE;
    private static String ANN_KEY_START;
    private static String ANN_KEY_END;

    /**
     * Load constants
     */
    private static void loadConfig() throws IOException {

            File configFile = new File("config.properties");
            FileReader configReader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(configReader);

            KEY_CONTENT = props.getProperty("KEYNAME_CONTENT");
            KEY_PERSONS = props.getProperty("KEYNAME_NE_PERSONS");
            KEY_MOTIVES = props.getProperty("KEYNAME_MOTIVES");
            KEY_METAMOTIVES = props.getProperty("KEYNAME_METAMOTIVES");
            KEY_ERRORS = props.getProperty("KEYNAME_ERRORS");
            ANN_KEY_VALUE = props.getProperty("ANNOTATION_KEYNAME_VALUE");
            ANN_KEY_START = props.getProperty("ANNOTATION_KEYNAME_START");
            ANN_KEY_END = props.getProperty("ANNOTATION_KEYNAME_END");
    }

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
    	
		try {
			loadConfig();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	JSONObject outjson = new JSONObject();
    	    	
    	JSONArray persons = new JSONArray();
    	JSONArray motives = new JSONArray();
    	JSONArray metamotives = new JSONArray();
    	JSONArray errors = new JSONArray();
    	
    	JSONObject annInstance = new JSONObject();  
    	
        OutputStream docOS = null;
        try {
            docOS = getOutputStream(aJCas, filenameSuffix);
            Iterator<JCas> viewIterator = aJCas.getViewIterator();
            
            //System.out.println("\u001B[32m" + aJCas.getDocumentAnnotationFs() + "\u001B[0m"); //DEBUG
            
			outjson.put(KEY_CONTENT, aJCas.getDocumentText());
            
        	while(viewIterator.hasNext())
        	{
        		JCas view = viewIterator.next();
        		FSIterator<org.apache.uima.jcas.tcas.Annotation> fsi = view.getAnnotationIndex().iterator();
        		
        		while(fsi.hasNext()) {
        			AnnotationFS a = fsi.next();

        			// skip useless info
        			if (a instanceof POS || a instanceof Token || a instanceof Sentence || a instanceof TagsetDescription
                        || a instanceof DocumentMetaData) {
        				continue;
        			}
        			
        			// skip from now
        			if (a instanceof Page || a instanceof Section) {
        				continue;
        			}
        			
        			if (a instanceof NamedEntity) {
        				
        		    	annInstance.put(ANN_KEY_VALUE, ((NamedEntity)a).getCoveredText());
        		    	annInstance.put(ANN_KEY_START, a.getBegin());
        		    	annInstance.put(ANN_KEY_END, a.getEnd());
        		    	persons.add(annInstance.clone());
        				
        			} else if (a instanceof SemanticLayer ) {
        				
        				if (((SemanticLayer)a).getError() != null) {
        					
            		    	annInstance.put(ANN_KEY_VALUE, ((SemanticLayer)a).getError());
            		    	annInstance.put(ANN_KEY_START, a.getBegin());
            		    	annInstance.put(ANN_KEY_END, a.getEnd());
            		    	errors.add(annInstance.clone());
        					
        				} else if (((SemanticLayer)a).getMotives() != null) {
        					
            		    	annInstance.put(ANN_KEY_VALUE, ((SemanticLayer)a).getMotives());
            		    	annInstance.put(ANN_KEY_START, a.getBegin());
            		    	annInstance.put(ANN_KEY_END, a.getEnd());
            		    	motives.add(annInstance.clone());

        				} else if (((SemanticLayer)a).getMetamotives() != null) {
        					
            		    	annInstance.put(ANN_KEY_VALUE, ((SemanticLayer)a).getMetamotives());
            		    	annInstance.put(ANN_KEY_START, a.getBegin());
            		    	annInstance.put(ANN_KEY_END, a.getEnd());
            		    	metamotives.add(annInstance.clone());

        				} else {
        					
        					//FIXME this is suppose to be an error in the workflow! needs more discussion about what to do
        					System.err.println("Warning! Subtype of Semantic layer not recognized: " + a + "File name: " + this.getRelativePath(aJCas) + 
        							"\nText included in the annotation = " + a.getCoveredText());
        					System.err.println("\u001B[31mFATAL ERROR: one or more annotation instances haven't been included in the output file because they lack some information. Fix your errors!\u001B[0m");
        				}

        			} else {
        				System.err.println("\u001B[FATAL ERROR! Annotation instance not recognized: " + a + "File name: " + this.getRelativePath(aJCas) + "\u001B[0m\n");
        			}
        		}
        	}
        	outjson.put(KEY_PERSONS, persons);
			outjson.put(KEY_MOTIVES, motives);
			outjson.put(KEY_METAMOTIVES, metamotives);
			outjson.put(KEY_ERRORS, errors);
			
        	IOUtils.write(outjson.toJSONString(), docOS);
        }
        
        catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }
        
        finally {
            closeQuietly(docOS);
        }
    }        
}



