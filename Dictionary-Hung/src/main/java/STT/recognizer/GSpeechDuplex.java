package STT.recognizer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import javaFlacEncoder.FLACFileWriter;
import STT.util.StringUtil;

public class GSpeechDuplex{

	private static final long MIN = 10000000;

	private static final long MAX = 900000009999999L;

	private static final String GOOGLE_DUPLEX_SPEECH_BASE = "https://www.google.com/speech-api/full-duplex/v1/";

	private List<GSpeechResponseListener> responseListeners = new ArrayList<GSpeechResponseListener>();

	private final String API_KEY;

	private String language = "auto";

	private final static int MAX_SIZE = 1048576;

	private final static byte[] FINAL_CHUNK = new byte[] { '0', '\r', '\n', '\r', '\n' };

	public GSpeechDuplex(String API_KEY){
		this.API_KEY = API_KEY;
	}

	public String getLanguage(){
		return language;
	}

	public void setLanguage(String language){
		this.language = language;
	}

	public void recognize(File flacFile, int sampleRate) throws IOException{
		recognize(mapFileIn(flacFile), sampleRate);
	}

	public void recognize(byte[] data, int sampleRate){

		if(data.length >= MAX_SIZE){//Temporary Chunking. Does not allow for Google to gather context.
			byte[][] dataArray = chunkAudio(data);
			for(byte[]array: dataArray){
				recognize(array, sampleRate);
			}
		}

		//Generates a unique ID for the response. 
		final long PAIR = MIN + (long)(Math.random() * ((MAX - MIN) + 1L));

		//Generates the Downstream URL
		final String API_DOWN_URL = GOOGLE_DUPLEX_SPEECH_BASE + "down?maxresults=1&pair=" + PAIR;

		//Generates the Upstream URL
		final String API_UP_URL = GOOGLE_DUPLEX_SPEECH_BASE + 
				"up?lang=" + language + "&lm=dictation&client=chromium&pair=" + PAIR + 
				"&key=" + API_KEY ;

		//Opens downChannel
		this.downChannel(API_DOWN_URL);
		//Opens upChannel
		this.upChannel(API_UP_URL, chunkAudio(data), sampleRate);
	}

	public void recognize(TargetDataLine tl, AudioFormat af) throws IOException, LineUnavailableException, InterruptedException {
		//Generates a unique ID for the response. 
		final long PAIR = MIN + (long)(Math.random() * ((MAX - MIN) + 1L));

		//Generates the Downstream URL
		final String API_DOWN_URL = GOOGLE_DUPLEX_SPEECH_BASE + "down?maxresults=1&pair=" + PAIR;

		//Generates the Upstream URL
		final String API_UP_URL = GOOGLE_DUPLEX_SPEECH_BASE + 
				"up?lang=" + language + "&lm=dictation&client=chromium&pair=" + PAIR + 
				"&key=" + API_KEY + "&continuous=true&interim=true"; //Tells Google to constantly monitor the stream;

		//Opens downChannel
		Thread downChannel = this.downChannel(API_DOWN_URL);

		//Opens upChannel
		Thread upChannel = this.upChannel(API_UP_URL, tl, af);
		try {
			downChannel.join();
			upChannel.interrupt();
			upChannel.join();
		} catch (InterruptedException e) {
			downChannel.interrupt();
			downChannel.join();
			upChannel.interrupt();
			upChannel.join();
		}

	}

	private volatile boolean stopRecognition = false;

	// ... (existing code)

	public void stopSpeechRecognition() {
		stopRecognition = true;
	}
	public void openSpeechRecognition() {
		stopRecognition = false;
	}
	private Thread downChannel(String urlStr) {
		final String url = urlStr;
		Thread downChannelThread = new Thread("Downstream Thread") {
			public void run() {
				Scanner inStream = openHttpsConnection(url);
				if (inStream == null) {
					// ERROR HAS OCCURRED
					System.out.println("Error has occurred");
					return;
				}
				String response;
				while (inStream.hasNext() && (response = inStream.nextLine()) != null && !stopRecognition) {
					if (response.length() > 17) {// Prevents blank responses from Firing
						GoogleResponse gr = new GoogleResponse();
						parseResponse(response, gr);
						fireResponseEvent(gr);
					}
				}
				inStream.close();
				System.out.println("Finished write on down stream...");
			}
		};
		downChannelThread.start();
		return downChannelThread;
	}

	private void upChannel(String urlStr, byte[][] data, int sampleRate) {
		final String murl = urlStr;
		final byte[][] mdata = data;
		final int mSampleRate = sampleRate;
		new Thread("Upstream File Thread") {
			public void run() {
				openHttpsPostConnection(murl, mdata, mSampleRate);
				// Google does not return data via this URL
			}
		}.start();
	}

	private Thread upChannel(String urlStr, TargetDataLine tl, AudioFormat af) throws IOException, LineUnavailableException {
		final String murl = urlStr;
		final TargetDataLine mtl = tl;
		final AudioFormat maf = af;
		if (!mtl.isOpen()) {
			mtl.open(maf);
			mtl.start();
		}
		Thread upChannelThread = new Thread("Upstream Thread") {
			public void run() {
				openHttpsPostConnection(murl, mtl, (int) maf.getSampleRate());
			}
		};
		upChannelThread.start();
		return upChannelThread;
	}

	private Scanner openHttpsConnection(String urlStr) {
		int resCode = -1;
		try {
			URL url = new URL(urlStr);
			URLConnection urlConn = url.openConnection();
			if (!(urlConn instanceof HttpsURLConnection)) {
				throw new IOException ("URL is not an Https URL");
			}
			HttpsURLConnection httpConn = (HttpsURLConnection)urlConn;
			httpConn.setAllowUserInteraction(false);
			// TIMEOUT is required
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			resCode = httpConn.getResponseCode();
			if (resCode == HttpsURLConnection.HTTP_OK) {
				return new Scanner(httpConn.getInputStream(), "UTF-8");
			}
			else{
				System.out.println("Error: " + resCode);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void openHttpsPostConnection(String murl, TargetDataLine mtl, int sampleRate) {
		URL url;
		try {
			url = new URL(murl);
			HttpsURLConnection httpConn = getHttpsURLConnection(sampleRate, url);
			// this opens a connection, then sends POST & headers.
			final OutputStream out = httpConn.getOutputStream();
			//Note : if the audio is more than 15 seconds
			// dont write it to UrlConnInputStream all in one block as this sample does.
			// Rather, segment the byteArray and on intermittently, sleeping thread
			// supply bytes to the urlConn Stream at a rate that approaches
			// the bitrate ( =30K per sec. in this instance ).
			System.out.println("Starting to write data to output...");
			final AudioInputStream ais = new AudioInputStream(mtl);;
			AudioSystem.write(ais, FLACFileWriter.FLAC, out);
			//Output Stream is automatically closed
			// do you need the trailer?
			// NOW you can look at the status.

			//Diagonostic Code.
			/*int resCode = httpConn.getResponseCode();
			if (resCode / 100 != 2) {
				System.out.println("ERROR");
			}
			Scanner scanner = new Scanner(httpConn.getInputStream());
			while(scanner.hasNextLine()){
				System.out.println("UPSTREAM READS:" + scanner.nextLine());
			}
			scanner.close();*/
			System.out.println("Upstream Closed...");
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	private Scanner openHttpsPostConnection(String urlStr, byte[][] data, int sampleRate){
		byte[][] mextrad = data;
		int resCode = -1;
		OutputStream out = null;
		// int http_status;
		try {
			URL url = new URL(urlStr);
			HttpsURLConnection httpConn = getHttpsURLConnection(sampleRate, url);
			// this opens a connection, then sends POST & headers.
			out = httpConn.getOutputStream();
			//Note : if the audio is more than 15 seconds
			// dont write it to UrlConnInputStream all in one block as this sample does.
			// Rather, segment the byteArray and on intermittently, sleeping thread
			// supply bytes to the urlConn Stream at a rate that approaches
			// the bitrate ( =30K per sec. in this instance ).
			System.out.println("Starting to write");
			for(byte[] dataArray: mextrad){
				out.write(dataArray); // one big block supplied instantly to the underlying chunker wont work for duration > 15 s.
				try {
					Thread.sleep(1000);//Delays the Audio so Google thinks its a mic.
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			out.write(FINAL_CHUNK);
			System.out.println("IO WRITE DONE");
			// do you need the trailer?
			// NOW you can look at the status.
			resCode = httpConn.getResponseCode();
			if (resCode / 100 != 2) {
				System.out.println("ERROR");
			}
			if (resCode == HttpsURLConnection.HTTP_OK) {
				return new Scanner(httpConn.getInputStream(), "UTF-8");
			}
			else{
				System.out.println("HELP: " + resCode);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private HttpsURLConnection getHttpsURLConnection(int sampleRate, URL url) throws IOException {
		URLConnection urlConn = url.openConnection();
		if (!(urlConn instanceof HttpsURLConnection)) {
            throw new IOException ("URL is not an Https URL");
        }
		HttpsURLConnection httpConn = (HttpsURLConnection)urlConn;
		httpConn.setAllowUserInteraction(false);
		httpConn.setInstanceFollowRedirects(true);
		httpConn.setRequestMethod("POST");
		httpConn.setDoOutput(true);
		httpConn.setChunkedStreamingMode(0);
		httpConn.setRequestProperty("Transfer-Encoding", "chunked");
		httpConn.setRequestProperty("Content-Type", "audio/x-flac; rate=" + sampleRate);
		// also worked with ("Content-Type", "audio/amr; rate=8000");
		httpConn.connect();
		return httpConn;
	}

	private byte[] mapFileIn(File infile) throws IOException{
		return Files.readAllBytes(infile.toPath());
	}

	private void parseResponse(String rawResponse, GoogleResponse gr){
		if(rawResponse == null || !rawResponse.contains("\"result\"")
				|| rawResponse.equals("{\"result\":[]}")){ return; }
		gr.getOtherPossibleResponses().clear(); // Emptys the list
		if(rawResponse.contains("\"confidence\":")){
			String confidence = StringUtil.substringBetween(rawResponse, "\"confidence\":", "}");
			gr.setConfidence(confidence);
		}
		else{
			gr.setConfidence(String.valueOf(1));
		}
		String response = StringUtil.substringBetween(rawResponse, "[{\"transcript\":\"", "\"}],");
		if (response == null) {
			response = StringUtil.substringBetween(rawResponse, "[{\"transcript\":\"", "\",\"");
		}
		gr.setResponse(response);
		gr.setFinalResponse(rawResponse.contains("\"final\":true"));
		String[] currentHypos = rawResponse.split("\\[\\{\"transcript\":\"");
		for(int i = 2; i<currentHypos.length; i++){
			String cleaned = currentHypos[i].substring(0, currentHypos[i].indexOf("\""));
			gr.getOtherPossibleResponses().add(cleaned);
		}
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

	private byte[][] chunkAudio(byte[] data) {
		if(data.length >= MAX_SIZE){//If larger than 1MB
			int frame = MAX_SIZE/2;
			int numOfChunks = (int)(data.length/((double)frame)) + 1;
			byte[][] data2D = new byte[numOfChunks][];
			for(int i = 0, j = 0; i<data.length && j<data2D.length; i+=frame, j++){
				int length = (data.length - i < frame)? data.length - i: frame;
				data2D[j] = new byte[length];
				System.arraycopy(data, i, data2D[j], 0, length);
			}
			return data2D;
		}	
		else{
			byte[][] tmpData = new byte[1][data.length];
			System.arraycopy(data, 0, tmpData[0], 0, data.length);
			return tmpData;
		}
	}
}
