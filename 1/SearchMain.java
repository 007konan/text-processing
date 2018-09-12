import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class SearchMain {

  public static void main(String[] args) throws Exception {
    String text = "";

    try {
      byte[] fileContentBytes = Files.readAllBytes(Paths.get("wiki100.txt"));
      text = new String(fileContentBytes, StandardCharsets.UTF_8);
    } catch (Exception e) {}

    searchAndPrint(text, "自然言語");
    
  }

  private static void searchAndPrint(String text, String pattern) {
    List<Integer> positions = new Search(text).searchPattern(pattern);
    positions.sort(Comparator.naturalOrder());
    for (int position : positions) {
      int begin = Math.max(0, position - 5);
      int end = Math.min(text.length(), position + pattern.length() + 5);
      System.out.println(text.substring(begin, end));
    }
  }
}