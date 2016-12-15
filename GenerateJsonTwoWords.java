package org.deeplearning4j.examples.nlp.word2vec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenerateJsonTwoWords {
	
	public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException, JsonProcessingException{
		WordVectors model = WordVectorSerializer.fromPair(WordVectorSerializer.loadTxt(new File("model.txt")));
		String name1 = "apple";
		String name2 = "opensource";
		int size = 3;
		Set<String> globalSet = new HashSet<String>();
		List<String> wordsNearest = (List<String>) model.wordsNearest(name1, size);
		globalSet.add(name1);
		globalSet.addAll(wordsNearest);
		System.out.println("name1 " + name1 + ":" +  wordsNearest);
		boolean found = false;
		if(wordsNearest.contains(name2)){
			found = true;
		}
		Word word = createWord(name1, wordsNearest, 1, model, size,found,name2,globalSet);
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(word);
		System.out.println(jsonInString);
	}
	
	
	
	private static Word createWord(String word , List<String> wordsNearest, int level, WordVectors model, int size, boolean found, String name2, Set<String> globalSet){
		Word w = new Word(word,new Integer(1),"");
		for(String childWord : wordsNearest){
			
			if(!found){
				List<String> nearestChildren = (List<String>) model.wordsNearest(childWord, size);
				nearestChildren.removeAll(globalSet);
				System.out.println("childWord " + childWord + ":" +  nearestChildren);
				globalSet.addAll(nearestChildren);
				boolean childfound = false;
				if(nearestChildren.contains(name2)){
					childfound = true;
					w.getChildren().add(createWord(childWord, Arrays.asList(name2), ++level, model, size, childfound,name2,globalSet));
				}else{
					w.getChildren().add(createWord(childWord, nearestChildren, ++level, model, size, childfound,name2,globalSet));	
				}
			}
		}
		return w;
	}
	class Word {
		private String name;
	    private List<Word> children = new ArrayList<Word>();
	    private Integer size;
	    private String language;
	    
		public Word(String name,  Integer size, String language) {
			super();
			this.name = name;
			this.size = size;
			this.language = language;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<Word> getChildren() {
			return children;
		}
		public void setChildren(List<Word> children) {
			this.children = children;
		}
		public Integer getSize() {
			return size;
		}
		public void setSize(Integer size) {
			this.size = size;
		}
		public String getLanguage() {
			return language;
		}
		public void setLanguage(String language) {
			this.language = language;
		}
		@Override
		public String toString() {
			return "[name=" + name + ", children=" + children + "]\n";
		}
	    
	    
	}	

}
