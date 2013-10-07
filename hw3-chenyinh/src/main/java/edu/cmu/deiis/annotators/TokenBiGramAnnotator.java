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

public class TokenBiGramAnnotator extends JCasAnnotator_ImplBase {

  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    String docText = aJCas.getDocumentText();

    /*
     * use two tokens iterators get the 2-gram, first iterator points to the first token of the
     * 2-gram and the second iterator points to the second token
     */
    AnnotationIndex<Annotation> idxt = aJCas.getAnnotationIndex(Token.type);
    FSIterator<Annotation> it = idxt.iterator();

    AnnotationIndex<Annotation> idxt2 = aJCas.getAnnotationIndex(Token.type);
    FSIterator<Annotation> it2 = idxt2.iterator();

    Token t2 = (Token) it2.next();

    while (it2.hasNext()) {
      int pos = t2.getEnd();
      if ((!docText.startsWith("?", pos)) && (!docText.startsWith(".", pos))) {
        Token t = (Token) it.next();
        t2 = (Token) it2.next();
        NGram annotation = new NGram(aJCas);
        FSArray uniarray = new FSArray(aJCas, 2); // the size of the elementArray is 2
        uniarray.set(0, t);
        uniarray.set(1, t2);
        annotation.setBegin(t.getBegin());
        annotation.setEnd(t2.getEnd());
        annotation.setElements(uniarray);
        annotation.setElementType("edu.cmu.deiis.types.Toke");
        annotation.setCasProcessorId("TokenBiGramAnnotator");
        annotation.setConfidence(1.0);
        annotation.addToIndexes();
      } else // if reaches the end of a sentence, jump to the next sentence.
      {
        it.next();
        t2 = (Token) it2.next();
      }
    }

  }

}
