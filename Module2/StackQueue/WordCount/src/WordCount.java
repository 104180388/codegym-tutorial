import java.util.TreeMap;
import java.util.Map;

public class WordCount {
    public static void main(String[] args) {
        String input = "Learning Queue and Stack and Queue";

        TreeMap<String, Integer> wordMap = new TreeMap<>();

        String[] words = input.split("\\s+");

        for (String mWord : words) {
            mWord = mWord.toLowerCase();
            mWord = mWord.replaceAll("[^a-zA-Z0-9à-ỹ]", "");

            if (mWord.isEmpty()) continue;
            if (wordMap.containsKey(mWord)) {
                int count = wordMap.get(mWord);
                wordMap.put(mWord, count + 1);
            } else {
                wordMap.put(mWord, 1);
            }
        }

        System.out.println("Kết quả đếm từ (đã sắp xếp A-Z):");
        for (Map.Entry<String, Integer> entry : wordMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}