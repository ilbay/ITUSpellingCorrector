package tr.edu.itu.bb.spellcorrection.trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import tr.edu.itu.bb.spellcorrection.levenshtein.Candidate;
import tr.edu.itu.bb.spellcorrection.levenshtein.Rule;
import tr.edu.itu.bb.spellcorrection.levenshtein.WordInformation;

public class CandidateSearcher {

	private Candidate word;
	private Map<Byte, List<Rule>> rulesAvailable;
	private Trie vocabularyTrie;

	public CandidateSearcher(Candidate word, Trie vocabularyTrie, Map<Byte, List<Rule>> rulesAvailable)
	{
		this.word = word;
		this.rulesAvailable = rulesAvailable;
		this.vocabularyTrie = vocabularyTrie;
	}
	
	public List<WordInformation> buildCandidateList(int maxCandidate)
	{
		List<WordInformation> candidateList = new ArrayList<>();
		Node rootNode = this.vocabularyTrie.getRootNode();
		
		Stack<DecisionPoint> decisionPointStack = new Stack<>();
		/**
		 * Used to find if there are multiple decision points that intersect at the same node.
		 * If yes, then prune the decision point that has the smallest weight
		 */
		Map<Node, DecisionPoint> decisionPointSet = new HashMap<>();

		DecisionPoint initialDecisionPoint = new DecisionPoint(this.word, 0, rootNode);
		decisionPointStack.push(initialDecisionPoint);
		decisionPointSet.put(rootNode, initialDecisionPoint);
		
		//min weight karşılaştırması daha implemente edilmedi.
		
		while(!decisionPointStack.isEmpty())
		{
			DecisionPoint currentDecisionPoint = decisionPointStack.pop();
			
			Candidate currentCandidateWord = currentDecisionPoint.getCandidateWord();
			int currentIndex = currentDecisionPoint.getIndex();
			Node currentNode = currentDecisionPoint.getNode();
			
			if(currentIndex >= currentCandidateWord.getCandidateWord().length() || (decisionPointSet.get(currentNode) != null && decisionPointSet.get(currentNode) != currentDecisionPoint))
			{
				continue;
			}
			
			if(currentNode.getSubNode(currentCandidateWord.getCandidateWord().charAt(currentIndex)) != null)
			{
				Node nextNode = currentNode.getSubNode(currentCandidateWord.getCandidateWord().charAt(currentIndex));
				DecisionPoint newDecisionPoint = new DecisionPoint(currentCandidateWord, currentIndex+1, nextNode);
				if(this.putNewDecisionPoint(newDecisionPoint, decisionPointSet))
				{
					decisionPointStack.add(newDecisionPoint);
				}
				if(nextNode.getOutput()!=null && !nextNode.getOutput().equals(""))
				{
					candidateList.add(new WordInformation(nextNode.getOutput(), currentCandidateWord.getCandidateWord().substring(currentIndex+1), currentCandidateWord.getTotalWeight(), currentIndex+1));
				}
			}
			
			List<Rule> rules = this.rulesAvailable.get((byte)currentIndex);
			if(rules == null)
			{
				continue;
			}
			
			for(Rule rule : rules)
			{
				Candidate newCandidateWord = currentCandidateWord.applyRule(rule);
				//if( candidateList.size() >= maxCandidate && candidateList.first().getTotalWeight() > newCandidateWord.getTotalWeight())
				//{
					//break;
				//}
				try
				{
					Node newCandidateCurrentNode = currentNode;
					for(char c : rule.getAfter().toCharArray())
					{
						newCandidateCurrentNode = newCandidateCurrentNode.getSubNode(c);
					}
					if(newCandidateCurrentNode.getOutput() != null && !newCandidateCurrentNode.getOutput().equals(""))
					{
						if(candidateList.size() == maxCandidate)
						{
							//candidateList.remove(candidateList.first());
						}
						candidateList.add(new WordInformation(newCandidateCurrentNode.getOutput(), newCandidateWord.getCandidateWord().substring(currentIndex+rule.getBefore().length()), newCandidateWord.getTotalWeight(), currentIndex+rule.getBefore().length()));
					}
					if(newCandidateCurrentNode != null) //TODO: This control seems not necessary
					{
						DecisionPoint newDecisionPoint = new DecisionPoint(newCandidateWord, currentIndex+rule.getBefore().length(), newCandidateCurrentNode);
						if(this.putNewDecisionPoint(newDecisionPoint, decisionPointSet))
						{
							decisionPointStack.push(newDecisionPoint);
						}
					}
				}
				catch(NullPointerException ex)
				{
					// Occured since probably the word generated by applying new word does not exist in vocabulary trie
					// No extra work is required.
				}
			}
		}
		
		Collections.sort(candidateList);
		
		return candidateList;
	}
	
	private boolean putNewDecisionPoint(DecisionPoint decisionPoint, Map<Node, DecisionPoint> decisionPointSet)
	{
		DecisionPoint existingDecisionPoint = decisionPointSet.get(decisionPoint.getNode());
		if( (existingDecisionPoint != null && existingDecisionPoint.getCandidateWord().getTotalWeight() < decisionPoint.getCandidateWord().getTotalWeight())
			|| existingDecisionPoint == null)
		{
			decisionPointSet.put(decisionPoint.getNode(), decisionPoint);
			return true;
		}
		return false;
	}
	
	private class DecisionPoint{
		private Node node;
		private int index;
		private Candidate candidateWord;
		
		public DecisionPoint(Candidate candidateWord, int index, Node node)
		{
			this.candidateWord = candidateWord;
			this.index = index;
			this.node = node;
		}
		
		public int hashCode()
		{
			return node.hashCode() * this.index + this.index;
		}
		
		public boolean equals(Object o)
		{
			DecisionPoint dp = (DecisionPoint)o;
			return dp.node == this.node && dp.index == this.index;
		}

		public Node getNode() {
			return node;
		}

		public void setNode(Node node) {
			this.node = node;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public Candidate getCandidateWord() {
			return candidateWord;
		}

		public void setCandidateWord(Candidate candidateWord) {
			this.candidateWord = candidateWord;
		}
	}
	
}
