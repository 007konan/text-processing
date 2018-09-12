import java.util.*;
import java.io.*;

public class TreeMain{
    public static void main(String[] args) throws Exception {
        String fileName = "wiki100.txt";
        TreeMatcher t = new TreeMatcher("自然言語処理");
        FileReader fileReader = new FileReader(fileName);
        BufferedReader r = new BufferedReader(fileReader, 1);
        String query = "自然言語処理";
        String line = null;
        long start = System.currentTimeMillis();
        while ((line = r.readLine()) != null) {
            if (line.contains(query)) { 
                System.out.println(line);  
            } 
        }
        long end = System.currentTimeMillis();
        System.out.println(t);
        System.out.println((end - start)  + "ms");
        r.close(); 
    }

}