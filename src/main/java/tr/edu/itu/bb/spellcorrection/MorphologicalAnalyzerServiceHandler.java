package tr.edu.itu.bb.spellcorrection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


public class MorphologicalAnalyzerServiceHandler {
	
	private static final 			String HOST = "http://localhost:8020/";
	private 						CloseableHttpClient httpClient;
	private static MorphologicalAnalyzerServiceHandler instance = null;
	
	private static Process			p				= null;
	private static ProcessBuilder	pb				= null;
	private static BufferedReader	pBr		 		= null;
	private static BufferedWriter	pBw				= null;
	
	private static Process			xp				= null;
	private static ProcessBuilder	xpb				= null;
	private static BufferedReader	xpBr		 	= null;
	private static BufferedWriter	xpBw			= null;
	
	private MorphologicalAnalyzerServiceHandler() {
		httpClient = HttpClientBuilder.create().build();
	}
	
	private String request( String serviceType, String input ) {
		String uri = HOST;
		
		try {
			uri += serviceType + "?w=" + URLEncoder.encode( input, "UTF-8" );
		}
		catch ( UnsupportedEncodingException e ) {
			e.printStackTrace();
		}
		
		Scanner reader = null;
		String response = "";
		
		try {
			HttpResponse httpResponse = httpClient.execute( new HttpGet( uri ) );
			HttpEntity entity = httpResponse.getEntity();
			
			if( entity != null ) {
				reader = new Scanner( entity.getContent(), "UTF-8" );
				
				while ( reader.hasNextLine() ) {
					if ( ! response.isEmpty() ) {
						response += "\r\n";
					}
					response += reader.nextLine();
				}
			}
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
		finally {
			if( reader != null ) {
				try {
					reader.close();
				}
				catch ( Exception e ) {
					e.printStackTrace();
				}
			}
		}
		
		return response;
	}
	
	public static MorphologicalAnalyzerServiceHandler getInstance() {
		if (instance == null) {
			instance = new MorphologicalAnalyzerServiceHandler();
		}

		return instance;
	}
	
//	public String parse( String input ) {
//		//mahehein();
//		return request( "parse", input );
//	}
	
	public String parsefst(String input, boolean xerox) {
		if (!xerox) {
			return parse(input);
		} else {
			return parse_xerox(input);
		}
		
	}
	
	private String splitResult(String line) {
		String[] ret = line.split("\t");
		
		switch (ret.length) {
			case 2:
				return ret[1] + "\n";
			case 3:
				return ret[1] + ret[2] + "\n";
			default:
				return line + "\n";
		}
	}
	
	public String parse(String input) {
		if (p == null) {
			try {
				// hfst
			//	pb = new ProcessBuilder(Path.getPath() + "morph\\hfst-optimized-lookup.exe", "-q", Path.getPath() + "morph\\ituo.hfst");
				
				// xerox itu fst
				pb = new ProcessBuilder("C:\\ITUNLPWebInterface\\" + "morph\\lookup.exe", "-d", "-utf8", "-q", "C:\\ITUNLPWebInterface\\" + "morph\\itumorf.fst");
			
				pb.redirectErrorStream(true);
				pb.directory(new File("C:\\ITUNLPWebInterface\\" + "morph\\"));
				p = pb.start();
			} catch (Exception e) {
				p = null;
				pb = null;
				e.printStackTrace();
				return "Impossible to execute the morphological analyzer, error code 1";
			} 
		}
		
		//Write the input
		try {
			if (pBw == null) {
				pBw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream(), "UTF-8"));
			}
			pBw.write(input + "\n");
			pBw.flush();
		} catch (UnsupportedEncodingException e) {
			return "Unicode encoding not supported, error code 4";
		} catch (IOException e) {
			return "Impossible to execute the morphological analyzer, error code 2";
		}
		
		//Read the output
		String result = "";
		try {
			if (pBr == null) {
				pBr = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
			}
			String line;
			while ((line = pBr.readLine()) != null && line.compareTo("") != 0) {
				result += splitResult(line);
			}
		} catch (UnsupportedEncodingException e) {
			return "Unicode encoding not supported, error code 4";
		} catch (IOException e) {
			return "Impossible to execute the morphological analyzer, error code 3";
		}
		
		return result;
	}
	
	private String parse_xerox(String input) {
		if (xp == null) {
			try {
				xpb = new ProcessBuilder("C:\\ITUNLPWebInterface\\" + "morph\\lookup.exe ", "-d", "-utf8", "-q", "-f", "C:\\ITUNLPWebInterface\\" + "morph\\esp-strategy.txt");
				xpb.redirectErrorStream(true);
				xpb.directory(new File("C:\\ITUNLPWebInterface\\" + "morph\\"));
				xp = xpb.start();
			} catch (Exception e) {
				xp = null;
				xpb = null;
				return "Impossible to execute the morphological analyzer, error code 1";
			} 
		}
		
		//Write the input
		try {
			if (xpBw == null) {
				xpBw = new BufferedWriter(new OutputStreamWriter(xp.getOutputStream(), "UTF-8"));
			}
			xpBw.write(input + "\n");
			xpBw.flush();
		} catch (UnsupportedEncodingException e) {
			return "Unicode encoding not supported, error code 4";
		} catch (IOException e) {
			return "Impossible to execute the morphological analyzer, error code 2";
		}
		
		//Read the output
		String result = "";
		try {
			if (xpBr == null)
				xpBr = new BufferedReader(new InputStreamReader(xp.getInputStream(), "UTF-8"));
			String line;
			String[] ret;
			while ((line = xpBr.readLine()) != null && line.compareTo("") != 0) {
				result += splitResult(line);
			}
		} catch (UnsupportedEncodingException e) {
			return "Unicode encoding not supported, error code 4";
		} catch (IOException e) {
			return "Impossible to execute the morphological analyzer, error code 3";
		}
		
		return result;
	}

	
	public boolean isTurkish( String input ) {
		if ( isTurkishStr( input ).equals( "true" ) ) {
			return true;
		}
		
		return false;
	}
	
	public String isTurkishStr(String input) {
		//return request( "isTurkish", input );
		String ret = parse(input);
		if (ret.contains("?"))
			return "false";
		return "true";
	}
}
