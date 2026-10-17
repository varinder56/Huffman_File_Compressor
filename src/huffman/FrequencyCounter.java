package huffman;

import java.io.FileInputStream;
import java.io.IOException;

public class FrequencyCounter {

    private final static long[] frequencies = new long[256];

    public static long[] countFrequencies(String filePath) throws IOException {

        try (FileInputStream input = new FileInputStream(filePath)) {

            int value;
            while ((value = input.read()) != -1) {
                frequencies[value]++;
            }
        }
        return frequencies;
    }

   
}