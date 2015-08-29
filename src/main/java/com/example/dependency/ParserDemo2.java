package com.example.dependency;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import xjava.io.File;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

class ParserDemo2 {
   public static void main(String []args) throws Exception {
       String text = "Messi is used to being sought after by fans,celebrities and even fellow professionals but he was still caught by surprise when  asked by Brown for a selfie following Argentina's 1-0 Copa America win.";
       text = "Bell, based in Los Angeles, makes and distributes electronic, computer and building products";
        TreebankLanguagePack tlp = new PennTreebankLanguagePack();
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        LexicalizedParser lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
        lp.setOptionFlags(new String[]{"-maxLength", "500", "-retainTmpSubcategories"});
        TokenizerFactory<CoreLabel> tokenizerFactory =
                PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        List<CoreLabel> wordList = tokenizerFactory.getTokenizer(new StringReader(text)).tokenize();
        System.out.println(wordList);
        Tree tree = lp.apply(wordList); 
        System.out.println(tree);
        GrammaticalStructure gs = gsf.newGrammaticalStructure(tree);
        Collection<TypedDependency> tdl = gs.typedDependenciesCCprocessed(true);
        System.out.println(tdl);
        Main.writeImage(tree,tdl, "image.png",3);
    	TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
    	tp.printTree(tree);
    	
    //	  Tree tree=parser.getBestParse();
    	  List<TaggedWord> taggedSent=tree.taggedYield();
    	  
    	 for (  TypedDependency dependency : tdl) {
    		    int nodeIndex=dependency.dep().index();
    		    int parentIndex=dependency.gov().index();
    		    String relation=dependency.reln().toString();
    		    String token=taggedSent.get(nodeIndex - 1).word();
    		    String pos=taggedSent.get(nodeIndex - 1).tag();
    	        System.out.println(" token : "+ token + " , relation : "+ relation+ ", pos : "+ pos+ " , parentIndex : "+ parentIndex);
    		  }
		
  }
}