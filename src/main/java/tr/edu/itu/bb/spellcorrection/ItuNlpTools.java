package tr.edu.itu.bb.spellcorrection;

import tr.edu.itu.bb.spellcorrection.levenshtein.Candidate;
import tr.edu.itu.bb.spellcorrection.levenshtein.LevenshteinDistance;

import java.util.List;

/**
 * @author erenbekar@gmail.com
 * @version 1.0.0
 * @since 2013-12-08
 */
public class ItuNlpTools {

    private static final String CHAR_FILE = "data/model/characters.txt";
    private static final String TRAINING_FILE = "data/model/TotalTrainingData-unique.txt";
    private static final Bootstrap.TurkishValidator TURKISH_VALIDATOR = Bootstrap.TurkishValidator.ItuNlpTools;

    private static final int DEFAULT_WINDOW_SIZE = 3;

    private Bootstrap bootstrap = null;
    
    private static ItuNlpTools instance = null;

    private ItuNlpTools() throws Exception {

        this.bootstrap = new Bootstrap(TRAINING_FILE, null, CHAR_FILE, DEFAULT_WINDOW_SIZE, TURKISH_VALIDATOR);
        bootstrap.init(); //kuralları, bir eklemeli kuralları oluşturuyor. kuralları aho corasick trie'ya ekliyor.

    }
    
    public static ItuNlpTools getInstance() {
    	if ( instance == null ) {
    		try {
    			instance = new ItuNlpTools();
    			System.out.println( "ItuNlpTools instance created!" );
    		}
    		catch ( Exception e ) {
    			e.printStackTrace();
    		}
    	}
    	
    	return instance;
    }

    public String execute(String input){
    	return bootstrap.findCandidates(input);
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println(ItuNlpTools.getInstance().execute("diyorumm"));
    }
}
