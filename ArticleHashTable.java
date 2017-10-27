 /*ArticleHashTable.java
 A class that inserts an array of articles into a separate-chaining hash table.
 Also creates a master linked list of all articles in the table.
 Author: Jessica McAloon (mcaloonj@bu.edu)
 */

public class ArticleHashTable{

  /* Node class stores each article with its title as the key.

  @param datum: The article to be inserted
  @param next: The head of the linked list that the node is being appended to (when multiple
                titles map to the same hash value)

  The node "next2" represents the head of the master list of all articles that have been
  inserted into the table.
  */

  private class Node {
    String key;
    Article datum;
    Node next;
    Node next2;

   Node(Article datum, Node next) {
      this.key = datum.getTitle();
      this.datum = datum;
      this.next = next;
      this.next2 = null;
    }

   Node(Article datum) {
      this(datum, null);
    }
  }


  public void initialize(Article [] A) { // inserts an array of articles into ArticleHashTable
    for(int i = 0; i < A.length; ++i)
      insert(A[i]);
  }


  private final int M = 2503;

  public int getM(){
    return M;
  }

  Node [] T = new Node[M];


  /*takes sum of chars in the article's title, and returns sum % M
    @param: a The Article object to be inserted into the table
    @return: integer representing the hash value of the article
  */
  private int hash (Article a) { // takes sum of chars in the article's title, and returns sum % M
    String s = a.getTitle();
    char ch[];
    ch = s.toCharArray();
    int slength = s.length();

    int i, sum;
    for (sum=0, i=0; i < s.length(); i++)
      sum += ch[i];
    return sum % M;
  }

  private int hash (String title){ // Same as previous, but takes string title as input
    char ch[];
    ch = title.toCharArray();
    int titlelength = title.length();

    int i, sum;
    for (sum=0, i=0; i < title.length(); i++)
      sum += ch[i];
    return sum % M;
  }


  Node head = null;

  /* Inserts an article into the Article Hash Table. Calls insertHelper to manipulate
     the linked list at the index that the article hashes to.
     @param: a   The Article object to be inserted
  */

  public void insert (Article a){
    if (!member(a.getTitle())){ // if not already in table, insert to proper hash location
      T[hash(a)] = insertHelper(a, T[hash(a)]);
      Node temp = head;
      head = new Node (a, head); // add article to front of master list
      head.next2 = temp;       // update next2 to point to rest of master list
    }
  }

/* insertHelper creates a new node with the article if it hashes to an index
   with no linked list yet. Otherwise, it appends the article to the end of the linked list

   @param: a The Article object to be inserted
   @param: t The node or list of nodes at the index the article hashes to (null if none)

*/
  public Node insertHelper(Article a, Node t){
    if (t == null) // if hash location is null, create a new node
      t = new Node(a);
    else{
      for (Node p = t; p != null; p = p.next){ // else add it to the end
        if (p.next == null){
          p.next = new Node(a);
          return t;
        }
      }
    }
    return t;
  }

  public void delete(String title) {
    T[hash(title)] = deleteHelper(title, T[hash(title)]); // delete from proper hash location
    head = deleteML(title, head);                         // delete from master list
  }

  private Node deleteHelper(String title, Node t){
    if (t == null)                                   // if title not in table, do nothing
      ;
    else if (t.datum.getTitle().compareTo(title) == 0) // if title is in the first node, reassign next to delete
      t = t.next;
    else{
      Node p;
      Node q;
      for (p = t, q = p; p != null; q = p, p = p.next){ // else, search for title in list using inchworm technique
        String s = p.datum.getTitle();
        if (s.compareTo(title) == 0)
          q.next = p.next;
      }
    }
    return t;
  }

/* Given title, delete that article from master list of articles */
  private Node deleteML(String title, Node t){
    if (t == null) // do nothing if title is not in list
      ;
    else if ((t.datum.getTitle().compareTo(title) == 0))
      head = head.next2; // if title is in the first node, reassign next2 to delete
    else{                // else, search for title in list using inchworm technique
      Node p;
      Node q;
      for (p = t, q = p; p != null; q = p, p = p.next2){
        String s = p.datum.getTitle();
        if (s.compareTo(title) == 0)
          q.next2 = p.next2;
      }
    }
    return head;
  }


  public Article lookup(String title) {
    return lookupHelper (title, T[hash(title)]);

  }

  private Article lookupHelper(String title, Node t) {
    if (t == null) // if hash location is empty, return null
      return null;
    for (Node p = t; p != null;p = p.next){ // else, search through LL and return the node's datum when found
      String s = p.datum.getTitle();
      if (s.compareTo(title) == 0)
        return p.datum;
    }
    return null;                            // return null if not found
  }

  public boolean member (String title){
    return (lookup(title) != null);
  }

  //Iterator methods
  Node pointer = head;

  public void reset(){ // resets pointer to point to head of master list
    pointer = head;
  }

  public boolean hasNext(){
    return (pointer != null); // iteration will stop when pointer reaches a null
  }

  public Article next(){      // returns node' datum and moves pointer down the list
    Node q = pointer;
    pointer = pointer.next2;
    return q.datum;
  }


/* Main method with unit tests */
  public static void main(String[] args) {
    ArticleHashTable Table = new ArticleHashTable();
    Article Cars = new Article("Cars", "wheels are nice");
    Article Cras = new Article("Cras", "wheels are nice");
    Article Beatles = new Article("The Beatles", "Paul, Ringo, George, John");

    Article [] A = new Article [3];
    A[0] = Cars;
    A[1] = Cras;
    A[2] = Beatles;

    System.out.println("Testing hash(Article):\n");

    System.out.println("Should hash to different locations:");
    System.out.println("Cars hashes to " +Table.hash(Cars));
    System.out.println("Beatles hashes to " +Table.hash(Beatles));
    System.out.println();

    System.out.println("Should hash to same location:");
    System.out.println("Article 1 hashes to " +Table.hash(Cars));
    System.out.println("Article 2 hashes to " + Table.hash(Cras));
    System.out.println();

    System.out.println("Testing hash(String):\n");

    System.out.println("Should hash to different locations:");
    System.out.println("Cars hashes to " +Table.hash("Cars"));
    System.out.println("The Beatles hashes to " +Table.hash("The Beatles"));
    System.out.println();
    System.out.println("Should hash to same location:");
    System.out.println("Cars hashes to " +Table.hash("Cars"));
    System.out.println("Cras hashes to " + Table.hash("Cras"));
    System.out.println();

    System.out.println("Testing insert (should contain Cars, Cras, and the Beatles):\n");
    Table.initialize(A);
    Node [] list = Table.T;
    for (int i = 0; i < list.length; ++i){
      if (list[i] != null){
        for (Node p = list[i]; p != null; p = p.next){
          String title = p.datum.getTitle();
          System.out.println( "Location " + Table.hash(title) +":\n" + p.datum);
        }
      }
    }


    System.out.println("Testing delete:\n");

    Table.delete("Cars");
    System.out.println("Deleting Cars:\n");
    for (int i = 0; i < list.length; ++i){
      if (list[i] != null){
        for (Node p = list[i]; p != null; p = p.next){
          String title = p.datum.getTitle();
          System.out.println( "Location " + Table.hash(title) +":\n" + p.datum);
        }
      }
    }

    Table.delete("The Beatles");
    System.out.println("Deleting The Beatles:\n");
    for (int i = 0; i < list.length; ++i){
      if (list[i] != null){
        for (Node p = list[i]; p != null; p = p.next){
          String title = p.datum.getTitle();
          System.out.println( "Location " + Table.hash(title) +":\n" + p.datum);
        }
      }
    }

    System.out.println("Testing Lookup:\n");

    System.out.println("Should find Cras:");
    System.out.println(Table.lookup("Cras"));

    System.out.println("Should not find The Beatles:");
    System.out.println(Table.lookup("The Beatles") + "\n");

    System.out.println("Testing Member:\n");

    System.out.println("Cras should return true:");
    System.out.println(Table.member("Cras") + "\n");

    System.out.println("The Beatles should return false:");
    System.out.println(Table.member("The Beatles") + "\n");

    Table.delete("Cras");

    System.out.println("Testing iterator: Should print Articles A-E in reverse order");

    Article A1 = new Article("A", "Article 1");
    Article A2 = new Article("B", "Article 2");
    Article A3 = new Article("C", "Article 3");
    Article A4 = new Article("D", "Article 4");
    Article A5 = new Article("E", "Article 5");

    Article [] B = new Article [5];
    B[0] = A1;
    B[1] = A2;
    B[2] = A3;
    B[3] = A4;
    B[4] = A5;

    Table.initialize(B);


    Table.reset();
    while(Table.hasNext()) {
      Article a = Table.next();
      System.out.println(a);
}



  }
}
