/* Javapedia.java
   A program that uses the DatabaseIterator
   and Article classes, along with other data
   structures, to allow a user to create, modify
   and interact with a local database of txt files.
   Author: Jessica McAloon (mcaloonj@bu.edu)
 */

import java.util.*;
import java.lang.StringBuilder;


public class Javapedia {

  /* getArticleList returns an array of articles from
     a DatabaseIterator object

     @param:  db    The DatabaseIterator object
     @return: list The list of articles found by db
  */
  private static Article[] getArticleList(DatabaseIterator db) {

    // Count how many articles are in the directory
    int count = db.getNumArticles();

    // Create array
    Article[] list = new Article[count];
    for(int i = 0; i < count; ++i)
      list[i] = db.next();

    return list;
  }


  /* Set up database iterator to iterate through files in the given path */
  private static DatabaseIterator setupDatabase(String path) {
    return new DatabaseIterator(path);
  }


  /* Add an article to the encyclopedia.

    @param: s The scanner to read user input
    @param: T The ArticleHashTable object that stores articles

  */
  private static void addArticle(Scanner s, ArticleHashTable T) {
    System.out.println();
    System.out.println("Add an article");
    System.out.println("==============");

    System.out.print("Enter article title: ");
    String title = s.nextLine();

    System.out.println("You may now enter the body of the article.");
    System.out.println("Press return two times when you are done.");

    String body = "";
    String line = "";
    do {
      line = s.nextLine();
      body += line + "\n";
    } while (!line.equals(""));

    T.insert(new Article(title, body));
  }

  /* Remove an article from the encyclopedia.

    @param: s The scanner to read user input
    @param: T The ArticleHashTable object that stores articles

  */
  private static void removeArticle(Scanner s, ArticleHashTable T) {
    System.out.println();
    System.out.println("Remove an article");
    System.out.println("=================");

    System.out.print("Enter article title: ");
    String title = s.nextLine();


    T.delete(title);
  }

 /* Search article database via title. Prints
     the article if found. Title must
    be an exact match for the article to found.

    @param: s The scanner object for user input
    @param: T The ArticleHashTable with the articles to be searched

 */
  private static void titleSearch(Scanner s, ArticleHashTable T) {
    System.out.println();
    System.out.println("Search by article title");
    System.out.println("=======================");

    System.out.print("Enter article title: ");
    String title = s.nextLine();

    Article a = T.lookup(title);
    if(a != null)
      System.out.println(a);
    else {
      System.out.println("Article not found!");
      return;
    }

    System.out.println("Press return when finished reading.");
    s.nextLine();
  }

 /* Preprocess a string to get its cosine similarity to another string*/
  private static String preprocess(String s) {
    s = s.toLowerCase();        // turns s into lowercase
    char [] C = s.toCharArray(); // convert to array of chars
    String new_string = "";
    for (int i = 0; i < C.length; ++ i){
      if (Character.isLetterOrDigit(C[i]) || Character.isWhitespace(C[i])) // iterate through array, add char to array
        new_string += C[i];                                                // if it is a letter, digit, or whitespace
    }
    return new_string;
  }

  private static boolean contains(String term, String [] S){ //tests if a string is in an array of strings
    for (int i = 0; i < S.length; ++ i){                   // return true if found
      if (term.compareTo(S[i]) == 0)
        return true;
    }
    return false;                                           // return false otherwise
  }

  private static boolean blacklisted(String s) { //returns true if string is in blacklist
    return contains(s, blackList);
  }

  /* getCosineSimilarity takes in two strings and returns a double representing their
     cosine similarity
  */
  private static double getCosineSimilarity(String s, String t) {
    s = preprocess(s); // preprocess both strings in order to compare them
    t = preprocess(t);
    String [] S = s.split(" "); // turn both strings into array, splitting on whitespace
    String [] T_list = t.split(" ");
    WordFrequencyTable X = new WordFrequencyTable();
    for (int i = 0; i < S.length; ++ i){  // insert word into table if it isn't blacklisted
      if (!blacklisted(S[i]))
        X.insert(S[i], 0);
    }
    for (int i = 0; i < T_list.length; ++ i){ // do the same for document 1
      if (!blacklisted(T_list[i]))
        X.insert(T_list[i], 1);
    }
    return X.cosineSimilarity();
  }


  /*phraseSearch returns the top 3 articles with the highest
    cosine similarity to the search phrase.

    @param:  phrase The string representing the search phrase
    @param:  T      The ArticleHashTable object to be searched
    @return: s      The string containing the titles and bodies of the top 3 articles

  */
  public static String phraseSearch(String phrase, ArticleHashTable T) {

    MaxHeap Heap = new MaxHeap();

    T.reset();
    while(T.hasNext()) { // for each article, get the cosine similarity of the search phrase and article's body
      Article a = T.next();
      a.cosineSimilarity = getCosineSimilarity(phrase, a.getBody());
      double score = a.cosineSimilarity;
      if (score > 0.001){         // insert similarity score into MaxHeap if score > 0.001
        Heap.insert(a);
      }
    }

    String s = "";
    if (Heap.isEmpty()){          // heap is empty, return proper message
      s =("\nNo matching articles found!");
    }

    else{
      if (Heap.size() < 3){ // if fewer than 3 articles have been added to the heap, return all of the articles, in order of ascending similarity score
        s += "Top " + Heap.size() + " Matches:\n\n";
        for (int i = 0; i < Heap.size(); ++i){
          Article match = Heap.getMax();
          s+= "Match " + (i + 1) + " with cosine similairty of " + match.cosineSimilarity + ":\n\n" + match;
        }
      }
      else{    // if heap size  is >= 3, just return the top 3 scores
        s += "Top " + 3 + " Matches:\n\n";
        for (int i = 0; i < 3; ++i){
          Article match = Heap.getMax();
          s+= "Match " + (i + 1) + " with cosine similairty of " + match.cosineSimilarity + ":\n\n" + match;
        }
      }
    }
    return s;
  }


 /*Blacklist common words that aren't used in cosine similairty computation  */
  private static final String [] blackList = { "the", "of", "and", "a", "to", "in", "is",
    "you", "that", "it", "he", "was", "for", "on", "are", "as", "with",
    "his", "they", "i", "at", "be", "this", "have", "from", "or", "one",
    "had", "by", "word", "but", "not", "what", "all", "were", "we", "when",
    "your", "can", "said", "there", "use", "an", "each", "which", "she",
    "do", "how", "their", "if", "will", "up", "other", "about", "out", "many",
    "then", "them", "these", "so", "some", "her", "would", "make", "like",
    "him", "into", "time", "has", "look", "two", "more", "write", "go", "see",
    "number", "no", "way", "could", "people",  "my", "than", "first", "water",
    "been", "call", "who", "oil", "its", "now", "find", "long", "down", "day",
    "did", "get", "come", "made", "may", "part" };


/* Main method providing user interface */
  public static void main(String[] args) {

    Scanner user = new Scanner(System.in);

    String dbPath = "articles/";

    DatabaseIterator db = setupDatabase(dbPath);

    System.out.println("Read " + db.getNumArticles() +
                       " articles from disk.");

    ArticleHashTable L = new ArticleHashTable();
    Article[] A = getArticleList(db);
    L.initialize(A);

    int choice = -1;
    do {
      System.out.println();
      System.out.println("Welcome to Javapedia!");
      System.out.println("=====================");
      System.out.println("Make a selection from the " +
                         "following options:");
      System.out.println();
      System.out.println("    1. add a new article");
      System.out.println("    2. remove an article");
      System.out.println("    3. Search by article title");
      System.out.println("    4. Search by phrase(list of keywords)");
      System.out.println();
      System.out.println();

      System.out.print("Enter a selection (1-4, or 0 to quit): ");

      choice = user.nextInt();
      user.nextLine();

      switch (choice) {
        case 0:
          return;

        case 1:
          addArticle(user, L);
          break;

        case 2:
          removeArticle(user, L);
          break;

        case 3:
          titleSearch(user, L);
          break;

        case 4:
          System.out.println();
          System.out.println("Search by article content");
          System.out.println("=======================");
          System.out.print("Enter search phrase: ");
          String phrase = user.nextLine();
          System.out.println(phraseSearch(phrase,L));
          break;

        default:
          break;
      }

      choice = -1;

    } while (choice < 0 || choice > 4);

  }



}
