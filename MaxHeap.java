/*MaxHeap.java

  Implements a MaxHeap tree to store articles that have a cosine
  similarity of > .001 with the search phrase.

 * Author: Jessica McAloon (mcaloonj@bu.edu)
 */
public class MaxHeap{

  /* MaxHeap tree implemented as a max priority queue*/

    private final int SIZE = 10;       // initial length of array
    private int next = 0;              // limit of elements in array
    private Article [] A = new Article [SIZE];   // implements tree by storing elements in level order

    /*Resizes array to avoid overflow */
    private void resize() {
      Article [] B = new Article [A.length*2];
      for(int i = 0; i < A.length; ++i)
        B[i] = A[i];
      A = B;
    }

    /* Functions to move up and down tree as array */

    private int parent(int i) { return (i-1) / 2; }
    private int lchild(int i) { return 2 * i + 1; }
    private int rchild(int i) { return 2 * i + 2; }

    private boolean isLeaf(int i) { return (lchild(i) >= next); }
    private boolean isRoot(int i) { return i == 0; }

    /* Standard swapping of elements in array */
    private void swap(int i, int j) {
      Article temp = A[i];
      A[i] = A[j];
      A[j] = temp;
    }

   public boolean isEmpty() {
      return (next == 0);
   }

   public int size() {
      return (next);
   }

   /* Inserts an article into an array at the next available location,
      and fixes violations of heap property from path to root

      @param: a The article object to be inserted

   */
   public void insert(Article a) {
      if(size() == A.length) resize();
      A[next] = a;

      int i = next;
      int p = parent(i);
      while(!isRoot(i) && A[i].cosineSimilarity > A[p].cosineSimilarity) {
         swap(i,p);
         i = p;
         p = parent(i);
      }

      ++next;
   }


   /* Removes article with highest score, and replace with last article in level
       order. Fixes any violations of heap property on a path downwards.
  */

   public Article getMax() {
      --next;
      swap(0,next);                // swap root with last element
      int i = 0;                   // i is location of new key as it moves down tree

      // while there is a maximum child and element out of order, swap with max child
      int mc = maxChild(i);
      while(!isLeaf(i) && A[i].cosineSimilarity < A[mc].cosineSimilarity) {
         swap(i,mc);
         i = mc;
         mc = maxChild(i);
      }


      return A[next];
   }

   /* Returns index of max child of i or -1 if i is a leaf node

      @param: i The index representing the parent node
   */

   int maxChild(int i) {
      if(lchild(i) >= next)
         return -1;
      if(rchild(i) >= next)
         return lchild(i);
      else if(A[lchild(i)].cosineSimilarity > A[rchild(i)].cosineSimilarity)
         return lchild(i);
      else
         return rchild(i);
   }

   /* Applies heapsort to the array A. To use, fill A with keys and then call heapsort */

   public  void heapSort() {
      next = 0;
      for(int i = 0; i < A.length; ++i)      // turn A into a heap
         insert(A[i]);
      for(int i = 0; i < A.length; ++i)      // delete root A.length times to swap max into
         getMax();                           //  right side of the array
   }


/*Methods for printing and debugging heap */

   private void printHeap() {
      for(int i = 0; i < A.length; ++i)
         System.out.print(A[i] + " ");
      System.out.println("\t next = " + next);
   }

   private void printHeapAsTree() {
      printHeapTreeHelper(0, "");
      System.out.println();
   }

   private void printHeapTreeHelper(int i, String indent) {
      if(i < next) {

         printHeapTreeHelper(rchild(i), indent + "   ");
         System.out.println(indent + A[i]);
         printHeapTreeHelper(lchild(i), indent + "   ");
      }
   }
    }
