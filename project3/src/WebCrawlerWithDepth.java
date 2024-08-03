import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import CosineSimilarity.*;
import java.io.*;
import java.util.*;

public class WebCrawlerWithDepth {
    private static final int MAX_DEPTH = 2;
    private static final int MAX_PER_PAGE = 6;
    int max_docs = 20;
    private HashSet<String> links;
    Map<Integer, SourceRecord> sources;
    int fid = 0;
    int plinks = 0;

    public WebCrawlerWithDepth() {
        links = new HashSet<>();
        sources = new HashMap<>(); // Initialize sources here
        fid = 0;
    }

    public WebCrawlerWithDepth(InvertedIndex in) {
        links = new HashSet<>();
        sources = in.sources;
        fid = 0;
    }

    public void setSources(InvertedIndex in) {
        sources = in.sources;
    }

    public String getText(Document document) {
        String pAcc = "";
        Elements p = document.body().getElementsByTag("p");

        for (Element e : p) {
            pAcc += e.text();
        }
        return pAcc;
    }

    public void saveContentToFile(String content, String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing content to file: " + e.getMessage());
        }
    }

    public void getPageLinks(String URL, int depth, InvertedIndex index) {
        System.out.println("|| URL: [" + URL + "] --------  depth: " + depth + " fid=" + fid + " plinks=" + plinks + "\t|||| ");

        if ((!(links.contains(URL)))
                && (depth < MAX_DEPTH)
                && (fid < max_docs)
                && ((depth == 0)
                || ((depth == 1) && (plinks < ((MAX_PER_PAGE) + 290)))
                || (plinks < ((MAX_PER_PAGE * (depth + 1)) - (plinks / 2))))
                && (!URL.contains("https://.m."))
                && (URL.contains("https://en.w"))
                && (!URL.contains("wiki/Wikipedia"))
                && (!URL.contains("searchInput"))
                && (!URL.contains("wiktionary"))
                && (!URL.contains("#"))
                && (!URL.contains(","))
                && (!URL.contains("Wikiquote"))
                && (!URL.contains("disambiguation"))
                && (!URL.contains("w/index.php"))
                && (!URL.contains("wikimedia"))
                && (!URL.contains("/Privacy_policy"))
                && (!URL.contains("Geographic_coordinate_system"))
                && (!URL.contains(".org/licenses/"))
                && ((!URL.substring(12).contains(":")) || (depth == 0))
                && (!URL.isEmpty())
                && (!URL.contains("Main_Page"))
                && (!URL.contains("mw-head"))) {
            try {
                links.add(URL);
                Document document = Jsoup.connect(URL).get();
                Elements linksOnPage = document.select("a[href]");

                String docText = getText(document);

                // Save content to file
                String fileName = "document_" + fid + ".txt";
                saveContentToFile(docText, fileName);

                // Store source record
                SourceRecord sr = new SourceRecord(fid, URL, document.title(), docText.substring(0, Math.min(docText.length(), 30)));
                sr.length = docText.length();
                sources.put(fid, sr);

                plinks++;
                fid++;

                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"), depth + 1, index);
                }
             plinks--;
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
        
    }


    public static void main(String[] args) throws IOException {

        String seedUrl = "https://en.wikipedia.org/wiki/List_of_pharaohs";
        //String seedUrl="https://en.wikipedia.org/wiki/Cairo";
        WebCrawlerWithDepth crawler = new WebCrawlerWithDepth();

        InvertedIndex index = new InvertedIndex(); 

        CosSimilarity cosine= new CosSimilarity();

        crawler.getPageLinks(seedUrl, 0, index);

        // Print out the URLs found
        System.out.println("All URLs found:");
        for (String url : crawler.links) {
            System.out.println(url);
        }


        List<String> fileNames = new ArrayList<>();
        for (int i = 0; i <= 19; i++) {
            fileNames.add("document_" + i + ".txt");
        }

        List<String> documents = InvertedIndex.readDocuments(fileNames);

        InvertedIndex invertedIndex = new InvertedIndex();
        invertedIndex.buildInvertedIndex(documents);
        int n;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 1=> display inverted index ");
        System.out.println("Enter 2=> Search for term ");
        System.out.println("Enter 3=> Enter query ");
        System.out.println("Enter 0=> Finish ");
        n = scanner.nextInt();


        while(n!=0) {
            if (n == 1) {
                invertedIndex.Display();
                System.out.println();
            } else if (n == 2) {
                String word;
                System.out.print("Enter word: \n");
                word = scanner.next();
                word=word.toLowerCase();
                invertedIndex.search(word);
                System.out.println();
            }
            else if (n == 3) {

                Scanner in = new Scanner(System.in);
                System.out.print("Enter query: \n");
                String query = in.nextLine();

                cosine.calcCosSimilarity(documents, query);

                cosine.printScore();
                System.out.println();
            }
            else
                System.out.println("Enter valid number\n ");


            System.out.println("Enter 1=> display inverted index ");
            System.out.println("Enter 2=> Search for term ");
            System.out.println("Enter 3=> Enter query ");
            System.out.println("Enter 0=> Finish ");
            n = scanner.nextInt();


        }
    }

}
