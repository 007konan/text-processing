import java.io.*;
import java.util.*;
import java.math.*;

public class Morpheme {
    String surface;
    String posStr;
    String posStr2;

    String mecabLine;
    static Process mecabPrc;
    static PrintWriter mecabOut;
    static BufferedReader mecabIn;
    static String mecabCmd = "mecab";
    static String encoding = "EUC-JP";

    public Morpheme(String line) {
	mecabLine = line;
	String[] a = line.split("\t");
	surface = a[0];
	String[] b = a[1].split(",");
	posStr = b[0];
	String[] c = b[1].split(",");
	posStr2 = c[0];

    }

    public boolean isNoun() {
	return posStr.equals("名詞");
    }

    public boolean isVerb() {
	return posStr.equals("動詞");
    }

    public boolean isSymbol() {
	return posStr2.equals("サ変接続");
    }

    public String toString() {
	return "語:" + surface + "品詞:" + posStr;
    }

    static void startMeCab() {
	try {
	    mecabPrc = Runtime.getRuntime().exec(mecabCmd);
	    mecabOut = new PrintWriter(new OutputStreamWriter(mecabPrc.getOutputStream(),encoding));
	    mecabIn = new BufferedReader(new InputStreamReader(mecabPrc.getInputStream(),encoding));
	} catch (IOException e) {
	    System.out.println("mecabを起動できませんでした。");
	    System.exit(-1);
	}
    }

    static ArrayList<Morpheme> analyzeMorpheme(String str) {
	if (mecabPrc == null) {
	    startMeCab();
	}
	
	mecabOut.println(str);
	mecabOut.flush();
	ArrayList<Morpheme> morphs = new ArrayList<Morpheme>();
	try {
	    for (String line = mecabIn.readLine(); line != null; line = mecabIn.readLine()) {
		if (line.equals("EOS")) {
		    break;
		} else {
		    morphs.add(new Morpheme(line));
		}
	    }
	} catch (IOException e) {
	    System.out.println("error");
	    e.printStackTrace();
	}
	return morphs;
    }
    
    public String getSurface() {
	return surface;
    }

    public String getPosStr() {
	return posStr;
    }

    public String getPosStr2() {
	return posStr;
    }

    // タイトルリストの作成
    static void createTitle() {
	try {
	    String fileName = "wiki100.txt";
	    FileReader fileReader = new FileReader(fileName);
	    BufferedReader r = new BufferedReader(fileReader,1024);
	    
	    // タイトルリスト
	    ArrayList<String> titleList = new ArrayList<>();
	    int dn = 0; //文書数
	    int num = 0;
	    String line = null;
	    while ((line = r.readLine()) != null) {
		if (line.contains("title=")) {
		    String title = r.readLine();
		    titleList.add(title);
		    dn++;
		}
	    }
	    r.close();
	    //ファイル出力
	    try {
		File file = new File("title100.txt");
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        
		for (int i = 0; i < titleList.size(); i++){
		    pw.println(titleList.get(i));
		}
		pw.close();
	    } catch(IOException e) {
		System.out.println(e);
	    }
	} catch(IOException e) {
	    System.out.println(e);
	}
    }

    // 0を抜いた索引語-文書行列の作成
   static void createIndex() {
	try {
	    String fileName = "wiki100.txt";
	    FileReader fileReader = new FileReader(fileName);
	    BufferedReader r = new BufferedReader(fileReader,1024);
	    
	    //索引語ｰ文書行列
	    HashMap<String,ArrayList<String>> term_document = new HashMap<>();
	    //文書ごとの索引語の数
	    HashMap<String,Integer> term = new HashMap<>();
	    
	    int dn = 0;  //文書数
	    int num = 0; //索引語の出現頻度
	    String line = null;
	    while ((line = r.readLine()) != null) {

		//文書の終わりで文書中にあった索引語をリストとしてMapに格納
		//文書番号 索引語数
		if (line.contains("</doc>")) {
		    dn++;
		    
		    for (String key : term.keySet()) {
			ArrayList<String> list = term_document.get(key);
			if (list == null){
			    list = new ArrayList<String>();
			}
			// 0以外の行列
			if (term.get(key) != 0){
			    String s1 = String.valueOf(dn);
			    String s2 = String.valueOf(term.get(key));
			    list.add(s1+" "+s2 );
			    term_document.put(key,list);
			}
		    }
		    
		    //索引語の出現頻度をリセット
		    num = 0;
		    for (Map.Entry<String,Integer> entry : term.entrySet()) {
			term.put(entry.getKey(),num);
		    }
		    line = r.readLine();
		}
		else {
		    // 形態素解析
		    ArrayList<Morpheme> morphs = analyzeMorpheme(line);
		
		    for (int i = 0; i < morphs.size(); i++) {
			Morpheme morph = morphs.get(i);
			
			if(!morph.isSymbol()) {
			    if(morph.isNoun()) {
				String surface = morph.getSurface();
			        
				if (!term.containsKey(surface)) {
				    num = 1;
				    term.put(surface,num);
				}
				else {
				    term.put(surface,term.get(surface)+1);
				}
			    }
			}
		    }
		}
	    }
	    r.close();
	    //csvファイル出力
	    try {
		File file = new File("index100.csv");
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		
		// ファイルの１行目に文書数と索引語数を書き込む
		pw.println(dn + "," + term_document.size());

		for (String key : term_document.keySet()) {
		    pw.print(key);
		    ArrayList<String> list = term_document.get(key);
		    for (int j = 0;j < list.size();j++) {
			pw.print("," + list.get(j));
			
		    }
		    pw.println();
		}
		pw.close();
	    } catch(IOException e) {
		System.out.println(e);
	    }
	} catch (IOException e) {
	    System.out.println(e);
	}
    }
    
    // tf*idfを計算しcsvファイル出力
    static void createTfidf() {
	try {
	    File file = new File("index100.csv");
	    BufferedReader br = new BufferedReader(new FileReader(file));
	    
	    File wfile = new File("tfidf100.csv");
	    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(wfile)));
	    
	    String line = "";
	    line = br.readLine();
	    String[] num = line.split(",",0);
	    int N = Integer.parseInt(num[0]);    //文書数
	    int term = Integer.parseInt(num[1]); //索引語数
	    pw.println(num[0] + "," + num[1]);
	
	    int df = 0;
	    while ((line = br.readLine()) != null) {
		df = 0;
		
		String[] data = line.split(",",0);		
		pw.print(data[0]);
		
		// dfの数え上げ
		for (int i = 1; i < data.length;i++){
		    df++;
		}

		// idfの計算
		double idf = Math.log(N / df) + 1;
		
		// tf * idfの計算
		for (int i = 1; i < data.length; i++) { 	
		    String[] data2 = data[i].split(" ",0);			    
		    double tf = Integer.parseInt(data2[1]);
		    double tfidf = tf * idf;
		    pw.print("," + data2[0] + " " + String.format("%.2f",tfidf));
		}
		pw.println();
	    }
	    br.close();
	    pw.close();
	} catch (FileNotFoundException e) {
	    System.out.println(e);
	} catch (IOException e) {
	    System.out.println(e);
	}
    }

    public static void main(String[] args) throws IOException {
	startMeCab();
	createIndex();
	createTfidf();
	createTitle(); 
    }
}

	
