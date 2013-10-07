package edu.cmu.deiis.annotators;


import java.util.ArrayList;
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

public class NGramScore extends JCasAnnotator_ImplBase{
  

    public void process(JCas aJCas) throws AnalysisEngineProcessException{

      
      String docText = aJCas.getDocumentText();
      /*
       * get question iterator and answer iterator to find ngrams related with each question or
       * answer
       */
      AnnotationIndex<Annotation> idxQst = aJCas.getAnnotationIndex(Question.type);
      FSIterator<Annotation> itQst = idxQst.iterator();
      
      AnnotationIndex<Annotation> idxtAns = aJCas.getAnnotationIndex(Answer.type);
      FSIterator<Annotation> itAns = idxtAns.iterator();
      
      
      
      while(itQst.hasNext())
      {
        Question qst = (Question)itQst.next();
        /* QstArray contains all ngrams in the question qst */
        ArrayList<String> QstArray = new ArrayList<String>();
        AnnotationIndex<Annotation> idxNgrm = aJCas.getAnnotationIndex(NGram.type);
        FSIterator<Annotation> itNgrm = idxNgrm.iterator();
        
        while(itNgrm.hasNext())
        {
          NGram ngrm = (NGram)itNgrm.next();
          /* if the ngram's span is within the question's span, than add this ngram's to QstArray */
          if ((ngrm.getBegin() >= qst.getBegin()) 
                  && (ngrm.getEnd() <= qst.getEnd()))
          {
            String s = docText.substring(ngrm.getBegin(), ngrm.getEnd());
            QstArray.add(s);
          }
        }
        
        
        int pos = qst.getEnd();
        while(itAns.hasNext())
        {
        while(docText.startsWith("A", pos + 2))
        {
          /* AnsArray contains all ngrams in the answer ans */
          Answer ans = (Answer)itAns.next();
          ArrayList<String> AnsArray = new ArrayList<String>();
          
          AnnotationIndex<Annotation> idxNgrm1 = aJCas.getAnnotationIndex(NGram.type);
          FSIterator<Annotation> itNgrm1 = idxNgrm1.iterator();
          
          while(itNgrm1.hasNext())
          {
            NGram ngrm = (NGram)itNgrm1.next();
            /* if the ngram's span is within the answer's span, add this ngram to AnsArray */
            if ((ngrm.getBegin() >= ans.getBegin()) 
                    && (ngrm.getEnd() <= ans.getEnd()))
            {
              String s = docText.substring(ngrm.getBegin(), ngrm.getEnd());
              AnsArray.add(s);
            }
          }
          
          float counter = 0; //counter represents how many ngrams matches between quesiton and answer
          float score = 0;
          for(int i = 0; i < QstArray.size(); i++)
          {
            if(AnsArray.contains(QstArray.get(i)))
            {
              counter++;
            }
          }
          score = (float)(counter/(float)AnsArray.size());
          
          AnswerScore annotation = new AnswerScore(aJCas);
          annotation.setAnswer(ans);
          annotation.setBegin(ans.getBegin());
          annotation.setEnd(ans.getEnd());
          annotation.setCasProcessorId("NgramScore");
          annotation.setConfidence(1);
          annotation.setScore(score);
          annotation.addToIndexes();
          
           
          pos = ans.getEnd();
          
        }
      }
      }
      
    }

}

