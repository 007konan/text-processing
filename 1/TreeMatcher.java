import java.util.HashMap;
import java.util.Map;

/**
 * マッチ対象の文字列集合を前処理し、ツリー構造で保持する。
 * NGワードチェックなど、マッチ対象の文字列集合がほぼ静的に決定されるケースで高速に動作する。
 */
public class TreeMatcher implements Matcher{

    private CharTreeNode rootNode = null;

    public TreeMatcher(String[] words){
        this.rootNode = new CharTreeNode(null);
        this.rootNode.addWords(words);
    }

    /**
     * マッチ処理を行う。
     */
    public boolean match(String target){
        CharTreeNode currentNode = null;
        for(int i=0;i<target.length();i++){
            currentNode = this.rootNode;
            for(int k=0;i+k<target.length();k++){
                Character c = target.charAt(i+k);
                currentNode = currentNode.child.get(c);

                if(currentNode == null){
                    break;
                }

                if(currentNode.end){
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * ツリー構造を保持する内部クラス。
     *
     */
    static class CharTreeNode {
        boolean end = false;
        Character value;
        Map<Character,CharTreeNode> child = new HashMap<Character, CharTreeNode>();
        CharTreeNode(Character value){
            this.value = value;
        }

        void addWords(String[] words){
            for(String word : words){
                this.addWord(word);
            }
        }

        void addWord(String word){
            addWord(word,this);
        }

        static void addWord(String value, CharTreeNode parent){
            if(value == null || value.length() < 1){
                parent.end = true;
                return;
            }

            if(parent == null || parent.end){
                return;
            }

            Character firstLetter = value.charAt(0);
            CharTreeNode current = null;
            if(parent.child.containsKey(firstLetter)){
                current = parent.child.get(firstLetter);
            }else{
                current = new CharTreeNode(firstLetter);
                parent.child.put(firstLetter,current);
            }

            current.addWord(value.substring(1,value.length()));
        }
    }
}