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

public class TokenTriGramAnnotator extends JCasAnnotator_ImplBase {

  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    String docText = aJCas.getDocumentText();

    /*
     * use three tokens iterators get the 3-gram, first iterator points to the first token of the
     * 2-gram and the second iterator points to the second token, the third iterator points to the
     * third token
     */

    AnnotationIndex<Annotation> idxt = aJCas.getAnnotationIndex(Token.type);
    FSIterator<Annotation> it = idxt.iterator();

    AnnotationIndex<Annotation> idxt2 = aJCas.getAnnotationIndex(Token.type);
    FSIterator<Annotation> it2 = idxt2.iterator();

    AnnotationIndex<Annotation> idxt3 = aJCas.getAnnotationIndex(Token.type);
    FSIterator<Annotation> it3 = idxt3.iterator();

    Token t2 = (Token) it2.next();

    it3.next();
    Token t3 = (Token) it3.next();

    while (it3.hasNext()) {
      int pos = t3.getEnd();
      String sub = docText.substring(pos);
      // System.out.println("the Test sub is"+sub);
      if ((!docText.startsWith("?", pos)) && (!docText.startsWith(".", pos))) {
        Token t = (Token) it.next();
        t2 = (Token) it2.next();
        t3 = (Token) it3.next();
        NGram annotation = new NGram(aJCas);
        FSArray uniarray = new FSArray(aJCas, 3);
        uniarray.set(0, t);
        uniarray.set(1, t2);
        uniarray.set(2, t3);
        annotation.setBegin(t.getBegin());
        annotation.setEnd(t3.getEnd());
        annotation.setElements(uniarray);
        annotation.setElementType("edu.cmu.deiis.types.Token");
        annotation.setCasProcessorId("TokenTriGramAnnotator");
        annotation.setConfidence(1.0);
        annotation.addToIndexes();
      } else // if reaches the end of a sentence, jump to the next sentence.
      {
        it.next();
        it.next();
        it2.next();
        it2.next();
        it3.next();
        t3 = (Token) it3.next();
      }
    }
  }

}
