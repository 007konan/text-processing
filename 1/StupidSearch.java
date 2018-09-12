import java.io.*;
public class StupidSearch {
    public static void main(String[] args) throws Exception {
        String fileName = "wiki100.txt";
        FileReader fileReader = new FileReader(fileName);
        BufferedReader r = new BufferedReader(fileReader, 1);
        String query = "自然言語処理";
        String line = null;
        while ((line = r.readLine()) != null) {
            if (line.contains(query)) {    
                System.out.println(line);
            }   
        }
        r.close(); 
    }
}