package STT.microphone;


import javax.sound.sampled.AudioFileFormat;

import STT.util.Complex;
import STT.util.FFT;

public class MicrophoneAnalyzer extends Microphone {

	public MicrophoneAnalyzer(AudioFileFormat.Type fileType){
		super(fileType);
	}

    public int getAudioVolume(){
    	return getAudioVolume(100);
    }

    public int getAudioVolume(int interval){
    	return calculateAudioVolume(this.getNumOfBytes(interval/1000d));
    }

    private int calculateAudioVolume(int numOfBytes){
    	byte[] data = getBytes(numOfBytes);
    	if(data==null)
    		return -1;
    	return calculateRMSLevel(data);
    }

	public static int calculateRMSLevel(byte[] audioData){
		long lSum = 0;
		for(int i=0; i<audioData.length; i++)
			lSum = lSum + audioData[i];

		double dAvg = lSum / audioData.length;

		double sumMeanSquare = 0d;
		for(int j=0; j<audioData.length; j++)
			sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

		double averageMeanSquare = sumMeanSquare / audioData.length;
		return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
	}

	public int getNumOfBytes(int seconds){
		return getNumOfBytes((double)seconds);
	}

	public int getNumOfBytes(double seconds){
		return (int)(seconds*getAudioFormat().getSampleRate()*getAudioFormat().getFrameSize()+.5);
	}

	private byte[] getBytes(int numOfBytes){
		if(getTargetDataLine()!=null){
    		byte[] data = new byte[numOfBytes];
    		this.getTargetDataLine().read(data, 0, numOfBytes);
    		return data;
		}
		return null;//If data cannot be read, returns a null array.
	}

	public int getFrequency(){
		try {
			return getFrequency(4096);
		} catch (Exception e) {
			//This will never happen. Ever...
			return -666;
		}
	}

	public int getFrequency(int numOfBytes) throws Exception{
		if(getTargetDataLine() == null){
			return -1;
		}
		byte[] data = new byte[numOfBytes+1];//One byte is lost during conversion
    	this.getTargetDataLine().read(data, 0, numOfBytes);
		return getFrequency(data);
	}

	public int getFrequency(byte[] bytes){
		double[] audioData = this.bytesToDoubleArray(bytes);
		audioData = applyHanningWindow(audioData);
		Complex[] complex = new Complex[audioData.length];
		for(int i = 0; i<complex.length; i++){
			complex[i] = new Complex(audioData[i], 0);
		}
		Complex[] fftTransformed = FFT.fft(complex);
		return this.calculateFundamentalFrequency(fftTransformed, 4);
	}

	private double[] applyHanningWindow(double[] data){
		return applyHanningWindow(data, 0, data.length);
	}

	private double[] applyHanningWindow(double[] signal_in, int pos, int size){
		for (int i = pos; i < pos + size; i++){
			int j = i - pos; // j = index into Hann window function
			signal_in[i] = (signal_in[i] * 0.5 * (1.0 - Math.cos(2.0 * Math.PI * j / size)));
		}
		return signal_in;
	}

	private int calculateFundamentalFrequency(Complex[] fftData, int N){
		if(N<=0 || fftData == null){ return -1; } //error case
		
		final int LENGTH = fftData.length;//Used to calculate bin size
		fftData = removeNegativeFrequencies(fftData);
		Complex[][] data = new Complex[N][fftData.length/N];
		for(int i = 0; i<N; i++){
			for(int j = 0; j<data[0].length; j++){
				data[i][j] = fftData[j*(i+1)];
			}
		}
		Complex[] result = new Complex[fftData.length/N];//Combines the arrays
		for(int i = 0; i<result.length; i++){
			Complex tmp = new Complex(1,0);
			for(int j = 0; j<N; j++){
				tmp = tmp.times(data[j][i]);
			}
			result[i] = tmp;
		}
		int index = this.findMaxMagnitude(result);
		return index*getFFTBinSize(LENGTH);
	}

	private Complex[] removeNegativeFrequencies(Complex[] c){
		Complex[] out = new Complex[c.length/2];
		for(int i = 0; i<out.length; i++){
			out[i] = c[i];
		}
		return out;
	}

	private int getFFTBinSize(int fftDataLength){
		return (int)(getAudioFormat().getSampleRate()/fftDataLength+.5);
	}

	private int findMaxMagnitude(Complex[] input){
		//Calculates Maximum Magnitude of the array
		double max = Double.MIN_VALUE;
		int index = -1;
		for(int i = 0; i<input.length; i++){
			Complex c = input[i];
			double tmp = c.getMagnitude();
			if(tmp>max){
				max = tmp;;
				index = i;
			}
		}
		return index;
	}

	private double[] bytesToDoubleArray(byte[] bufferData){
	    final int bytesRecorded = bufferData.length;
		final int bytesPerSample = getAudioFormat().getSampleSizeInBits()/8; 
	    final double amplification = 100.0; // choose a number as you like
	    double[] micBufferData = new double[bytesRecorded - bytesPerSample +1];
	    for (int index = 0, floatIndex = 0; index < bytesRecorded - bytesPerSample + 1; index += bytesPerSample, floatIndex++) {
	        double sample = 0;
	        for (int b = 0; b < bytesPerSample; b++) {
	            int v = bufferData[index + b];
	            if (b < bytesPerSample - 1 || bytesPerSample == 1) {
	                v &= 0xFF;
	            }
	            sample += v << (b * 8);
	        }
	        double sample32 = amplification * (sample / 32768.0);
	        micBufferData[floatIndex] = sample32;
	        
	    }
	    return micBufferData;
	}
	
}
