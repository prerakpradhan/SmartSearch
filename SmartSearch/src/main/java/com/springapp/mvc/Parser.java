package com.springapp.mvc;

import java.util.ArrayList;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

/**
 * Created by coolp_000 on 2/6/2015.
 */
public class Parser {

    ArrayList<String> resource = new ArrayList();
    ArrayList<String> predicate = new ArrayList();
    ArrayList<String> Target = new ArrayList();
    ArrayList<String> a = new ArrayList();
    ArrayList<String> b = new ArrayList();
    ArrayList<String> c = new ArrayList();
    ArrayList<String> Num = new ArrayList();
    ArrayList<String> Number = new ArrayList();
    ArrayList<String> tempArray = new ArrayList();

    public  ArrayList<Result> parsing(ArrayList<String> Ner_Normalized, String text,
                                      ArrayList<String> Wordlist ) {

        String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
        String[] options = {"-maxLength", "80", "-retainTmpSubcategories"};
        LexicalizedParser lp = LexicalizedParser.loadModel(grammar, options);
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        ArrayList<Result> result = new ArrayList();
        Iterable<List<? extends HasWord>> sentences;

        Tokenizer<? extends HasWord> toke =
                tlp.getTokenizerFactory().getTokenizer(new StringReader(text));
        List<? extends HasWord> sentence2 = toke.tokenize();
        List<List<? extends HasWord>> tmp =
                new ArrayList<List<? extends HasWord>>();

        tmp.add(sentence2);
        sentences = tmp;

        for ( List<? extends HasWord> sentence : sentences) {
            Tree parse = lp.apply(sentence);
            GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
            Collection tdl = gs.typedDependenciesCCprocessed(true);

            String s = tdl.toString();
            System.out.println(s);

            int l1, l2, l3, len = s.length(), i = 0;
            String s1, s2, s3, temp, temp1 = "";

            while (i < len) {
                l1 = s.indexOf('(', i);
                if (l1 == -1) {
                    break;
                }
                s1 = s.substring(i + 1, l1);
                if (s1.charAt(0) == ' ') {
                    s1 = s1.substring(1, s1.length());
                }

                l2 = s.indexOf('-', l1);
                if (l2 == -1) {
                    break;
                }
                s2 = s.substring(l1 + 1, l2);

                l3 = s.indexOf('-', l2 + 2);
                if (l3 == -1) {
                    break;
                }
                s3 = s.substring(l2 + 4, l3);
                if (s3.charAt(0) == ' ') {
                    s3 = s3.substring(1, s3.length());
                }

                i = l3 + 4;
                System.out.println(s1 + " :   " + s2 + "  =>  " + s3);
                a.add(s1);
                b.add(s2);
                c.add(s3);
            }

            System.out.println();
            if ( Ner_Normalized.isEmpty()) {
                l1 = a.indexOf("root");
                temp = c.get(l1);
                if (!Target.contains(temp)) {
                    Target.add(temp);
                }
            } else {
                for (i = 0; i < Ner_Normalized.size(); i++) {
                    temp = Ner_Normalized.get(i);
                    if (!resource.contains(temp)) {
                        resource.add(temp);
                    }
                    l1 = c.indexOf(temp);
                    if (l1 != -1) {
                        temp = b.get(l1);
                        if (!Target.contains(temp) && !temp.equals("ROOT")) {
                            Target.add(temp);
                        }
                    }
                    // System.out.println("Target is  => " + temp);
                }
            }

            String tempb, tempc;

            if ( a.contains("mwe")) {
                i = a.indexOf("mwe");
                if ((c.get(i)).equals("less") || (c.get(i)).equals("lesser")) {
                    //  System.out.println("here is num2");
                    temp1 = "0";
                } else {
                    if ((c.get(i)).equals("more") || (c.get(i)).equals("larger")
                            || (c.get(i)).equals("longer") || (c.get(i)).equals("long")
                            || (c.get(i)).equals("greater")) {
                        temp1 = "2";
                    } else {
                        temp1 = "1";
                    }
                }
                if (!Num.contains(temp1)) {
                    Num.add(temp1);
                }
            }

            for (i = 0; i < a.size(); i++) {
                temp = a.get(i);
                tempb = b.get(i);
                tempc = c.get(i);

                if (temp.equals("num") || tempb.matches("[0-9]+") || tempc.matches("[0-9]+") ) {
                    // updated here tempc case added
                    if (!Num.contains(c.get(i))) {
                        Num.add(c.get(i));
                    }
                    if (!Num.contains(b.get(i))) {
                        Num.add(b.get(i));
                    }

                    int k = c.indexOf(b.get(i));
                    int j = 0;
                    for (j = 0; j < b.size(); j++) {
                        if (j != k && (b.get(j)).equals(b.get(k))) {
                            temp1 = c.get(j);
                            if (!Num.contains(temp1)) {
                                Num.add(temp1);
                            }
                        }
                    }
                    int m = b.indexOf(temp1);
                    if (m != -1) {
                        if (!Num.contains(c.get(m))) {
                            Num.add(c.get(m));
                        }
                    }
                }

                if ( temp.equals("conj_and") || temp.equals("conj_or")) {
                    if ( Target.contains(b.get(i))) {
                        Target.add(c.get(i));
                    }
                }
                if (temp.equals("quatmod")) {
                    if (!Num.contains(b.get(i))) {
                        Num.add(b.get(i));
                    }
                    l1 = c.indexOf(b.get(i));
                    if (l1 != -1) {
                        if (!Num.contains(b.get(l1))) {
                            Num.add(b.get(l1));
                        }
                    }
                    if (!Num.contains(c.get(i))) {
                        Num.add(c.get(i));
                    }

                }

                temp1 = b.get(i);
        /*        for (int j = 0; j < Target.size(); j++) {
                    if (temp1.equals(Target.get(j))) {
                        if (!predicate.contains(c.get(i)) && !resource.contains(c.get(i))) {
                            predicate.add(c.get(i));
                        }
                        // System.out.println("Prediacte is " + c.get(i));
                    }
                } */
            }

            Number.add("");
            Number.add("");
            for ( i = 0; i < Num.size(); i++ ) {
                temp = Num.get(i);
                if ( temp.equals("than") || temp.equals("smaller") || temp.equals("more")
                        || temp.equals("less")  || temp.equals("greater") ) {
                } else {
                    if ( temp.equals("0") || temp.equals("1") || temp.equals("2") )
                        Number.set(0, temp);
                    else if(temp.matches("[0-9]+")) Number.set(1, temp);
                    else Number.add(temp);
                }
            }

            for ( i = 0; i < Wordlist.size(); i++ ) {
                temp = Wordlist.get(i);
                if ( temp.equals("smaller") || temp.equals("small")
                        || temp.equals("less") ) {
                    Number.set(0, "0");
                } else {
                    if ( temp.equals("more") || temp.equals("larger") || temp.equals("large")
                            || temp.equals("greater") )
                        Number.set(0, "2");
                    else if ( Number.get(0).equals(""))
                        Number.set(0, "1");
                }
            }

            // This is modeified for additional property
            for (  i = 0; i < a.size(); i++ ) {
                temp = b.get(i);

                if ( resource.contains(temp) || Target.contains(temp)
                        || predicate.contains(temp) || temp.equals("ROOT")
                        || temp.equals("with") || temp.equals("of")
                        || temp.equals("the") || temp.equals("or") ) {
                } else {
                    predicate.add(temp);
                }
              /*      temp = c.get(i);
                    if ( resource.contains(temp) || Target.contains(temp)
                          || predicate.contains(temp) || temp.equals("ROOT") ) {

                    } else {
                        predicate.add(temp);
                    } */
            }

            // ---------------end-------------------------

        }

        //processor p = new processor();
        //result = p.process( resource, predicate, Number, Target);
        return result;
    }

}
