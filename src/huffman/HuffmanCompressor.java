package huffman;
import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;

public class HuffmanCompressor {

    private static final int HUFF = 0x48554646; 

    public CompressionResult compress(File inputFile, File outputFile, Consumer<String> log)throws IOException {

        if (inputFile == null || !inputFile.isFile()) {
            throw new IOException("Invalid input file.");
        }

        if (outputFile == null || inputFile.getCanonicalFile().equals(outputFile.getCanonicalFile())) {
            throw new IOException("Input and output files must be different.");
        }

        long originalSize = Files.size(inputFile.toPath());

    
        long[] frequencies = FrequencyCounter.countFrequencies(inputFile.getAbsolutePath());
        log.accept("1. Frequency counting completed.");

        
        HuffmanTree.buildTree(frequencies);
        log.accept("2. Huffman tree built.");

        
        String[] codes = HuffmanTree.generateCodes();
        log.accept("3. Huffman codes generated.");

        
        try (DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {

            output.writeInt(HUFF);
            output.writeLong(originalSize);

            for (long frequency : frequencies) {
                output.writeLong(frequency);
            }

            try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(inputFile))) {

                BitWriter.initialize(output);
                int value;

                while ((value = input.read()) != -1) {
                    String code = codes[value];

                    for (int i = 0; i < code.length(); i++) {
                        BitWriter.writeBit(code.charAt(i) == '1' ? 1 : 0);
                    }
                }
                BitWriter.finish();
            }
        }
        log.accept("4. Encoded data written successfully.");

        long compressedSize = Files.size(outputFile.toPath());

        double savedPercentage = (1.0 - (double) compressedSize / originalSize) * 100;
        log.accept("Compression completed.");

        return new CompressionResult(originalSize, compressedSize, savedPercentage);
    }


    private class BitWriter {

        private static DataOutputStream output;
        private static int currentByte;
        private static int bitCount;

        static void initialize(DataOutputStream stream) {
        output = stream;
        currentByte = 0;
        bitCount = 0;
     }

        static void writeBit(int bit) throws IOException {
            currentByte = (currentByte << 1) | bit;
            bitCount++;

            if (bitCount == 8) {
                output.writeByte(currentByte);
                currentByte = 0;
                bitCount = 0;
            }
        }

        static void finish() throws IOException {
            if (bitCount > 0) {
                currentByte <<= (8 - bitCount);
                output.writeByte(currentByte);
            }
        }
    }

    public static class CompressionResult {
        public final long originalSize;
        public final long compressedSize;
        public final double savedPercentage;

        public CompressionResult(long originalSize, long compressedSize,double savedPercentage) {
                this.originalSize = originalSize;
                this.compressedSize = compressedSize;
                this.savedPercentage = savedPercentage;
        }
    }
}
