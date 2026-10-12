
package huffman;

import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;

public class HuffmanCompressor {

    private static final int MAGIC = 0x48554646; 

    public CompressionResult compress(
            File inputFile, File outputFile, Consumer<String> log)
            throws IOException {

        if (inputFile == null || !inputFile.isFile()) {
            throw new IOException("Invalid input file.");
        }

        if (outputFile == null ||
                inputFile.getCanonicalFile().equals(outputFile.getCanonicalFile())) {
            throw new IOException("Input and output files must be different.");
        }

        long originalSize = Files.size(inputFile.toPath());

        // Step 1: Count byte frequencies
        FrequencyCounter counter = new FrequencyCounter();
        counter.countFrequencies(inputFile.getAbsolutePath());

        long[] frequencies = counter.getFrequencies();
        log.accept("1. Frequency counting completed.");

        // Step 2: Build the Huffman tree
        HuffmanTree tree = new HuffmanTree();
        tree.buildTree(frequencies);

        log.accept("2. Huffman tree built.");

        // Step 3: Generate Huffman codes
        String[] codes = tree.generateCodes();

        log.accept("3. Huffman codes generated.");

        // Step 4: Write the header and encoded data
        try (DataOutputStream output = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputFile)))) {

            // File identifier and original file size
            output.writeInt(MAGIC);
            output.writeLong(originalSize);

            // Store all 256 byte frequencies for decompression
            for (long frequency : frequencies) {
                output.writeLong(frequency);
            }

            // Encode the original file one byte at a time
            try (BufferedInputStream input = new BufferedInputStream(
                    new FileInputStream(inputFile))) {

                BitWriter writer = new BitWriter(output);
                int value;

                while ((value = input.read()) != -1) {
                    String code = codes[value];

                    for (int i = 0; i < code.length(); i++) {
                        writer.writeBit(code.charAt(i) == '1' ? 1 : 0);
                    }
                }

                writer.finish();
            }
        }

        log.accept("4. Encoded data written successfully.");

        long compressedSize = Files.size(outputFile.toPath());

        double savedPercentage = originalSize == 0 ? 0.0
                : (1.0 - (double) compressedSize / originalSize) * 100.0;

        log.accept("Compression completed.");

        return new CompressionResult(
                originalSize, compressedSize, savedPercentage);
    }

    // Writes bits into the output file as complete bytes
    private static class BitWriter {

        private final DataOutputStream output;
        private int currentByte;
        private int bitCount;

        BitWriter(DataOutputStream output) {
            this.output = output;
        }

        void writeBit(int bit) throws IOException {
            currentByte = (currentByte << 1) | bit;
            bitCount++;

            if (bitCount == 8) {
                output.writeByte(currentByte);
                currentByte = 0;
                bitCount = 0;
            }
        }

        void finish() throws IOException {
            if (bitCount > 0) {
                currentByte <<= (8 - bitCount);
                output.writeByte(currentByte);
            }
        }
    }

    // Results returned to the GUI
    public static class CompressionResult {

        public final long originalSize;
        public final long compressedSize;
        public final double savedPercentage;

        public CompressionResult(
                long originalSize, long compressedSize,
                double savedPercentage) {

            this.originalSize = originalSize;
            this.compressedSize = compressedSize;
            this.savedPercentage = savedPercentage;
        }
    }
}
