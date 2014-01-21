package tr.edu.itu.bb.spellcorrection;



public class MorphologicalOperations {

	private static MorphologicalOperations instance = null;
	private MorphologicalAnalyzerServiceHandler analyzerServiceHandler;

	public static MorphologicalOperations getInstance() {
		if (instance == null) {
			instance = new MorphologicalOperations();
		}

		return instance;
	}

	private MorphologicalOperations() {
		analyzerServiceHandler = MorphologicalAnalyzerServiceHandler
				.getInstance();
	}

	public boolean isTurkish(String word) {
		return analyzerServiceHandler.isTurkish(word);
	}

	public String parse(String word) {
		return analyzerServiceHandler.parse(word);
	}

}
