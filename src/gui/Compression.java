package gui;

import huffman.HuffmanCompressor;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Compression extends JPanel {

    private final Color background = new Color(245, 247, 252);
    private final Color blue = new Color(76, 110, 245);
    private final Color grey = new Color(71, 85, 105);

    private File inputFile, outputFile;

    private final JLabel inputLabel = new JLabel("No file selected");
    private final JLabel outputLabel = new JLabel("No output selected");
    private final JLabel originalSize = new JLabel("—");
    private final JLabel compressedSize = new JLabel("—");
    private final JLabel saved = new JLabel("—");
    private final JLabel status = new JLabel("Waiting");

    private final JTextArea log = new JTextArea(10, 30);

    public Compression() {
        setLayout(new BorderLayout(10, 15));
        setBackground(background);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel heading = new JLabel("Compress File");
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(fileRow("Input File", inputLabel, "Choose File", true));
        content.add(Box.createVerticalStrut(10));
        content.add(fileRow("Output Destination", outputLabel, "Choose Location", false));
        content.add(Box.createVerticalStrut(10));

        JPanel summary = new JPanel(new GridLayout(1, 4, 8, 0));
        summary.setOpaque(false);
        summary.add(statCard("Original Size", originalSize));
        summary.add(statCard("Compressed Size", compressedSize));
        summary.add(statCard("Space Saved", saved));
        summary.add(statCard("Status", status));
        content.add(summary);
        content.add(Box.createVerticalStrut(10));

        log.setEditable(false);
        log.setFont(new Font("SansSerif", Font.PLAIN, 15));
        log.setForeground(grey);
        log.setBackground(Color.WHITE);
        log.setLineWrap(false);
        log.setBorder(new EmptyBorder(12, 12, 12, 12));
        log.setText("Select an input file to begin.");
        content.add(log);

        JButton compressButton = new JButton("Compress File");
        compressButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        compressButton.setBackground(blue);
        compressButton.setForeground(Color.WHITE);
        compressButton.setFocusPainted(false);
        compressButton.addActionListener(e -> startCompression());

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonRow.setOpaque(false);
        buttonRow.add(compressButton);
        content.add(Box.createVerticalStrut(10));
        content.add(buttonRow);

        add(content, BorderLayout.CENTER);
    }

    private JPanel fileRow(String title, JLabel label, String buttonText, boolean input) {
        JPanel row = new JPanel(new BorderLayout(10, 5));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(10, 12, 10, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel labels = new JPanel(new GridLayout(2, 1, 0, 5));
        labels.setOpaque(false);
        labels.add(heading);
        labels.add(label);

        JButton button = new JButton(buttonText);
        button.addActionListener(e -> {
            if (input) chooseInput();
            else chooseOutput();
        });

        row.add(labels, BorderLayout.CENTER);
        row.add(button, BorderLayout.EAST);
        return row;
    }

    private JPanel statCard(String title, JLabel value) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        value.setFont(new Font("SansSerif", Font.BOLD, 15));

        card.add(label);
        card.add(value);
        return card;
    }

    private void chooseInput() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File selected = chooser.getSelectedFile();
        if (!selected.isFile() || selected.getName().toLowerCase().endsWith(".huff")) {
            JOptionPane.showMessageDialog(this, "Choose a valid original file.");
            return;
        }

        inputFile = selected;
        inputLabel.setText(inputFile.getName());

        try {
            originalSize.setText(formatSize(Files.size(inputFile.toPath())));
        } catch (IOException e) {
            originalSize.setText("Unavailable");
        }

        outputFile = new File(inputFile.getAbsolutePath() + ".huff");
        outputLabel.setText(outputFile.getAbsolutePath());
        compressedSize.setText("—");
        saved.setText("—");
        status.setText("Ready");

        log.setText("Input selected: " + inputFile.getName()
                + "\nChoose the output destination, then start compression.");
    }

    private void chooseOutput() {
        if (inputFile == null) {
            JOptionPane.showMessageDialog(this, "Choose an input file first.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(outputFile);

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            if (!selected.getName().toLowerCase().endsWith(".huff"))
                selected = new File(selected.getAbsolutePath() + ".huff");

            outputFile = selected;
            outputLabel.setText(outputFile.getAbsolutePath());
        }
    }

    private void startCompression() {
        if (inputFile == null || outputFile == null) {
            JOptionPane.showMessageDialog(this,
                    "Select an input file and output destination.");
            return;
        }

        try {
            if (inputFile.getCanonicalFile().equals(outputFile.getCanonicalFile())) {
                JOptionPane.showMessageDialog(this,
                        "Input and output must be different files.");
                return;
            }

            status.setText("Compressing");
            log.setText("Starting compression...\n");

            HuffmanCompressor.CompressionResult result =
                    new HuffmanCompressor().compress(inputFile, outputFile, message -> {});

            originalSize.setText(formatSize(result.originalSize));
            compressedSize.setText(formatSize(result.compressedSize));
            saved.setText(String.format("%.2f%%", result.savedPercentage));
            status.setText("Completed");

            log.setText(
                    "Starting compression...\n"
                    + "1. Frequency counting completed.   Time: O(n) | Space: O(1)\n\n"
                    + "2. Huffman tree built.   Time: O(k log k) | Space: O(k)\n\n"
                    + "3. Huffman codes generated.   Time: O(k) | Space: O(k)\n\n"
                    + "4. Encoded data written successfully.   Time: O(n + L) | Space: O(1)\n\n"
                    + "Compression completed.\n\nOutput: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            status.setText("Failed");
            log.setText("Compression failed: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Compression failed:\n" + e.getMessage());
        }
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
}
