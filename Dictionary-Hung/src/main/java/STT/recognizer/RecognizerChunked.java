package STT.recognizer;


import STT.util.StringUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class RecognizerChunked {

	private static final String GOOGLE_SPEECH_URL_V2 = "https://www.google.com/speech-api/v2/recognize";

	private final String API_KEY;

	private String language;

	private List<GSpeechResponseListener> responseListeners = new ArrayList<GSpeechResponseListener>();

	public RecognizerChunked(String API_KEY){
		this.API_KEY = API_KEY;
		this.language = "auto";
	}

	public RecognizerChunked(String API_KEY, String language){
		this(API_KEY);
		this.language = language;
	}

	public String getLanguage(){
		return language;
	}

	public void setLanguage(String language){
		this.language = language;
	}

	public void getRecognizedDataForFlac(File infile, int sampleRate) throws IOException{
		byte[] data = mapFileIn(infile);
		getRecognizedDataForFlac(data, sampleRate);
	}

	public void getRecognizedDataForFlac(String inFile, int sampleRate) throws IOException{
		getRecognizedDataForFlac(new File(inFile), sampleRate);
	}

	public void getRecognizedDataForFlac(byte[] data, int sampleRate){
		StringBuilder sb = new StringBuilder(GOOGLE_SPEECH_URL_V2);
		sb.append("?output=json");
		sb.append("&client=chromium");
		sb.append("&lang=" + language);
		sb.append("&key=" + API_KEY);
		String url = sb.toString();
		
		openHttpsPostConnection(url, data, sampleRate);
	}

	private void openHttpsPostConnection(final String urlStr, final byte[] data, final int sampleRate) {
		new Thread () {
			public void run() {
				HttpsURLConnection httpConn = null;
				ByteBuffer buff = ByteBuffer.wrap(data);
				byte[] destdata = new byte[2048];
				int resCode = -1;
				OutputStream out = null;
				try {
					URL url = new URL(urlStr);
					URLConnection urlConn = url.openConnection();
					if (!(urlConn instanceof HttpsURLConnection)) {
						throw new IOException ("URL must be HTTPS");
					}
					httpConn = (HttpsURLConnection)urlConn;
					httpConn.setAllowUserInteraction(false);
					httpConn.setInstanceFollowRedirects(true);
					httpConn.setRequestMethod("POST");
					httpConn.setDoOutput(true);
					httpConn.setChunkedStreamingMode(0); //TransferType: chunked
					httpConn.setRequestProperty("Content-Type", "audio/x-flac; rate=" + sampleRate);
					// this opens a connection, then sends POST & headers.
					out = httpConn.getOutputStream();
					//beyond 15 sec duration just simply writing the file
					// does not seem to work. So buffer it and delay to simulate
					// bufferd microphone delivering stream of speech
					// re: net.http.ChunkedOutputStream.java
					while(buff.remaining() >= destdata.length){
						buff.get(destdata);
						out.write(destdata);
					};
					byte[] lastr = new byte[buff.remaining()];
					buff.get(lastr, 0, lastr.length);
					out.write(lastr);
					out.close();
					resCode = httpConn.getResponseCode();
//					if(resCode >= HttpURLConnection.HTTP_UNAUTHORIZED){//Stops here if Google doesn't like us/
//						throw new HTTPException(HttpURLConnection.HTTP_UNAUTHORIZED);//Throws
//					}
					String line;//Each line that is read back from Google.
					BufferedReader br =	new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
					while ((line = br.readLine( )) != null) {
						if(line.length()>19 && resCode > 100 && resCode < HttpURLConnection.HTTP_UNAUTHORIZED){
							GoogleResponse gr = new GoogleResponse();
							parseResponse(line, gr);
							fireResponseEvent(gr);
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					if(httpConn != null) {
						httpConn.disconnect();
					}
				}
			}
		}.start();
	}

	private byte[] mapFileIn(File infile) throws IOException{
		FileInputStream fis = new FileInputStream(infile);
		try{
			FileChannel fc = fis.getChannel(); // Get the file's size and then map it into memory
			int sz = (int)fc.size();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);
			byte[] data2 = new byte[bb.remaining()];
			bb.get(data2);
			return data2;
		}
		finally{//Ensures resources are closed regardless of whether the action suceeded
			fis.close();
		}
	}

	private void parseResponse(String rawResponse, GoogleResponse gr){
		if(rawResponse == null || !rawResponse.contains("\"result\"")){ return; }
		if(rawResponse.contains("\"confidence\":")){
			String confidence = StringUtil.substringBetween(rawResponse, "\"confidence\":", "}");
			gr.setConfidence(confidence);
		}
		else{
			gr.setConfidence(String.valueOf(1d));
		}
		String array = StringUtil.trimString(rawResponse, "[", "]");
		if(array.contains("[")){
			array = StringUtil.trimString(array, "[", "]");
		}
		String[] parts = array.split(",");
		gr.setResponse(parseTranscript(parts[0]));
		for(int i = 1; i<parts.length; i++){
			gr.getOtherPossibleResponses().add(parseTranscript(parts[i]));
		}
	}

	private String parseTranscript(String s){
		String tmp = s.substring(s.indexOf(":")+1);
		if(s.endsWith("}")){
			tmp = tmp.substring(0, tmp.length()-1);
		}
		tmp = StringUtil.stripQuotes(tmp);
		return tmp;
	}

	public synchronized void addResponseListener(GSpeechResponseListener rl){
		responseListeners.add(rl);
	}

	public synchronized void removeResponseListener(GSpeechResponseListener rl){
		responseListeners.remove(rl);
	}

	private synchronized void fireResponseEvent(GoogleResponse gr){
		for(GSpeechResponseListener gl: responseListeners){
			gl.onResponse(gr);
		}
	}
	
}
