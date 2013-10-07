package edu.cmu.deiis.annotators;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.*;

public class TokenUniGramAnnotator extends JCasAnnotator_ImplBase{
  
  
    /*UniGram can be get directly with Token*/
    public void process(JCas aJCas) throws AnalysisEngineProcessException{
   
      AnnotationIndex<Annotation> idxt = aJCas.getAnnotationIndex(Token.type);
      // get token index and get an iterator
      FSIterator<Annotation> it = idxt.iterator();
      
      while(it.hasNext())
      {
        Token t = (Token)it.next();
        NGram annotation = new NGram(aJCas);
        FSArray uniarray = new FSArray(aJCas, 1);
        uniarray.set(0, t);
        annotation.setBegin(t.getBegin());
        annotation.setEnd(t.getEnd());
        annotation.setElements(uniarray);
        annotation.setElementType("edu.cmu.deiis.types.Token");
        annotation.setCasProcessorId("TokenUniGramAnnotator");
        annotation.setConfidence(1.0);
        annotation.addToIndexes();
      }
    }

}
