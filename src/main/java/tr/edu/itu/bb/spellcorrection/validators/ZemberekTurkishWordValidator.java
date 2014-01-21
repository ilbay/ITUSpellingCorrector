package tr.edu.itu.bb.spellcorrection.validators;

import tr.edu.itu.bb.spellcorrection.util.ZemberekUtil;

/**
 * @author erenbekar@gmail.com
 * @version 1.0.0
 * @since 2013-12-08
 */
public class ZemberekTurkishWordValidator implements TurkishWordValidator {

    @Override
    public boolean isTurkish(String word) {

        return ZemberekUtil.canBeParsed(word);

    }

}
