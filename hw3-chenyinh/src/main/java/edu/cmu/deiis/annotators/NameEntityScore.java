/*
 * NameEntity Scoring generates score for each answer by calculating overlap among same mention type
 * */
package edu.cmu.deiis.annotators;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.cleartk.ne.type.NamedEntity;
import org.cleartk.ne.type.NamedEntityMention;

import edu.cmu.deiis.types.*;
import edu.stanford.nlp.util.Index;

public class NameEntityScore extends JCasAnnotator_ImplBase {

  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    String docText = aJCas.getDocumentText();

    /*
     * get question iterator and answer iterator to finds tokens related with each question or
     * answer
     */
    
    AnnotationIndex<Annotation> idxQst = aJCas.getAnnotationIndex(Question.type);
    FSIterator<Annotation> itQst = idxQst.iterator();

    AnnotationIndex<Annotation> idxtAns = aJCas.getAnnotationIndex(Answer.type);
    FSIterator<Annotation> itAns = idxtAns.iterator();
    
    
    while(itQst.hasNext())
    {
      /*get one question from question index */
      Question qst = (Question) itQst.next();
      ArrayList<String> QstArray = new ArrayList<String>();
      FSIndex  nameEntityindexq = aJCas.getAnnotationIndex(NamedEntityMention.type);
      FSIterator neitq =  nameEntityindexq.iterator();
      
      /*get mentions in the same mention type in a question */
      while (neitq.hasNext()) {
        NamedEntityMention mentionq = (NamedEntityMention) neitq.next();
        if((mentionq.getBegin() >= qst.getBegin()) && (mentionq.getEnd() <= qst.getEnd()))
        {
          if(mentionq.getMentionType().equals("PERSON"))
          {
            QstArray.add(docText.substring(mentionq.getBegin(), mentionq.getEnd()));
          }
        }
        
      
      while(itAns.hasNext())
      { 
        /*get one answer from answer index */
        Answer ans = (Answer) itAns.next();
        ArrayList<String> AnsArray = new ArrayList<String>();
        FSIndex  nameEntityindexa = aJCas.getAnnotationIndex(NamedEntityMention.type);
        FSIterator neita =  nameEntityindexa.iterator();
        /*get mentions in the same mention type in an answer */
        while (neita.hasNext()) {
          NamedEntityMention mentiona = (NamedEntityMention) neita.next();
       
          if((mentiona.getBegin() >= ans.getBegin()) && (mentiona.getEnd() <= ans.getEnd()))
          {
            if(mentiona.getMentionType()!=null)
            {
              if(mentiona.getMentionType().equals("PERSON"))
              {
                AnsArray.add(docText.substring(mentiona.getBegin(), mentiona.getEnd()));
              }
            }
            
          }
        }
        
        /*compute the score*/
        float counter = 0; //counter represents how many tokens matches between quesiton and answer
        float score = 0;
        for (int i = 0; i < QstArray.size(); i++) {
          if (AnsArray.contains(QstArray.get(i))) {
            counter++;
          }
        }
        
        score = (float) (counter / (float) AnsArray.size());
        /*generate an answerscore annotation*/
        AnswerScore annotation = new AnswerScore(aJCas);
        annotation.setAnswer(ans);
        annotation.setBegin(ans.getBegin());
        annotation.setEnd(ans.getEnd());
        annotation.setCasProcessorId("NameEntityScore");
        annotation.setConfidence(1);
        annotation.setScore(score);
        annotation.addToIndexes();
      }
     } 

  }
}
}