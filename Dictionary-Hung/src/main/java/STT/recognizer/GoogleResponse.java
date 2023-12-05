package STT.recognizer;

import java.util.ArrayList;
import java.util.List;

public class GoogleResponse {

    private String response;

    private String confidence;

    private List<String> otherPossibleResponses = new ArrayList<String>(20);


    private boolean finalResponse = true;

    public GoogleResponse() {

    }

    public String getResponse() {
        return response;
    }

    protected void setResponse(String response) {
        this.response = response;
    }

    public String getConfidence() {
        return confidence;
    }

    protected void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public List<String> getOtherPossibleResponses() {
        return otherPossibleResponses;
    }

    public List<String> getAllPossibleResponses() {
    	List<String> tmp = otherPossibleResponses;
    	tmp.add(0,response);
    	return tmp;
    }

    public boolean isFinalResponse() {
        return finalResponse;
    }

    public void setFinalResponse(boolean finalResponse) {
        this.finalResponse = finalResponse;
    }
}
