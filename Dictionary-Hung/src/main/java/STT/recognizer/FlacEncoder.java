package STT.recognizer;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import javaFlacEncoder.FLACEncoder;
import javaFlacEncoder.FLACFileOutputStream;
import javaFlacEncoder.StreamConfiguration;



public class FlacEncoder {

    public FlacEncoder() {

    }

    public void convertWaveToFlac(File inputFile, File outputFile) {


        StreamConfiguration streamConfiguration = new StreamConfiguration();
        streamConfiguration.setSampleRate(8000);
        streamConfiguration.setBitsPerSample(16);
        streamConfiguration.setChannelCount(1);


        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputFile);
            AudioFormat format = audioInputStream.getFormat();

            int frameSize = format.getFrameSize();

            FLACEncoder flacEncoder = new FLACEncoder();
            FLACFileOutputStream flacOutputStream = new FLACFileOutputStream(outputFile);

            flacEncoder.setStreamConfiguration(streamConfiguration);
            flacEncoder.setOutputStream(flacOutputStream);

            flacEncoder.openFLACStream();

            int frameLength = (int) audioInputStream.getFrameLength();
            if(frameLength <= AudioSystem.NOT_SPECIFIED){
            	frameLength = 16384;//Arbitrary file size
            }
            int[] sampleData = new int[frameLength];
            byte[] samplesIn = new byte[frameSize];

            int i = 0;

            while (audioInputStream.read(samplesIn, 0, frameSize) != -1) {
                if (frameSize != 1) {
                    ByteBuffer bb = ByteBuffer.wrap(samplesIn);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                    short shortVal = bb.getShort();
                    sampleData[i] = shortVal;
                } else {
                    sampleData[i] = samplesIn[0];
                }

                i++;
            }

            sampleData = truncateNullData(sampleData, i);
            
            flacEncoder.addSamples(sampleData, i);
            flacEncoder.encodeSamples(i, false);
            flacEncoder.encodeSamples(flacEncoder.samplesAvailableToEncode(), true);

            audioInputStream.close();
            flacOutputStream.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void convertWaveToFlac(String inputFile, String outputFile) {
        convertWaveToFlac(new File(inputFile), new File(outputFile));
    }

    private int[] truncateNullData(int[] sampleData, int index){
    	if(index == sampleData.length) return sampleData;
    	int[] out = new int[index];
    	for(int i = 0; i<index; i++){
    		out[i] = sampleData[i];
    	}
    	return out;
    }

}
