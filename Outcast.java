public class Outcast {

    private WordNet wd;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new IllegalArgumentException("Null input");
        this.wd = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int max = 0;
        String maxnoun = "";

        for (int i = 0; i < nouns.length; i++) {
            String curnoun = nouns[i];
            int sum = 0;
            for (int j = 0; j < nouns.length; j++) {
                sum += wd.distance(curnoun, nouns[j]);
            }
            if (sum > max) {
                max = sum;
                maxnoun = nouns[i];
            }
        }
        return maxnoun;
    }
    
    public static void main(String[] args) {
    }
}
