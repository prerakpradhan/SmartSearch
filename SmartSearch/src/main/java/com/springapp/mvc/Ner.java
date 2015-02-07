package com.springapp.mvc;

import edu.stanford.nlp.ie.*;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;
import java.util.List;
import java.util.ArrayList;
/**
 * Created by coolp_000 on 2/6/2015.
 */
public class Ner {

    ArrayList<Result> rs = new ArrayList();

    public void ner(String text) {
        ArrayList<String> Ner = new ArrayList();
        ArrayList<String> Ner_value = new ArrayList();
        ArrayList<String> Ner_Normalized = new ArrayList();
        ArrayList<String> Wordlist = new ArrayList();


        String serializedClassifier = "C:\\Final\\classifiers\\english.all.3class.distsim.crf.ser.gz";
        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

   /*     StringBuilder b = new StringBuilder(text);
        int i = 0;
        do {
            b.replace(i, i + 1, b.substring(i, i + 1).toUpperCase());
            i = b.indexOf(" ", i) + 1;
        } while (i > 0 && i < b.length());
        String Uppercased = b.toString();
        text = Uppercased;
*/
        System.out.println(classifier.classifyWithInlineXML(text));
        for (List<CoreLabel> lcl : classifier.classify(text)) {
            for (CoreLabel cl : lcl) {
                String s = cl.toString();
                int Length = s.length() - 1;
                int len = s.indexOf(' ');
                String ss = s.substring(17, len);
                int value = s.lastIndexOf('=');
                String value_of_String = s.substring(value + 1, Length);
                //    System.out.println(ss + "  " + value_of_String);
                char ch = ss.charAt(0);
                ch = Character.toUpperCase(ch);
                ss = ch + ss.substring(1, ss.length());
                Ner.add(ss);
                Ner_value.add(value_of_String);
            }
        }

        int i = 0;
        Ner.add("");
        Ner_value.add("");
        String temp = "", temp2;
        int count_normalized = 0;
        for (i = 0; i < Ner.size() - 1; i++) {
            temp = Ner.get(i);
            temp2 = Ner_value.get(i);
            if (!temp2.equals("O")) {
                if (temp2.equals(Ner_value.get(i + 1))) {
                    while (temp2.equals(Ner_value.get(i + 1)) && i < Ner.size() - 1) {
                        temp = temp + "_" + Ner.get(i + 1);
                        i++;
                    }
                    Ner_Normalized.add(temp);
                    count_normalized++;
                } else {
                    String temp22 = temp.toLowerCase();
                    //                 System.out.println("temp22" + temp22 );
                    //               System.out.println("Text before => " + text);
                    text = text.replaceAll( temp22 , temp );
                    //                System.out.println("Text after => " + text);

                    Ner_Normalized.add(temp);
                }
            }
        }

        Wordlist = Ner;
        System.out.println("Wordlist contains");
        for (  i = 0; i < Wordlist.size(); i++ ) {
            System.out.println(Wordlist.get(i));
        }

        String pattern;
        if (!Ner_Normalized.isEmpty() && count_normalized != 0) {
            i = 0;
            while ( i < Ner_Normalized.size() && count_normalized != 0) {
                pattern = Ner_Normalized.get(i);
                if (pattern.contains("_")) {
                    count_normalized--;
                    temp = pattern.replace("_", " ");
                    text = text.replaceAll(temp, pattern);
                }
                i++;
            }
        }
        System.out.println("Updated => " + text );
        System.out.println("NER in given text are : ");
        for (i = 0; i < Ner_Normalized.size(); i++) {
            System.out.println(Ner_Normalized.get(i));
        }

        if ( Ner_Normalized.isEmpty() ) {
            System.out.println("------------ Now Processing Non Ner case ----------");
           // NonNerStarter a = new NonNerStarter();
            //rs = a.Target_setter(Wordlist, text);
        } else {
            Parser N = new Parser();
            rs = N.parsing( Ner_Normalized, text, Wordlist );
        }
    }

    public ArrayList<Result> getResult()
    {
        return rs;
    }
}
