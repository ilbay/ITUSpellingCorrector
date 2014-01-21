package tr.edu.itu.bb.spellcorrection.util;

import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import net.zemberek.yapi.Kelime;

import java.util.List;

/**
 * User: erenbekar
 * Date: 5/14/13
 * Time: 9:02 PM
 */
public class ZemberekUtil {

    private static final Zemberek zemberek = new Zemberek(new TurkiyeTurkcesi());

    public static String[] getCandidates(String misspelled){

        return zemberek.oner(misspelled);

    }

    public static boolean inTopN(String misspelled, String corrected, int n){

        String[] candidates = getCandidates(misspelled);

        int limit = n;

        if(candidates.length < n || n == -1){

            limit = candidates.length;

        }

        for (int i = 0; i < limit; i++) {

            if(candidates[i].equals(corrected)){
                return true;
            }

        }

        return false;

    }

    public static boolean canBeParsed(String word){

        //araya bosluk konulan kurallarda zemberek iki kelimeyi de gecirebiliyor
        return !word.contains(" ") && zemberek.kelimeDenetle(word);

    }

}
