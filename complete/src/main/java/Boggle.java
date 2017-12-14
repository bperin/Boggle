
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Boggle {

    static final int MAX_FAN_OUT = 26;
    static int N;
    static ArrayList<String> foundWords = new ArrayList<>();
    static long startTime;

    /**
     * Takes args for relative string paths for dictionary and problem
     * loads the text files into array and starts creating the Trie
     * If matches are made they are outputted to a text file
     */
    public static void main(String args[]) {

        try {

            startTime = System.currentTimeMillis();

            String dictionary[] = loadDictionary(args[0]);
            char board[][] = loadBoard(args[1]);

            TrieNode rootNode = new TrieNode();

            // insert all words of dictionary into Trie omit words less than 3 or larger than the board
            int n = dictionary.length;
            int maxLength = N * N;

            for (int i = 0; i < n; i++) {
                String word = dictionary[i];
                if (word.length() < 3 || word.length() > maxLength)
                    continue;

                insert(rootNode, dictionary[i]);
            }

            findWords(board, rootNode);

            long endTime = System.currentTimeMillis();
            long timeDiff = endTime - startTime;

            System.out.println(
                    "FOUND  " + String.valueOf(foundWords.size()) + " words in " + String.valueOf(timeDiff) + " ms");

            if (foundWords.size() > 0) {
                writeOutput();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Our class to hold a Trie Node
     * with an array backed list for children
     */
    static class TrieNode {

        private TrieNode[] children = new TrieNode[MAX_FAN_OUT];
        private boolean leaf = false;

        public TrieNode() {

        }
    }

    /**
     * Inserts a node into roots parent children if it doesn't exist
     * Mark as an end node/leaf after
     */
    static void insert(TrieNode root, String word) {

        int n = word.length();

        for (int i = 0; i < n; i++) {

            int c = word.charAt(i) - 'A';

            if (root.children[c] == null) {
                root.children[c] = new TrieNode();
            }
            root = root.children[c];
        }
        root.leaf = true;
    }

    /**
     * Recursively searches for a leaf node word by traversing all children of the current node and appending
     * the chars to the search
     */
    static void searchWord(TrieNode root, char boggle[][], int i, int j, boolean visited[][], String str) {
        // if we found word in trie / dictionary
        if (root.leaf == true && !foundWords.contains(str)) {
            foundWords.add(str);
        }

        if (validMove(i, j, visited)) {

            visited[i][j] = true;

            //delta movements across board
            int[] dx = { 1, 1, 0, -1, -1, -1, 0, 1 };
            int[] dy = { 0, 1, 1, 1, 0, -1, -1, -1 };

            for (int c = 0; c < MAX_FAN_OUT; c++) {

                if (root.children[c] != null) { //node exists

                    char ch = (char) (c + 'A');

                    for (int l = 0; l < 8; l++) {
                        int x = i + dx[l];
                        int y = j + dy[l];

                        if (validMove(x, y, visited) && boggle[x][y] == ch) {
                            searchWord(root.children[c], boggle, x, y, visited, str + ch);
                        }
                    }
                }
            }

            visited[i][j] = false;//unvisit
        }
    }

    /**
    * check if this move has been visited and if its within our array index
    */
    static boolean validMove(int i, int j, boolean visited[][]) {
        return (i >= 0 && i < N && j >= 0 && j < N && !visited[i][j]);
    }

    /**
     * Traverses our boggle char array combining all permutations and searches the word list
     * 
     */
    static void findWords(char boggle[][], TrieNode root) {

        boolean[][] visited = new boolean[N][N];

        String str = "";

        // traverse all matrix elements
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (root.children[(boggle[i][j]) - 'A'] != null) {
                    str = str + boggle[i][j];
                    searchWord(root.children[(boggle[i][j]) - 'A'], boggle, i, j, visited, str);
                    str = "";
                }
            }
        }
    }

    /**
     * Loads our board into a multi dimensional char array
     * File is expected to be specifically formatted with line number at top a break and letters in a grid with spaces
     * Sets the size of the board
     */
    private static char[][] loadBoard(String path) throws IOException {

        File file = new File(path);

        java.util.List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);

        String letterSize = lines.get(0);

        N = Integer.valueOf(letterSize);

        char[][] board = new char[N][N];

        for (int i = 2; i < lines.size(); i++) {

            String line = lines.get(i);

            if (line.isEmpty())
                break;

            String[] letters = line.split("\\s+");

            for (int j = 0; j < letters.length; j++) {

                String letter = letters[j];

                char c = letter.charAt(0);

                board[i - 2][j] = letter.charAt(0);
            }
        }

        return board;
    }

    /**
     * Reads our list of dictionary words and outputs a string array
     */
    private static String[] loadDictionary(String path) throws IOException {

        File file = new File(path);

        List<String> words = FileUtils.readLines(file, StandardCharsets.UTF_8);
        if (words != null) {

            String[] dictionary = new String[words.size()];

            Collections.sort(words);

            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                dictionary[i] = words.get(i).toUpperCase();
            }
            return dictionary;
        }
        return null;
    }

    /**
     * Helper method to write output
     */
    private static void writeOutput() throws IOException {

        String outputName = "solution.txt";
        File previousSolution = new File(outputName);
        
        FileUtils.deleteQuietly(previousSolution);

        FileWriter writer = new FileWriter(outputName);

        for (String word : foundWords) {
            writer.write(word + "\n");
        }
        writer.close();
    }
}