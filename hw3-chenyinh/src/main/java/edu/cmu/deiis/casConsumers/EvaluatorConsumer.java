package edu.cmu.deiis.casConsumers;


import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.deiis.types.*;

public class EvaluatorConsumer extends CasConsumer_ImplBase{
  
  float calnumber = 0;
  float calcuscore = 0;
  

  
  public static Comparator<AnswerScore> comparator = new Comparator<AnswerScore>(){
    public int compare(AnswerScore a1, AnswerScore a2)
    {
      if (a1.getScore() > a2.getScore())
      {
        return -1;
      }else if(a1.getScore() < a2.getScore())
      {
        return 1;
      }
      return 0;
    }
   };

    @Override
    public void processCas(CAS aCAS) throws ResourceProcessException {
      
      JCas aJCas;
      
      try{
        aJCas = aCAS.getJCas(); 
      }catch(CASException e)
      {
        throw new ResourceProcessException(e);
      }


      String docText = aJCas.getDocumentText();
      
      AnnotationIndex<Annotation> idxQst = aJCas.getAnnotationIndex(Question.type);
      
      FSIterator<Annotation> itQst = idxQst.iterator();
      
      AnnotationIndex<Annotation> idxtAns = aJCas.getAnnotationIndex(Answer.type);
      
      FSIterator<Annotation> itAns = idxtAns.iterator();
   
      AnnotationIndex<Annotation> idxtAnScore = aJCas.getAnnotationIndex(AnswerScore.type);
      
      FSIterator<Annotation> itAnScore = idxtAnScore.iterator();
      
      int predcounter = 0;
      int truecounter = 0;
      
      ArrayList<Double> precisionlist = new ArrayList<Double>();
      
      while(itQst.hasNext())
      {
        Question qst = (Question)itQst.next();
     
       // int pos = qst.getEnd();
        ArrayList<AnswerScore> ansScorelist = new ArrayList<AnswerScore>();
        ArrayList<Answer> anslist = new ArrayList<Answer>();
        
      
          while(itAnScore.hasNext())
          {
            /*match grams*/
            AnswerScore ans = (AnswerScore)itAnScore.next();
            
            if(ans.getCasProcessorId().equals("NameEntityScore"))
            {
              Answer an = (Answer)itAns.next();
              ansScorelist.add(ans);
              anslist.add(an);
            }
            
         //   pos = ans.getEnd();
          
          }
          
        //  System.out.println("The size of anslist is :"+ ansScorelist.size());
          
          Collections.sort(ansScorelist, comparator);
          
          
          
          System.out.println(docText.substring(qst.getBegin(), qst.getEnd()));
         
          truecounter = 0;
          for(int i = 0; i < anslist.size();i++)
          {
            
            Answer an = anslist.get(i);
            if(an.getIsCorrect() == true)
            {
              truecounter++;
            }
          }
          
          predcounter = 0;
          for(int i = 0; i < ansScorelist.size();i++)
          {
              for(int j = 0; j < anslist.size(); j++)
              {
                if(ansScorelist.get(i).getAnswer().getBegin() 
                        == anslist.get(j).getBegin())
                {
                  if(anslist.get(j).getIsCorrect() == true)
                  {
                    System.out.println("A 1 "+ ansScorelist.get(i).getScore()+" "+
                            docText.substring(ansScorelist.get(i).getBegin(), ansScorelist.get(i).getEnd()));
                    if(i < truecounter) 
                    {
                      predcounter++;
                    }
                   }else if(anslist.get(j).getIsCorrect() == false)
                   {
                     System.out.println("A 0 "+ ansScorelist.get(i).getScore()+" "+
                             docText.substring(ansScorelist.get(i).getBegin(), ansScorelist.get(i).getEnd()));
                   }
                 }
                }
              
              
            }
            double p = ((double)predcounter)/((double)truecounter);
            System.out.println("The precision is: " + p);
            calcuscore =  (float) (calcuscore + p);
          
          
          
       }
      
      
      calnumber++;
     
    
      // TODO Auto-generated method stub
      
    }
    
    @Override
    public void collectionProcessComplete(ProcessTrace arg0)
            throws ResourceProcessException,
                   IOException{
      System.out.println("Average precision is: " + (float)calcuscore/calnumber);
    }
    
      
      
  }




