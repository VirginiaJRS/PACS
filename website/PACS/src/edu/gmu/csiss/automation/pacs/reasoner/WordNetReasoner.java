/**
#******************************************************************************
#
# PACS online system
# ---------------------------------------------------------
# Parameterless automatic classification system.
#
# Copyright (C) 2015 CSISS, GMU (http://csiss.gmu.edu), Ziheng Sun (szhwhu@gmail.com)
#
# This source is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by the Free
# Software Foundation; either version 2 of the License, or (at your option)
# any later version.
#
# This code is distributed in the hope that it will be useful, but WITHOUT ANY
# WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
# FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
# details.
#
# A copy of the GNU General Public License is available on the World Wide Web
# at <http://www.gnu.org/copyleft/gpl.html>. You can also obtain it by writing
# to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
# MA 02111-1307, USA.
#
#******************************************************************************
*/
package edu.gmu.csiss.automation.pacs.reasoner;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 *Class WordNetReasoner.java
 *@author Ziheng Sun
 *@time Oct 5, 2015 4:46:27 PM
 *Original aim is to support PACS.
 */
public class WordNetReasoner {
	
	public static final String wordnetpath = "C:/Program Files (x86)/WordNet/2.1/dict/";
	/**
	 * Break a sentence into words
	 * @param str
	 * @return
	 */
	public static String[] breakASentenceIntoWords(String str){
		str = str.replaceAll("[!?,.]", "");
		String[] strs = str.split("\\s+");
		return strs;
	}
	/**
	 * Calculate the distance between two sentences
	 * @param sen1
	 * @param sen2
	 * @return
	 */
	public static int calculateDistanceBetweenTwoSentence(String sen1, String sen2){
		String[] word1 = breakASentenceIntoWords(sen1);
		String[] word2 = breakASentenceIntoWords(sen2);
		//calculate the distance between each pair of word
		//sum all the distance up
		//divide the sum with the number of the word pairs
		int summeddis = 0;
		for(int i=0;i<word1.length;i++){
			for(int j=0;j<word2.length;j++){
				String w1 = word1[i];
				String w2 = word2[j];
				int singledis = calculateSemanticDistanceBetweenTwoWords(w1, w2);
				summeddis += singledis;
			}
		}
		int avgdis = summeddis/(word1.length*word2.length);
		return avgdis;
	}
	/**
	 * Calculate the distance between two names
	 * @param name1
	 * @param name2
	 * @return
	 */
	public static int calculateDistanceBetweenTwoName(String name1, String name2){
		int dis = calculateSemanticDistanceBetweenTwoWords(name1, name2);
		return dis;
	}
	/**
	 * Calculate literal distance
	 * @param w1
	 * @param w2
	 * @return
	 */
	public static int calculateLiteralDistanceBetweenTwoWords(String w1, String w2){
		return levenshtein_distance(w1, w2);
	}
	public static int levenshtein_distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
	/**
	 * Calculate semantic distance between two words based on wordnet
	 * @param w1
	 * @param w2
	 * @return
	 */
	public static int calculateSemanticDistanceBetweenTwoWords(String w1, String w2){
		System.setProperty("wordnet.database.dir", wordnetpath);
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		String wordForm = w1;

		int distance = -1;
		if(w1.equals(w2)){
			distance = 0;
		}else{
			//traverse wordnet to find the distance between two concept.
			Synset[] synsets = database.getSynsets(wordForm);
			//  Display the word forms and definitions for synsets retrieved
			if (synsets.length > 0)
			{
				System.out.println("The following synsets contain '" +
						wordForm + "' or a possible base form " +
						"of that text:");
				for (int i = 0; i < synsets.length; i++)
				{
					System.out.println("");
					String[] wordForms = synsets[i].getWordForms();
					for (int j = 0; j < wordForms.length; j++)
					{
						System.out.print((j > 0 ? ", " : "") +
								wordForms[j]);
						if(wordForms[j].contains(w2)||w2.contains(wordForms[j])){
							distance = 1;
						}
					}
					System.out.println(": " + synsets[i].getDefinition());
				}
			}
			else
			{
				System.err.println("No synsets exist that contain " +
						"the word form '" + wordForm + "'");
			}
		}
		
		if(distance==-1){
			distance = 10;
		}
		return distance;
	}
	/**
	 * Calculate the similarity between two objects based on Wordnet
	 * This function is only a very simple implementation of the similarity estimation algorithm
	 * Further development is needed.
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public static float calculateSimilarityBetweenTwoObject(String obj1, String obj2){
		//initialize the environment
		System.setProperty("wordnet.database.dir", wordnetpath);
		NounSynset nounSynset;
		NounSynset[] hyponyms;
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets("object", SynsetType.NOUN);
		int len = 0;
		for (int i = 0; i < synsets.length; i++) {
		    nounSynset = (NounSynset)(synsets[i]);
		    hyponyms = nounSynset.getHyponyms();
		    System.err.println(nounSynset.getWordForms()[0] +
		            ": " + nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms");
		    len++;
		}
		return len;
	}
	
	public static void main(String[] args){
//		WordNetReasoner.calculateSimilarityBetweenTwoObject("", "");
//		String[] words = WordNetReasoner.breakASentenceIntoWords("You are a good man. But I don't love you.");
//		for(String w: words){
//			System.out.println(w);
//		}
//		int dis = WordNetReasoner.calculateLiteralDistanceBetweenTwoWords("image23232xxsdfsdf", "bysexysdf123e23");
		int dis = WordNetReasoner.calculateSemanticDistanceBetweenTwoWords("hello", "hi");
		System.out.println(dis);
	}
}
