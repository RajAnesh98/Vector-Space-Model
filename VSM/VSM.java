/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VSM;

import java.io.FileInputStream;
import java.lang.Math;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
/**
 *
 * @author Raj Anesh
 */
public class VSM {

    /**
     * @param args the command line arguments
     */
    
    
    //map---Used my logic in a way retrieve one document and save it to other treemap for different same terms-Tf//
    public  Map<String ,Double> map=new TreeMap();
    
    //TF---This is that map which is used to store term-with its tfs-with list of 50 documents
    public  TreeMap<String,List<Double>> TF=new TreeMap();
    
    //-This map is used to store Document frequency of terms in documents
    public  Map<String, Integer> DOCFREQ = new TreeMap<String, Integer>();
    
    //IDF---This map is used to store idf 
    public  Map<String,Double> IDF=new TreeMap();
    
    //TFIDF---This map is used to store tf*idf of terms of 50 documents
    public  Map<String,ArrayList<Double>> TFIDF=new TreeMap();
    
    //que---This map is used to store query-terms-Tfs otherwise 0 for other terms basically a vector
    public  TreeMap<String,List<Double>> que=new TreeMap();
    
    //que1---This map is used to store idf of query term otherwise 0 for other terms
    public  TreeMap<String,Double> que1=new TreeMap();
    
    //que2---This map is used to store TF-idf of query term
    public  TreeMap<String,Double> que2=new TreeMap();
 
    //rank---This map is store all documents matched to query  
    public  TreeMap<Double,Integer> rank=new TreeMap();
    
    //
    public  ArrayList<Double> list6=new ArrayList();
    
    
    public   ArrayList StopWordslist = new ArrayList();
    
    //used to store stopwords coming in query
    public   ArrayList <String> StopWordslistINQuery = new ArrayList();
    
    //Query Norm---
    public  double ans3;
    
    //Alpha
    public  double alpha=0.005;
    
    //NO Of Documents
    public  int corpussize;
    
    public   String tempFilename1="ShortStories\\";
    
  
        public  void  createDictionary(int n) throws FileNotFoundException, IOException{

            
        //My first logic was to store all tf-df-idf-tf*idf values in a file then got idea of storing in lists using treemap  -------------------  
            
        FileInputStream input = null;
        float t;
        corpussize=n;
        File f=new File("TF.txt");
        FileWriter fstream = new FileWriter("TF.txt");
        BufferedWriter out = new BufferedWriter(fstream);
     
        File doc=new File("IDF.txt");
        FileWriter fstream12 = new FileWriter("IDF.txt");
        BufferedWriter out12 = new BufferedWriter(fstream12);
     
        
        
        File f1=new File("TF.txt");
        FileReader fstream123 = new FileReader("IDF.txt");
        BufferedReader out123 = new BufferedReader(fstream123);
     
        File doc1=new File("IDF.txt");
        FileReader fstream1234 = new FileReader("IDF.txt");
        BufferedReader out1234 = new BufferedReader(fstream1234);
        Integer count1;
        
        
        //----------------------------------------------------------------------------------------------------------------------------
       
        String Word;
        Scanner scan;
        try {

         
            
         //  **For Stopwords***   
            
       //     String StopWordsFileName = FolderName + "\\Stopword-List.txt";
            input = new FileInputStream("Stopword-List.txt");
            scan = new Scanner(input);
            while (scan.hasNext()) {
                Word = scan.next();
                StopWordslist.add(Word);
            }

            // ***FOR LEXICON***
            String FileName =tempFilename1; //For the Documents
            for (int i = 0; i <= n-1; i++) {
                String tempFilename =FileName+(i+1) + ".txt";
                input = new FileInputStream(tempFilename);
                scan = new Scanner(input);
                Set<String> alreadyAdded = new HashSet<String>();
          
                while (scan.hasNext()) {
                    Word = scan.next();
                    Word = Word.toLowerCase();
                    Word = Word.replaceAll("[^\\w\\s]", ""); 

                    if (!StopWordslist.contains(Word) && !Word.equals("")) {
                               
                        if (!alreadyAdded.contains(Word))
                        {
                            if (!DOCFREQ.containsKey(Word))
                            {
                                DOCFREQ.put(Word, 1);
                            }
                            else
                            {
                                DOCFREQ.put(Word, DOCFREQ.get(Word) + 1);
                            }
                                alreadyAdded.add(Word); // don't add the word anymore if found again in the document
                        }
                          // get value of the specified key
		        Double count = map.get(Word);
		
		// if Not mapping for that key, then map the key with value of 1
		        if (count == null) 
                        {
			    map.put(Word, 1.0);
                            
		        }
		// else increment the value by 1
		        else
                        {
			    map.put(Word, count + 1);
                            
		        }
                    }
                    
                }
                  
                    //For Tfs of terms in 50 documents....
                    
                    for (Map.Entry<String, Double> entry : map.entrySet())
                    {
                        
                            if(!TF.containsKey(entry.getKey()))
                            {
                               
                                    TF.put(entry.getKey(), new ArrayList<>(Collections.nCopies(n, 0.0)));
                                    TF.get(entry.getKey()).set(i,1+Math.log10(entry.getValue()));
                                    
                            }
                            else
                            {
                                 
                                TF.get(entry.getKey()).set(i,1+Math.log10(entry.getValue()));
        
                            }                        
                    }
                 // Clear that map and run again for next documents for tf's   
                  map.clear();
                }
            
            
            
            //For IDF......
            for(Map.Entry<String,Integer> entry : DOCFREQ.entrySet() )
            {
                
                double idf=Math.log10((double)(n / entry.getValue()));
                IDF.put(entry.getKey(), idf);
                   
            }
           
        }catch (FileNotFoundException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try {
                input.close();
            } catch (IOException ex) {
                Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
        
        
    // ans1-- for to store every document norm and then add it to list.   
    public  double ans1;   
    
    public  void TFIDFCALCULATOR()
    {
       //FOR Every term's 50 documents tfs*idf 
        for(int k=0;k<corpussize;k++)
        {
            for (Map.Entry<String, Double> entry : IDF.entrySet()) 
            {
                if(!TFIDF.containsKey(entry.getKey()))
                {
                    ArrayList TFlist=(ArrayList) TF.get(entry.getKey());
                    double ans=(double)TFlist.get(k) * IDF.get(entry.getKey());
                    ArrayList<Double> TFIDFlist1=new ArrayList<>();
                    TFIDFlist1.add(k,ans);
                    TFIDF.put(entry.getKey(), TFIDFlist1);
                }
                else
                {
                    ArrayList list=(ArrayList) TF.get(entry.getKey());
                    double ans=(double)list.get(k) * IDF.get(entry.getKey());
                    ArrayList list2=TFIDF.get(entry.getKey());
                    list2.add(k,ans);
                    
                }
                
                ArrayList list5=(ArrayList) TFIDF.get(entry.getKey());
                ans1+=Math.pow((double) list5.get(k), 2);
                
            }
            ans1=Math.sqrt(ans1);
            //list6-- to store 50 documents norm.
            list6.add(ans1);
            ans1=0;
        }
    }
    
    public  void QueryResult(String q)
    {     
        String[] ParsedQuery = q.split(" ");
        
        Double y=1.0;
 
        for(Map.Entry<String, Double> entry1 : IDF.entrySet())
        {
            que1.put(entry1.getKey(),0.0);
        }
        
        for(int i=0;i<ParsedQuery.length;i++)
        {
            for(Map.Entry<String, Double> entry1 : que1.entrySet())
            {
                String replaceAll = ParsedQuery[i].replaceAll("[^\\w\\s]", "");
                String toLowerCase = replaceAll.toLowerCase();
                if(!StopWordslist.contains(toLowerCase))
                {
                    if (entry1.getKey().equals(toLowerCase))
                    {		 
		        que1.put(toLowerCase, 1+Math.log10(entry1.getValue()+1));
                                
	            }                          
                }
                else
                {
                    StopWordslistINQuery.add(toLowerCase);
                }        
            }
        }
        
        for(Map.Entry<String, Double> entry1 : IDF.entrySet())
        {
           //Query--->TFIDF VALUES in QUE3----   
            que2.put(entry1.getKey(), entry1.getValue()*que1.get(entry1.getKey()));
        
        }
        
        for(Map.Entry<String, Double> entry1 : que2.entrySet())
        {
            if(entry1.getValue()!=0)
            {
                System.out.println(entry1.getKey()+"\t"+entry1.getValue());
            }
        }
        
        for(Map.Entry<String, Double> entry1 : que2.entrySet())
        {
          ans3+=Math.pow(entry1.getValue(), 2);
        }
            ans3=Math.sqrt(ans3);
            
            
          //  System.out.println("Query Norm-->"+ans3);
            
            double ans4 = 0;
            
           // Cosine Similarity for every document with given query..//
            for(int g=0;g<corpussize;g++)
            {
                for(Map.Entry<String, Double> entry1 : que2.entrySet())
                {
                    ArrayList list8=(ArrayList) TFIDF.get(entry1.getKey());
                    ans4=ans4+(double)(list8.get(g)) *  (entry1.getValue())/list6.get(g)*ans3; 
                }
                rank.put(ans4, g+1);
                ans4=0;
            }
      }
      
    VSM(String str,JTextArea area) throws FileNotFoundException,IOException {
        // TODO code application logic here
         
        createDictionary(50);
        System.out.println("");
        TFIDFCALCULATOR();
        System.out.println("");
        Scanner sc = new Scanner(System.in); 
  
        String name = str;
        QueryResult(name);
        int count=0;
        System.out.println("");
        System.out.println("");
        
        String res = "";
        
       
        for(Double key : rank.descendingKeySet())
        {
            if(key!=0.0)
            {
                if(key>=alpha)
                {
                    count++;
           //         System.out.println(rank.get(key)+".txt"+"---"+key);
                    res += rank.get(key)+".txt"+"---"+key+ "\n"+"\n";
                }
            }
        }
        //        System.out.println("TOTAL Retrieved DOCUMENTS = "+count); 
                res += "TOTAL Retrieved DOCUMENTS = "+count + "\n";
                
                area.setText(res);
    }
}
