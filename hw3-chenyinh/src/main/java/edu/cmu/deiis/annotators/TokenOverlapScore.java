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

public class TokenOverlapScore extends JCasAnnotator_ImplBase {

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

    while (itQst.hasNext()) {
      Question qst = (Question) itQst.next();

      /* QstArray contains all tokens in the question qst */
      ArrayList<String> QstArray = new ArrayList<String>();
      AnnotationIndex<Annotation> idxt = aJCas.getAnnotationIndex(Token.type);
      FSIterator<Annotation> itt = idxt.iterator();

      while (itt.hasNext()) {
        Token tk = (Token) itt.next();
        /* if the token's span is within the question's span, than add this token to QstArray */
        if ((tk.getBegin() >= qst.getBegin()) && (tk.getEnd() <= qst.getEnd())) {
          String s = docText.substring(tk.getBegin(), tk.getEnd());
          QstArray.add(s);
        }
      }

      int pos = qst.getEnd();
      while (itAns.hasNext()) {
        while (docText.startsWith("A", pos + 2)) {
          
          Answer ans = (Answer) itAns.next();
          /* AnsArray contains all tokens in the answer ans */
          ArrayList<String> AnsArray = new ArrayList<String>();

          AnnotationIndex<Annotation> idxtk1 = aJCas.getAnnotationIndex(Token.type);
          FSIterator<Annotation> itt1 = idxtk1.iterator();

          while (itt1.hasNext()) {
            Token tk1 = (Token) itt1.next();
            /* if the token's span is within the answer's span, add this token to AnsArray */
            if ((tk1.getBegin() >= ans.getBegin()) && (tk1.getEnd() <= ans.getEnd())) {
              String s = docText.substring(tk1.getBegin(), tk1.getEnd());
              AnsArray.add(s);
            }
          }

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
          annotation.setCasProcessorId("TokenOverlapScore");
          annotation.setConfidence(1);
          annotation.setScore(score);
          annotation.addToIndexes();

          pos = ans.getEnd();

        }
      }
    }

  }

}