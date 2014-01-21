package tr.edu.itu.bb.spellcorrection.validators;


import tr.edu.itu.bb.spellcorrection.MorphologicalOperations;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import tr.edu.itu.bb.spellcorrection.*;
/**
 * @author erenbekar@gmail.com
 * @version 1.0.0
 * @since 2013-12-08
 */
public class ItuNlpToolsTurkishWordValidator implements TurkishWordValidator{

    private static final String BASE_URI = "http://tools.nlp.itu.edu.tr/SimpleApi";
    private static final String TOKEN = "x76B1ycrM2Ft5wMdds6cnER8br9QviXa";
    private CloseableHttpClient httpClient;
    
    private MorphologicalOperations morphOps = MorphologicalOperations.getInstance();

    public ItuNlpToolsTurkishWordValidator() {
        httpClient = HttpClientBuilder.create().build();
    }

    private String request(String serviceType, String input) {

        Scanner reader = null;
        String response = "";

        try {

            System.out.println("sending input: " + input);
            HttpPost post = new HttpPost(BASE_URI);

            List<NameValuePair> parameters 	= new ArrayList<>(3);

            parameters.add(new BasicNameValuePair("tool", serviceType));
            parameters.add(new BasicNameValuePair("input", input));
            parameters.add(new BasicNameValuePair("token", TOKEN));

            post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
            HttpResponse httpResponse = httpClient.execute(post);

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                reader = new Scanner(entity.getContent());

                while (reader.hasNextLine()) {
                    response += reader.nextLine();
                }

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            if (reader != null) {
                reader.close();
            }
        }

        System.out.println("response: " + response);

        return response;
    }


    @Override
    public boolean isTurkish(String word) {
       // return request("isturkish", word).equals("true");
    	return morphOps.isTurkish(word);
    }

}
