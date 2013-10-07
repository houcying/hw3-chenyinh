package edu.cmu.deiis.annotators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.*;


public class TokenAnnotator extends JCasAnnotator_ImplBase{
  //the token pattern also consider the condition of 's
  private Pattern tokenPattern = 
          Pattern.compile("\\w+[']?\\w+");
  

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
      //get the question index
     AnnotationIndex<Annotation> idxq = aJCas.getAnnotationIndex(Question.type);
      // get the question iterator
     FSIterator<Annotation> iq = idxq.iterator();
    
     while(iq.hasNext())
     {
       Question q = (Question)iq.next();
       Matcher matcherq = tokenPattern.matcher(q.getCoveredText());
       
       int pos = 0;
       while (matcherq.find(pos)) {
         // found one - create token annotation
         Token annotation = new Token(aJCas);
         annotation.setBegin(matcherq.start()+q.getBegin()); /* offset adding the matcher position*/
         annotation.setEnd(matcherq.end()+q.getBegin());
         annotation.setCasProcessorId("TokenAnnotator");
         annotation.setConfidence(1.0);
         annotation.addToIndexes();
         pos = matcherq.end();
       }
     }
      // get the answer index
      AnnotationIndex<Annotation> idxa = aJCas.getAnnotationIndex(Answer.type);
       // get the answer iterator
      FSIterator<Annotation> ia = idxa.iterator();
     
     
      while(ia.hasNext())
      {
        Answer a = (Answer)ia.next();
        Matcher matchera = tokenPattern.matcher(a.getCoveredText());
        int pos = 0;
        while (matchera.find(pos)) {
          // found one - create token annotation
          Token annotation = new Token(aJCas);
          annotation.setBegin(matchera.start()+a.getBegin());
          annotation.setEnd(matchera.end()+a.getBegin());
          annotation.setCasProcessorId("TokenAnnotator");
          annotation.setConfidence(1.0);
          annotation.addToIndexes();
          pos = matchera.end();
        }
     }
   }// TODO Auto-generated method stub
    
  
  
  }
 
