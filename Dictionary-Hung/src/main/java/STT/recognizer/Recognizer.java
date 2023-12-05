package STT.recognizer;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Recognizer {

    public enum Languages{
		AUTO_DETECT("auto"),//tells Google to auto-detect the language
		ARABIC_JORDAN("ar-JO"),
		ARABIC_LEBANON("ar-LB"),
		ARABIC_QATAR("ar-QA"),
		ARABIC_UAE("ar-AE"),
		ARABIC_MOROCCO("ar-MA"),
		ARABIC_IRAQ("ar-IQ"),
		ARABIC_ALGERIA("ar-DZ"),
		ARABIC_BAHRAIN("ar-BH"),
		ARABIC_LYBIA("ar-LY"),
		ARABIC_OMAN("ar-OM"),
		ARABIC_SAUDI_ARABIA("ar-SA"),
		ARABIC_TUNISIA("ar-TN"),
		ARABIC_YEMEN("ar-YE"),
		BASQUE("eu"),
		CATALAN("ca"),
		CZECH("cs"),
		DUTCH("nl-NL"),
		ENGLISH_AUSTRALIA("en-AU"),
		ENGLISH_CANADA("en-CA"),
		ENGLISH_INDIA("en-IN"),
		ENGLISH_NEW_ZEALAND("en-NZ"),
		ENGLISH_SOUTH_AFRICA("en-ZA"),
		ENGLISH_UK("en-GB"),
		ENGLISH_US("en-US"),
		FINNISH("fi"),
		FRENCH("fr-FR"),
		GALICIAN("gl"),
		GERMAN("de-DE"),
		HEBREW("he"),
		HUNGARIAN("hu"),
		ICELANDIC("is"),
		ITALIAN("it-IT"),
		INDONESIAN("id"),
		JAPANESE("ja"),
		KOREAN("ko"),
		LATIN("la"),
		CHINESE_SIMPLIFIED("zh-CN"),
		CHINESE_TRANDITIONAL("zh-TW"),
		CHINESE_HONGKONG("zh-HK"),
		CHINESE_CANTONESE("zh-yue"),
		MALAYSIAN("ms-MY"),
		NORWEGIAN("no-NO"),
		POLISH("pl"),
		PIG_LATIN("xx-piglatin"),
		PORTUGUESE("pt-PT"),
		PORTUGUESE_BRASIL("pt-BR"),
		ROMANIAN("ro-RO"),
		RUSSIAN("ru"),
		SERBIAN("sr-SP"),
		SLOVAK("sk"),
		SPANISH_ARGENTINA("es-AR"),
		SPANISH_BOLIVIA("es-BO"),
		SPANISH_CHILE("es-CL"),
		SPANISH_COLOMBIA("es-CO"),
		SPANISH_COSTA_RICA("es-CR"),
		SPANISH_DOMINICAN_REPUBLIC("es-DO"),
		SPANISH_ECUADOR("es-EC"),
		SPANISH_EL_SALVADOR("es-SV"),
		SPANISH_GUATEMALA("es-GT"),
		SPANISH_HONDURAS("es-HN"),
		SPANISH_MEXICO("es-MX"),
		SPANISH_NICARAGUA("es-NI"),
		SPANISH_PANAMA("es-PA"),
		SPANISH_PARAGUAY("es-PY"),
		SPANISH_PERU("es-PE"),
		SPANISH_PUERTO_RICO("es-PR"),
		SPANISH_SPAIN("es-ES"),
		SPANISH_US("es-US"),
		SPANISH_URUGUAY("es-UY"),
		SPANISH_VENEZUELA("es-VE"),
		SWEDISH("sv-SE"),
		TURKISH("tr"),
        VIETNAMESE("vi"),
		ZULU("zu");

	    private final String languageCode;

	    private Languages(final String languageCode){
	    	this.languageCode = languageCode;
	    }

	    public String toString(){
	    	return languageCode;
	    }
	      
	}

    // "http://www.google.com/speech-api/v2/recognize?lang=en-us&key=ADD_YOUR_KEY_HERE&output=json" 
    private static final String GOOGLE_RECOGNIZER_URL = "http://www.google.com/speech-api/v2/recognize?client=chromium&output=json";

    private boolean profanityFilter = true;
    private String language = null;
    private String apikey = null;

    public Recognizer(String language, String apikey) {
        this.language = language; 
        this.apikey = apikey;
    }

     public Recognizer(Languages language, String apikey){
     	this.language = language.languageCode;
        this.apikey = apikey;
     }

    public Recognizer(boolean profanityFilter, String apikey){
    	this.profanityFilter = profanityFilter;
        this.apikey = apikey;
    }

    public Recognizer(String language, boolean profanityFilter, String apikey){
    	this.language = language;
    	this.profanityFilter = profanityFilter;
        this.apikey = apikey;
    }

     public Recognizer(Languages language, boolean profanityFilter, String apikey){
     	this.language = language.languageCode;
     	this.profanityFilter = profanityFilter;
        this.apikey = apikey;
     }

    public void setLanguage(Languages language) {
        this.language = language.languageCode;
    }

    public void setLanguage(String language) {
    	this.language = language;
    }

    public boolean getProfanityFilter(){
    	return profanityFilter;
    }

    public String getLanguage(){
    	return language;
    }

    public String getApiKey() {
        return apikey;
    }

    public void setApiKey(String apikey) {
        this.apikey = apikey;
    }

    public GoogleResponse getRecognizedDataForWave(File waveFile, int maxResults) throws IOException{
        FlacEncoder flacEncoder = new FlacEncoder();
        File flacFile = new File(waveFile + ".flac");

        flacEncoder.convertWaveToFlac(waveFile, flacFile);
        
        GoogleResponse googleResponse = getRecognizedDataForFlac(flacFile, maxResults, 8000);
        
        //Delete converted FLAC data
        flacFile.delete();
        return googleResponse;
    }

    public GoogleResponse getRecognizedDataForWave(String waveFile, int maxResults) throws IOException {
        return getRecognizedDataForWave(new File(waveFile), maxResults);
    }

    public GoogleResponse getRecognizedDataForFlac(File flacFile, int maxResults) throws IOException {
    	return getRecognizedDataForFlac(flacFile, maxResults, 8000);
    }

    public GoogleResponse getRecognizedDataForFlac(File flacFile, int maxResults, int sampleRate) throws IOException{
        String [] response = rawRequest(flacFile, maxResults, sampleRate);
        GoogleResponse googleResponse = new GoogleResponse();
        parseResponse(response, googleResponse);
        return googleResponse;
    }

    public GoogleResponse getRecognizedDataForFlac(String flacFile, int maxResults, int sampleRate) throws IOException{
    	return getRecognizedDataForFlac(new File(flacFile), maxResults, sampleRate);
    }

    public GoogleResponse getRecognizedDataForFlac(String flacFile, int maxResults) throws IOException {
        return getRecognizedDataForFlac(new File(flacFile), maxResults);
    }

    public GoogleResponse getRecognizedDataForWave(File waveFile) throws IOException {
        return getRecognizedDataForWave(waveFile, 1);
    }

    public GoogleResponse getRecognizedDataForWave(String waveFile) throws IOException {
        return getRecognizedDataForWave(waveFile, 1);
    }

    public GoogleResponse getRecognizedDataForFlac(File flacFile) throws IOException {
        return getRecognizedDataForFlac(flacFile, 1);
    }

    public GoogleResponse getRecognizedDataForFlac(String flacFile) throws IOException {
        return getRecognizedDataForFlac(flacFile, 1);
    }

    private void parseResponse(String[] rawResponse, GoogleResponse googleResponse) {

        for (String s : rawResponse) {
            JSONObject jsonResponse = JSONObject.fromObject(s);
            JSONArray jsonResultArray = jsonResponse.getJSONArray("result");
            for (int i = 0; i < jsonResultArray.size(); i++) {
                JSONObject jsonAlternativeObject = jsonResultArray.getJSONObject(i);
                JSONArray jsonAlternativeArray = jsonAlternativeObject.getJSONArray("alternative");
                double prevConfidence = 0;
                for (int j = 0; j < jsonAlternativeArray.size(); j++) {
                    JSONObject jsonTranscriptObject = jsonAlternativeArray.getJSONObject(j);
                    String transcript = jsonTranscriptObject.optString("transcript", "");
                    double confidence = jsonTranscriptObject.optDouble("confidence", 0.0);
                    if (confidence > prevConfidence) {
                        googleResponse.setResponse(transcript);
                        googleResponse.setConfidence(String.valueOf(confidence));
                        prevConfidence = confidence;
                    } else
                        googleResponse.getOtherPossibleResponses().add(transcript);
                }
            }
        }
    }

    private String[] rawRequest(File inputFile, int maxResults, int sampleRate) throws IOException{
        URL url;
        URLConnection urlConn;
        OutputStream outputStream;
        BufferedReader br;

        StringBuilder sb = new StringBuilder(GOOGLE_RECOGNIZER_URL);
        if( language != null ) {
            sb.append("&lang=");
            sb.append(language);
        }
        else{
        	sb.append("&lang=auto");
        }
        if(apikey != null) {
                sb.append("&key=");
                sb.append(apikey);
        }

        if( !profanityFilter ) {
            sb.append("&pfilter=0");
        }
        sb.append("&maxresults=");
        sb.append(maxResults);

        // URL of Remote Script.
        url = new URL(sb.toString());
        // System.out.println("Recognizer.rawRequest(): url=" + url);

        // Open New URL connection channel.
        urlConn = url.openConnection();

        // we want to do output.
        urlConn.setDoOutput(true);

        // No caching
        urlConn.setUseCaches(false);

        // Specify the header content type.
        urlConn.setRequestProperty("Content-Type", "audio/x-flac; rate=" + sampleRate);
        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) "
        		+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.52 Safari/537.36");

        // Send POST output.
        outputStream = urlConn.getOutputStream();

        FileInputStream fileInputStream = new FileInputStream(inputFile);

        byte[] buffer = new byte[256];

        while ((fileInputStream.read(buffer, 0, 256)) != -1) {
            outputStream.write(buffer, 0, 256);
        }

        fileInputStream.close();
        outputStream.close();

        // Get response data.
        br = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), Charset.forName("UTF-8")));

        List<String> completeResponse = new ArrayList<String>();
        String response = br.readLine();
        while(response != null) {
            completeResponse.add(response);
            response = br.readLine();
        }

        br.close();

        // System.out.println("Recognizer.rawRequest() -> completeResponse = " + completeResponse);
        return completeResponse.toArray(new String[completeResponse.size()]);
    }
}
