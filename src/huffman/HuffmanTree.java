package huffman;
import java.util.PriorityQueue;

public class HuffmanTree {

    private static HuffmanNode root;

    public static void buildTree(long[] frequencies) {

        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>((a, b) -> Long.compare(a.frequency, b.frequency));

        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                queue.add(new HuffmanNode(i, frequencies[i]));
            }
        }

        while (queue.size() > 1) {

            HuffmanNode first = queue.poll();
            HuffmanNode second = queue.poll();

            HuffmanNode parent = new HuffmanNode(-1, first.frequency + second.frequency);

            parent.left = first;
            parent.right = second;

            queue.add(parent);
        }

        root = queue.poll();
    }
    ////
    ///////
    public static HuffmanNode getRoot() {
        return root;
    }
    //////
    //// ///
    ////
    public static String[] generateCodes() {

        String[] codes = new String[256];
        if (root != null) {
            generateCodes(root, "", codes);
        }

        return codes;
    }
    /////////
    private static String[] generateCodes(HuffmanNode node, String code, String[] codes) {

        if (node.left == null && node.right == null) {
            codes[node.byteValue] = code.isEmpty() ? "0" : code;
            return codes;
        }

        generateCodes(node.left, code + "0", codes);
        generateCodes(node.right, code + "1", codes);
         return codes;
    }
}
