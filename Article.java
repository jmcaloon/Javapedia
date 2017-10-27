/* Article.java
   A class representing a Wikipedia article with a title and
   body. An article's filename refers to its name in the local
   database of articles
   Author: Jessica McAloon (mcaloonj@bu.edu)
 */

import java.util.*;

public class Article implements Comparable<Article> {

    private String title;
    private String body;
    private String filename;
    public double cosineSimilarity = 0;

    public Article(String title, String body) {
        this.title = title;
        this.body = body;

    }

    public String getTitle() {
        return this.title;
    }

    public String getBody() {
        return this.body;
    }


/* toString converts an article to a string and formats it.
   The title's article becomes underlined with equal signs,
   and the cleanString function is called so that no line is more than
   80 characters.

   @return: s The article as a string

*/
    public String toString() {
        String t = getTitle();
        String s = t +  "\n";

        for (int i = 0; i < t.length(); i++)
            s += "=";

        s += "\n";
        s += cleanString(getBody());

        return s;
    }


    /*Tests if two articles have the same title */
    public int compareTo(Article other) {
        return this.getTitle().compareTo(other.getTitle());
    }

     /* cleanString inserts newline characters so that each line is
        less than 80 characters long. It also replaces single line
        breaks to double line breaks to represent new paragraphs.

        @param: s   The string to be formatted
        @param: out The formatted string

     */
    private String cleanString(String s) {
        String out = "";
        String[] lines = s.split("\r\n?|\n");

        int cols = 0;
        for (int i = 0; i < lines.length; i++) {
            String[] words = lines[i].split(" ");

            for (int j = 0; j < words.length; j++) {
                if (cols + words[j].length() >= 80) {
                    cols = words[j].length() + 1;
                    out += "\n" + words[j] + " ";
                } else {
                    cols += words[j].length() + 1;
                    out += words[j] + " ";
                }
            }

            cols = 0;
            out += "\n\n";
        }

        return out;
    }
}
