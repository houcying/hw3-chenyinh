package edu.cmu.deiis.annotators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.deiis.types.*;

public class TestElementAnnotator extends JCasAnnotator_ImplBase{
  //the question pattern
  private Pattern questionPattern = 
          Pattern.compile("Q.*[?]");
  //the answer pattern
  private Pattern answerPattern = 
          Pattern.compile("A.*[.]");
  

    public void process(JCas aJCas) throws AnalysisEngineProcessException{
      // get document text
      String docText = aJCas.getDocumentText();
      // search for Questions
      Matcher matcher = questionPattern.matcher(docText);
      int pos = 0;
      while (matcher.find(pos)) {
        // found one - create a Question
        Question annotation = new Question(aJCas);
        annotation.setBegin(matcher.start()+2);
        annotation.setEnd(matcher.end());
        annotation.setCasProcessorId("TestElementAnnotator");
        annotation.setConfidence(1.0);
        annotation.addToIndexes();
        pos = matcher.end();
      }
      // search for Answers
      matcher = answerPattern.matcher(docText);
      pos = 0;
      while (matcher.find(pos)) {
        // found one - create answer
        Answer annotation = new Answer(aJCas);
        if(docText.charAt((matcher.start()+2)) == '0')
        {
          annotation.setIsCorrect(false); 
        }else
        {
          annotation.setIsCorrect(true);
        }
        annotation.setBegin(matcher.start()+4);
        annotation.setEnd(matcher.end());
        annotation.setCasProcessorId("TestElementAnnotator");
        annotation.setConfidence(1.0);
        annotation.addToIndexes();
        pos = matcher.end();
      }
      
    }

}