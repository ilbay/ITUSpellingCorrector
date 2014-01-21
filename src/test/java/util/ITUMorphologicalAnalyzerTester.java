package util;

import org.junit.Test;
import tr.edu.itu.bb.spellcorrection.validators.ItuNlpToolsTurkishWordValidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author erenbekar@gmail.com
 * @version 1.0.0
 * @since 2013-11-24
 */
public class ITUMorphologicalAnalyzerTester {

    @Test
    public void testIsTurkish() {

        assertTrue(new ItuNlpToolsTurkishWordValidator().isTurkish("çekmiyor"));
        assertTrue(new ItuNlpToolsTurkishWordValidator().isTurkish("geliyorum"));
        assertTrue(new ItuNlpToolsTurkishWordValidator().isTurkish("kalem"));
        assertTrue(new ItuNlpToolsTurkishWordValidator().isTurkish("kaybettik"));
        assertTrue(new ItuNlpToolsTurkishWordValidator().isTurkish("çelme"));
        assertTrue(new ItuNlpToolsTurkishWordValidator().isTurkish("silgilerimden"));
        assertTrue(new ItuNlpToolsTurkishWordValidator().isTurkish("giydirme"));
        assertTrue(new ItuNlpToolsTurkishWordValidator().isTurkish("çaydanlık"));
        assertTrue(new ItuNlpToolsTurkishWordValidator().isTurkish("geldin"));

    }

    @Test
    public void testNotIsTurkish() {

        assertFalse(new ItuNlpToolsTurkishWordValidator().isTurkish("geliyom"));
        assertFalse(new ItuNlpToolsTurkishWordValidator().isTurkish("klem"));
        assertFalse(new ItuNlpToolsTurkishWordValidator().isTurkish("caydanlik"));
        assertFalse(new ItuNlpToolsTurkishWordValidator().isTurkish("geldinmi"));

    }



}
