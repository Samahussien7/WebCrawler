
package CosineSimilarity;

import java.util.*;

public class CosSimilarity {

    private List<String> score;
    private Map<Integer, Double> matrix;

    public CosSimilarity() {
        score = new ArrayList<>();
        matrix = new HashMap<>();
    }

    public List<String> calcCosSimilarity(List<String> documents, String sentence) {
        matrix.clear();
        for (int i = 0; i < 20; i++) {
            String[] terms1 = sentence.toLowerCase().split("\\W+");
            String[] terms2 = documents.get(i).toLowerCase().split("\\W+");

            Map<String, Integer> termFreq1 = calcTermFreq(terms1);
            Map<String, Integer> termFreq2 = calcTermFreq(terms2);

            double dotProduct = calcDotProduct(termFreq1, termFreq2);
            double magnitude1 = calcVectorMagnitude(termFreq1);
            double magnitude2 = calcVectorMagnitude(termFreq2);

            double res = dotProduct / (magnitude1 * magnitude2);
            matrix.put(i, res);
        }
        List<Map.Entry<Integer, Double>> sortedEntries = new ArrayList<>(matrix.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        score.clear();
        
        for (Map.Entry<Integer, Double> sortedEntry : sortedEntries) {
            score.add("document_" + (sortedEntry.getKey()) + " :    (" + sortedEntry.getValue() + ")");
        }
        return score;
    }

    public void printScore() {
        System.out.println("Top " + 10 + " documents for query: \""  );
        System.out.println("Documents        Similarity Score");
        for (int i = 0; i <  10; i++) { 
            String s = score.get(i);
            System.out.println(s);
        }
    }

    private Map<String, Integer> calcTermFreq(String[] terms) {
        Map<String, Integer> termFreq = new HashMap<>();
        for (String term : terms) {
            termFreq.put(term, termFreq.getOrDefault(term, 0) + 1);
        }
        return termFreq;
    }

    private double calcDotProduct(Map<String, Integer> termFreq1, Map<String, Integer> termFreq2) {
        double dotProduct = 0.0;
        for (String term : termFreq1.keySet()) {
            if (termFreq2.containsKey(term)) {
                dotProduct += termFreq1.get(term) * termFreq2.get(term);
            }
        }
        return dotProduct;
    }

    private double calcVectorMagnitude(Map<String, Integer> termFreq) {
        double ans = 0.0;
        for (int frequency : termFreq.values()) {
            ans += Math.pow(frequency, 2);
        }
        return Math.sqrt(ans);
    }
}
