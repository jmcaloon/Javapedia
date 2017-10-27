/* WordFrequencyTable.java
   A class that inserts the words of two documents into a separate-chaining hash table,
   and computes the  cosine similarity between the two vocabularies.
   Author: Jessica McAloon (mcaloonj@bu.edu)
 */

public class WordFrequencyTable{

  /* Node class stores each word along with its frequency
  in each document, taking on the form (string, [frequency in doc 0, frequency in doc 1]).

  @param word: The term to be inserted
  @param next: The head of the linked list that the node is being appended to (when multiple
                terms map to the same hash value)

  The node "next2" represents the head of the master list of all words that have been
  inserted into the table.
  */
  private class Node{

    String word;
    int[] wordFreq = new int[2];
    Node next;
    Node next2;

    Node (String word, Node next){
      this.word = word;
      this.next = next;
      this.next2 = null;
    }

    Node (String word){
      this.word = word;
      this.next = null;
      this.next2 = null;
    }
  }

  private final int M = 101;

  Node [] T = new Node[M];

  Node head = null;

  /* Makes an array of string words from a string so that they can be inserted
     @param:  doc The string to be broken up into words
     @return: S   Array of strings containing each word in the document
  */

  public String [] makeWordList (String doc){
    String lowerCase = doc.toLowerCase();
    String [] S = lowerCase.split(" ");
    return S;
  }

  /* Inserts a word into the Word Frequency Table. Calls insertHelper to manipulate
     the linked list at the index that the word hashes to.
     @param: word   The string to be inserted
     @param: docNum The integer (0 or 1) representing document that the word is from
  */

  public void insert(String word, int docNum) {
    boolean wasMember = member(word);   //checks if word was already in table before this insertion
    T[hash(word)] = insertHelper(word, T[hash(word)], docNum);

    if (!wasMember){  //if word wasn't already in table, insert it at the front of the master list
      Node temp = head;
      head = new Node (word, head);
      head.wordFreq[docNum] = 1;
      head.next2 = temp;
    }
    else{     //if it was in the table, just increase its wordFreq in the master list
      Node q;
      for (q = head; q != null; q = q.next){
        if (q.word.compareTo(word) == 0)
          ++q.wordFreq[docNum];
      }
    }
  }

  /* insertHelper either:
        1. creates a new node with the word if it hashes to an index with no linked list yet
        2. increases the word frequency of the word if the word is already in the
           linked list at that index
        3. inserts the word at the end of the linked list because the word has the same
           hash value as another word, but is not already in the linked list


     @param:  word   The string to be inserted
     @param:  p      The node or list of nodes at the index the word hashes to (null if none)
     @param:  docNum The integer (0 or 1) representing document that the word is from
     @return: p      The new node or list of nodes at the appropriate index
  */

  private Node insertHelper(String word, Node p, int docNum){
    if (p == null){  //if it hashes to an empty node, create a new node with the word
      p = new Node(word);
      p.wordFreq[docNum] = 1;
      return p;
    }
    else{           //if it hashes to a non-empty node
      Node q;
      for (q = p; q != null; q = q.next){
        if (q.word.compareTo(word) == 0){ // if word is already in the linked list, just increase its wordFreq
          ++q.wordFreq[docNum];
          return p;
        }
        else if (q.next == null){  // otherwise, insert word at end of LL
          q.next = new Node(word);
          q.wordFreq[docNum] = 1;
          return p;
        }

      }
    }
    return p;
  }


  Node pointer = head; // pointer to master list

  /* Returns length of linked list
     @param:  p      Head of linked list
     @return: count  The number of nodes in the linked list
  */
  private int length(Node p){
    int count = 0;
    for (Node q = p; q != null; q = q.next)
      ++ count;
    return count;
  }


  /* Make word frequency vector for a document, with a count for each
     word in the master word list

     @param:  p           Head of master list
     @param:  docNum      The integer (0 or 1) representing document that the vector is associated with
     @return: freqVector  Array of ints representing count of each word
  */
  private int [] makeVector(Node p, int docNum){
    int [] freqVector = new int [length(head)]; // vector is the same length as as the master word list
    int i = 0;
    for (Node q = p; q != null; q = q.next){ // iterate through master list, add each wordFreq to the vector
      freqVector[i] = q.wordFreq[docNum];
      ++ i;
    }
    return freqVector;
  }

  /* Returns double representing cosine similarity between two word frequency vectors */
  double cosineSimilarity(){
    int [] A = makeVector(head, 0); // make wordFreq vector of document 0
    int [] B = makeVector(head, 1); // make wordFreq vector of document 1
    double result = dot(A,B)/((Math.sqrt(dot(A,A)))*(Math.sqrt(dot(B,B)))); //calculate cosine similarity of vectors
    return result;
  }


/* Returns double representing the dot product of 2 vectors*/
  private double dot(int [] A, int [] B){ //calculates dot product
    double sum = 0;
    for (int i = 0; i < A.length; ++ i){ //iterates through 2 vectors, takes the product of 2 indexes, adds it to running sum
      double product = A[i]*B[i];
      sum += product;
    }
    return sum;
  }


/* lookup calls lookupHelper to see if a word already has a node in the table
   @param: word The string to search for
   @return: word if found, null otherwise
*/
  public String lookup(String word) {
    return lookupHelper (word, T[hash(word)]);

  }

  private String lookupHelper(String word, Node t) {
    if (t == null)
      return null;
    for (Node p = t; p != null;p = p.next){
      if (p.word.compareTo(word) == 0)
        return p.word;
    }
    return null;
  }

 /* returns true if string is in table, returns false otherwise */
  public boolean member (String title){
    return (lookup(title) != null);
  }


  /* hash function takes sum of chars in a string and applies % M to it
     @param: word The string to be hashed
     @return: integer hash value
  */
  private int hash (String word){
    char ch[];
    ch = word.toCharArray();
    int titlelength = word.length();

    int i, sum;
    for (sum=0, i=0; i < word.length(); i++)
      sum += ch[i];
    return sum % M;
  }

  /*initialize inserts a string array into the table, useful for debugging */
  public void initialize(String [] A, int docNum) {
    for(int i = 0; i < A.length; ++i)
      insert(A[i], docNum);
  }


  /*iterator methods for debugging. Prints out the contents of the master list using next2 pointer */
   public void reset(){
    pointer = head;
  }

  public boolean hasNext(){
    return (pointer != null);
  }

  public String next(){
    Node q = pointer;
    pointer = pointer.next2;
    return q.word;
  }

  /* Blacklist common words that aren't used in cosine similarity calculation */
  private final String [] blackList = { "the", "of", "and", "a", "to", "in", "is",
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


  /* Main method with unit tests */
  public static void main (String [] args){
    WordFrequencyTable Table = new WordFrequencyTable();
    String [] A = Table.makeWordList("A B");

    String [] B = Table.makeWordList("A A B B");

    System.out.println("Testing Insert, should be:");
    System.out.println("Location 97: a [1, 2]\nLocation 98: b [1, 2]\n");
    Table.initialize(A,0);
    Table.initialize(B,1);
    Node [] list = Table.T;
    for (int i = 0; i < list.length; ++i){
      if (list[i] != null){
        for (Node p = list[i]; p != null; p = p.next){
          System.out.println( "Location " + Table.hash(p.word)+": "+ p.word +" ["+ p.wordFreq[0] +", "+p.wordFreq[1] +"]");
        }
      }
    }

    System.out.println();
    System.out.println("Testing iterator and next2 pointer, should be:");
    System.out.println("b a");
    Table.reset();
    while(Table.hasNext()) {
      String s = Table.next();
      System.out.print(s + " ");
    }

    System.out.println();
    System.out.println();

    System.out.println("Testing cosine similarity of \"A B\" and \"A A B B\", shoud be about 1.0:");
    System.out.println(Table.cosineSimilarity());
    System.out.println();

    WordFrequencyTable Table2 = new WordFrequencyTable();

    String [] C = Table2.makeWordList("A B");
    String [] D = Table2.makeWordList("C D");

    Table2.initialize(C,0);
    Table2.initialize(D,1);

    System.out.println("Testing cosine similarity of \"A B\" and \"C D\", shoud be 0.0:");
    System.out.println(Table2.cosineSimilarity());
    System.out.println();


  }
}
