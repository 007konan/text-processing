import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// new SuffixTrie(text).searchPattern(pattern)とすれば
// text中からpatternがどこに出現するかpositionのリストを返してくれる
public class Search{
  public String text;
  public SuffixTrieNode root;

  public Search(String text) {
    // 番兵を入れておけば、葉ノードからさらに枝が生えることがなくなるので
    // 構築や探索が楽になる。
    this.text = text + "\0"; // マッチングしやすいように番兵を入れておく
    this.root = new SuffixTrieNode();

    // 全てのSuffixを一つずつTrie木に追加していく
    for (int i = 0; i < this.text.length(); i++) {
      this.insertSuffix(new Suffix(this.text, i));
    }
  }

  // Suffix一つをTrie木に追加する
  private void insertSuffix(Suffix suffix) {
    SuffixTrieNode node = this.root;

    // 1文字ずつrootから辿りながらnodeを作成していく
    for (int i = 0; i < suffix.length(); i++) {
      Character c = suffix.charAt(i);
      if (node.children.containsKey(c)) {
        // 既に辿る枝があれば辿る
        node = node.children.get(c);
      }
      else {
        // なければ次のノードを作る
        SuffixTrieNode newNode = new SuffixTrieNode();
        node.children.put(c, newNode);
        node = newNode;
      }
    }

    // 辿った最後が葉なので、そこにSuffixのpositionを入れておく
    node.position = suffix.index;
  }

  // patternから出現するpositionのリストを返す
  public List<Integer> searchPattern(String pattern) {
    // まずpatternからマッチするnodeを取得する
    SuffixTrieNode matched = this.searchNode(pattern);
    if (matched != null) {
      // 取得したnodeの全ての葉ノードを取得すれば、positionのリストが得られる
      return matched.getAllLeafNodes().stream().map(
          node -> node.position
      ).collect(Collectors.toList());
    }
    else {
      return new ArrayList<>();
    }
  }

  // patternを辿ったnodeを返す
  // nodeが見つからなければnull
  private SuffixTrieNode searchNode(String pattern) {
    SuffixTrieNode node = this.root;
    for (int i = 0; i < pattern.length(); i++) {
      Character c = pattern.charAt(i);
      if (!node.children.containsKey(c)) {
        return null;
      }
      else {
        node = node.children.get(c);
      }
    }
    return node;
  }
}

class SuffixTrieNode {
  public int position; // どこからのsuffixの葉なのか
  public Map<Character, SuffixTrieNode> children = new HashMap<>();

  public SuffixTrieNode() {}

  public boolean isLeaf() {
    return this.children.isEmpty();
  }

  // あるノードの全ての葉ノードを返す
  public List<SuffixTrieNode> getAllLeafNodes() {
    if (this.isLeaf()) {
      List<SuffixTrieNode> nodes = new ArrayList<>();
      nodes.add(this);
      return nodes;
    }
    else {
      List<SuffixTrieNode> nodes = new ArrayList<>();
      for (SuffixTrieNode child : this.children.values()) {
        nodes.addAll(child.getAllLeafNodes());
      }
      return nodes;
    }
  }
}

class Suffix implements Comparable<Suffix> {
  public String text;
  public int index;

  public Suffix(String text, int index) {
    this.text = text;
    this.index = index;
  }
  public int length() {
    return text.length() - index;
  }
  public char charAt(int i) {
    return text.charAt(index + i);
  }

  public int compareTo(Suffix that) {
    if (this == that) return 0;  // optimization
    int n = Math.min(this.length(), that.length());
    for (int i = 0; i < n; i++) {
      if (this.charAt(i) < that.charAt(i)) return -1;
      if (this.charAt(i) > that.charAt(i)) return +1;
    }
    return this.length() - that.length();
  }
}