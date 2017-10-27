/* DatabaseIterator.java

   This class iterates through a directory containing txt files,
   and returns a new Article object representing a file.
   Author: Jessica McAloon (mcaloonjn@bu.edu)
 */

import java.util.*;
import java.io.*;

public class DatabaseIterator implements Iterator<Article> {

    private String directoryPath;
    private File[] children;
    private int next;


    /*An instance of DatabaseIterator takes a pathname as a parameter and
    has 3 fields:
      path: the path of the directory to be iterated through
      next: int representing if there are more files in the directory to iterate through
      children: array of pathnames of files within the directory

    */
    public DatabaseIterator(String path) {
        this.directoryPath = path;
        this.next = 0;
        this.children = findChildren(path);
    }

    public boolean hasNext() {
        return next < children.length - 1; // no files left once next = number of files in dir
    }


    /* next() returns an Article representation of the next article in
       the database, and throws an error if the next child in the children
       array can't be found
    */
    public Article next() {
        File thisFile = children[next];
        Scanner s = null;

        try {
            s = new Scanner(thisFile, "UTF-8");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("child does not exist -- " +
                                       "was it deleted?");
        }
        String title = s.nextLine();
        String body = "";

        while (s.hasNextLine())
            body += s.nextLine() + "\n";

        Article a = new Article(title, body);

        next++;
        return a;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void reset() {
        next = 0;
    }

    public int getNumArticles() {
        return children.length;
    }

    /* findChildren returns a list of pathnames for files in the directory
       or throws an appropriate exception

       @param:  path The string representing the pathname of the file
       @return: cs   The array of pathnames for files in the directory

    */
    private File[] findChildren(String path) throws IllegalArgumentException {
        File dir = new File(path);

        if (!dir.exists())
            throw new IllegalArgumentException("directory does not exist");

        if (!dir.isDirectory())
            throw new IllegalArgumentException("path does not refer to " +
                                               "a directory");
        File[] cs = dir.listFiles();

        if (cs == null)
            throw new RuntimeException("an error occured getting files " +
                                       "under directory");

        return cs;
    }
}
