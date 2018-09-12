import java.io.*;
import java.util.*;

public class Search {
    
 public static Map<Integer,Double> similarity(String query) {
	Map<Integer,Double> sim = new TreeMap<>();
	try {
	    File file = new File("tfidf100.csv");
	    BufferedReader br = new BufferedReader(new FileReader(file));
       	    
	    String line = "";
	    line = br.readLine();
	    String[] num = line.split(",",0);
	    int N = Integer.parseInt(num[0]);    //文書数
	    int term = Integer.parseInt(num[1]); //索引語数
	 
	    String[] termList = new String[term];
	    
	    int term_num = 0;
	    Map<Integer,TreeMap<Integer,Double>> link = new TreeMap<>();
	    while ((line = br.readLine()) != null) {
		String[] data = line.split(",",0);		
		termList[term_num] = data[0];

		for (int i = 1; i < data.length; i++){
		    String[] data2 = data[i].split(" ",0);
		    TreeMap<Integer,Double> tmap = link.get(Integer.parseInt(data2[0]));
		    if (tmap == null) {
			tmap = new TreeMap<Integer,Double>();
		    }
		    tmap.put(term_num , Double.parseDouble(data2[1]));
		    link.put(Integer.parseInt(data2[0]) , tmap);
		}
		term_num++;
	    }
		    
	    br.close();

	    // 検索質問のベクトルを求める
	    // 質問が索引語と一致したらリストに1をいれる
	    ArrayList<String> searchQuestions = new ArrayList<>();
	    for (int i = 0; i < term; i++){
		String str = termList[i];
		if (str.equals(query)) {
		    String s1 = String.valueOf(i);
		    String s2 = String.valueOf(1);
		    searchQuestions.add(i+" "+1);
		}
	    }
	    
	    // 記事と検索質問の類似度の計算
	    double document_w = 0;
	    double question_w = 0;
	    double question_document = 0;
	    for (int i = 0; i < N; i++) {
		document_w = 0;
		question_w = 0;
		question_document = 0;

		for (int j = 0; j < searchQuestions.size(); j++) {
		    String question_v1 = searchQuestions.get(j);
		    String[] qn1 = question_v1.split(" ",0);
		    question_w += Double.parseDouble(qn1[1]);
		}

		TreeMap<Integer,Double> tmap = link.get(i+1);
		for (Map.Entry<Integer,Double> entry : tmap.entrySet()){
		    document_w += entry.getValue();
		    for (int k = 0; k < searchQuestions.size(); k++) {
			String question_v2 = searchQuestions.get(k);
			String[] qn2 = question_v2.split(" ",0);
			if (Integer.parseInt(qn2[0]) == entry.getKey()) {
			    question_document += Double.parseDouble(qn2[1]) * entry.getValue();
			}
		    }		    
		}	
		// cosが0以外をマップに格納
		if (question_document != 0) {
		    sim.put(i+1,question_document / (question_w * document_w));
		}
	    }
	   	    
	} catch (FileNotFoundException e) {
	    System.out.println(e);
	} catch (IOException e) {
	    System.out.println(e);
	}
	return sim;
    }

    // トップn記事のタイトルを探す
    public static void searchTitle(int[] num) {
	try {
	    File file = new File("title100.txt");
	    BufferedReader br = new BufferedReader(new FileReader(file));
       	    
	    // タイトル検索
	    int count = 1;  // 文書番号
	    String[] title = new String[num.length];
	    String line = "";
	    while ((line = br.readLine()) != null) {
		for (int i = 0; i < num.length; i++) {
		    if (count == num[i]){
			title[i] = line;
		    }
		}
		count++;
	    }

	    // トップn記事の出力
	    System.out.println("検索結果TOP" + num.length);
	    for (int i = 0; i < title.length; i++) {
		if (title[i] == null) {
		    System.out.println("これ以上見つかりませんでした。");
		    break;
		}
		System.out.println((i+1)+ ":" + title[i]);
	    }
	    br.close();
	} catch (FileNotFoundException e) {
	    System.out.println(e);
	} catch (IOException e) {
	    System.out.println(e);
	}
    }
    
    public static void main(String[] args) {
	Map<Integer,Double> sim = new TreeMap<Integer,Double>();
	
	System.gc();
	long start = System.currentTimeMillis();

	sim = similarity("言語");
	
	// 類似度の高い順にソート
	List<Map.Entry<Integer,Double>> entries = new ArrayList<Map.Entry<Integer,Double>>(sim.entrySet());
	
	Collections.sort(entries, new Comparator<Map.Entry<Integer,Double>>() {
	        
		public int compare(Map.Entry<Integer,Double> entry1 , Map.Entry<Integer,Double> entry2) {
		    return ((Double)entry2.getValue()).compareTo((Double)entry1.getValue());
		}
	    });
	
	
	int n = 30;   // n記事を返す
	int x = 0;
	int[] y = new int[n];
	for(Map.Entry<Integer,Double> entry : entries){
	    if (x < n){
		y[x] = entry.getKey();
	    }
	    x++;
	}
	
	// 記事のタイトル検索
	searchTitle(y);

	long end = System.currentTimeMillis();
	System.out.println((end-start) + "ms");
    }
}
