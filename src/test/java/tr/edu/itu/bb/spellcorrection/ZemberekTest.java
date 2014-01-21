package tr.edu.itu.bb.spellcorrection;

import net.zemberek.erisim.Zemberek;
import net.zemberek.tr.yapi.TurkiyeTurkcesi;
import org.junit.Test;

/**
 * User: erenbekar
 * Date: 5/14/13
 * Time: 6:11 PM
 */
public class ZemberekTest {

    @Test
    public void testZemberek(){

        Zemberek zemberek = new Zemberek(new TurkiyeTurkcesi());
        String[] list = zemberek.oner("kalam");

        for (String s : list) {
            System.out.println(s);
        }


    }

}
