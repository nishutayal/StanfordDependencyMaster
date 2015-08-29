package com.example.dependency;

import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCluster;
import edu.stanford.nlp.dcoref.Document;
import edu.stanford.nlp.dcoref.Mention;

/**
 * 
 * @author Nishu
 */
public class CoRefExample {

	public static void main(String[] args) throws IOException {
		System.out.println(Runtime.getRuntime().totalMemory());
		System.out.println(Runtime.getRuntime().maxMemory());

		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		; // The path for a file that includes a list of demonyms

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// read some text in the text variable
		// String text ="Ram and Lakshman went to the market he purchased";
		String text = "The Revolutionary War occurred during the 1700s .it was the first war in the United States";
	 
		// create an empty Annotation just with the given text

		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		System.out.println(sentences);
	 
		for (CoreMap sentence : sentences) {
			
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class);
			}

			// this is the parse tree of the current sentence
			Tree tree = sentence.get(TreeAnnotation.class);

			// this is the Stanford dependency graph of the current sentence
			SemanticGraph dependencies = sentence
					.get(CollapsedCCProcessedDependenciesAnnotation.class);

			System.out.println("Dependencies " + dependencies);
		}

		// This is the coreference link graph
		// Each chain stores a set of mentions that link to each other,
		// along with a method for getting the most representative mention
		// Both sentence and token offsets start at 1!
		Map<Integer, CorefChain> graph = document
				.get(CorefChainAnnotation.class);

		System.out.println("Graph " + graph);
		 
		for (Map.Entry<Integer, CorefChain> entry : graph.entrySet()) {
			CorefChain c = entry.getValue();
		 
			// this is because it prints out a lot of self references which
			// aren't that useful
			// if (c.getCorefMentions().size() <= 1)
			// continue;

			CorefMention cm = c.getRepresentativeMention();
			System.out.println("****************** cm value***************");
			System.out.println(cm);
			System.out.println(cm.startIndex + " , "+ cm.endIndex);
			String clust = "";
			List<CoreLabel> tks = document.get(SentencesAnnotation.class)
					.get(cm.sentNum - 1).get(TokensAnnotation.class);
 
			
			for (int i = cm.startIndex - 1; i < cm.endIndex - 1; i++)
				clust += tks.get(i).get(TextAnnotation.class) + " ";
			clust = clust.trim();
			System.out.println("representative mention: \"" + clust
					+ "\" is mentioned by:");

			Iterable<Set<CorefMention>> cSet = c.getMentionMap().values();
		 
			CorefMention m = c.getRepresentativeMention();
 			String clust2 = "";
			tks = document.get(SentencesAnnotation.class).get(m.sentNum - 1)
					.get(TokensAnnotation.class);
			for (int i = m.startIndex - 1; i < m.endIndex - 1; i++)
				clust2 += tks.get(i).get(TextAnnotation.class) + " ";
			clust2 = clust2.trim();
			// don't need the self mention
			if (clust.equals(clust2))
				continue;

			System.out.println("\t" + clust2);
		 
		}
	}
}