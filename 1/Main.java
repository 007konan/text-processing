import java.util.*;
import java.io.*;

public class Main{
    public static void main(String[] args) throws Exception{
        String filename = "wiki100.txt";
        FileReader filereader = new FileReader(filename);
        BufferedReader r = new BufferedReader(filereader);
        String query = "自然言語処理";
        String data = null;
        ArrayList<String> list = new ArrayList<String>();
        long start = System.currentTimeMillis();
        while((data = r.readLine())!=null){
            list.add(data);
            if (data.contains(query)) {    
                System.out.println(data);
            }  
        }
        /*while(Arrays.asList(filename).contains(query)){
            System.out.println(r.readLine());
        }*/
        long end = System.currentTimeMillis();
        String[] array = list.toArray(new String[list.size()]);
        TreeMatcher treematcher = new TreeMatcher(array);
        
        if(treematcher.match(query)){
            System.out.println(data);
        }
        
        System.out.println((end - start)  + "ms");
        
    }
}