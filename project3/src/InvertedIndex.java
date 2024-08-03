

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;


public class InvertedIndex {


    public Map<Integer,SourceRecord> sources;
    private HashMap<String, DictEntry> Inverted_Index;
    List<String> stopWords = new ArrayList<>();
    HashMap <String,Integer> [] CosineMatrix;
    public List<Integer> len ;
    public InvertedIndex() {
        this.Inverted_Index = new HashMap<>();
        this.len =new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("stopwords.txt")));
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split("\\s+");
                Collections.addAll(stopWords, words);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, DictEntry> getInverted_Index() {
        return Inverted_Index;
    }

    public static List<String> readDocuments(List<String> fileNames) {
        List<String> documents = new ArrayList<>();
        for (String fileName : fileNames) {
            String document = "";
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
                String line;
                while ((line = br.readLine()) != null) {
                    document += line + " ";
                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            documents.add(document);
        }
        return documents;
    }

    public  void buildInvertedIndex(List<String> documents) {
        int docId = 1;
        int i = 0 ;
        for (String document : documents) {
            String[] terms = document.split("\\W+");
            for (String term : terms) {
                term = term.toLowerCase();
                if(stopWords.contains(term)) {
                    continue;
                }

                DictEntry entry = Inverted_Index.get(term);
                if (entry == null) {
                    entry = new DictEntry();
                    Inverted_Index.put(term, entry);
                }
                entry.term_freq++;
                //handling posting list.
                // if term does not have posting list or term appear in another document
                //add a new post
                if (entry.pList == null || entry.pList.docId != docId) {
                    Posting posting = new Posting();
                    posting.docId = docId;
                    entry.doc_freq++;
                    posting.next = entry.pList;
                    entry.pList = posting;
                } else {
                    //the term appeared in same before document.
                    entry.pList.dtf++;
                }
            }
            docId++;
            i++;
        }

    }




    public void Display() {
        TreeMap<String, DictEntry> Index = new TreeMap<>(Inverted_Index);
        System.out.printf("%-15s %-8s %s%n", "Term", "DocFreq", "PList");

        for (Map.Entry<String, DictEntry> entry : Index.entrySet()) {
            String term = entry.getKey();
            DictEntry dictEntry = entry.getValue();
            Posting posting = dictEntry.pList;

            System.out.printf( "[ "+term+" , " +dictEntry.doc_freq+" ]");
            printPostingList(posting);
            System.out.println();
        }
    }

    private void printPostingList(Posting posting) {
        System.out.print("   ---> [");
        Vector<Integer> array = new Vector<>();
        while (posting != null) {
            array.add(posting.docId);
            posting = posting.next;
        }
        Collections.reverse(array);
        for (int i = 0; i < array.size(); i++) {
            System.out.print(array.get(i) );
            if (array.size()- i != 1)
                System.out.print(", ");
        }

        System.out.print("]");
    }

    public void printDocTermFreq(Posting posting)
    {
        Vector<Integer> array = new Vector<>();

        while (posting != null) {
            array.add(posting.dtf);
            posting = posting.next;
        }
        Collections.reverse(array);
        for (int i = 0; i < array.size(); i++) {
            System.out.print(array.get(i) );
            if (array.size()- i != 1)
                System.out.print("   ");
        }
    }

    public void search(String word) {
        // Search for a word and list all files containing the word
        DictEntry dictEntry = Inverted_Index.get(word);
        if (dictEntry == null) {
            System.out.println("No documents found containing the term " + word);
        } else {
            System.out.println("Documents containing the term { " + word + " }:");
            System.out.printf("%-8s","DocFreq");
            System.out.printf("%-9s","TermFreq");
            System.out.printf("%-8s","PList");
            System.out.println();
            System.out.printf("%-8s",dictEntry.doc_freq);
            System.out.printf("%-9s",dictEntry.term_freq);
            Posting posting = dictEntry.pList;
            printPostingList(posting);
            System.out.println();
            System.out.printf("%17s"," ");
            printDocTermFreq(posting);
            System.out.println();
        }

    }

}
