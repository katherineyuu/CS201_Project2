import java.util.*;

public class HashMarkov implements MarkovInterface {
    private String[] myWords; // Training text split into array of words
    private Random myRandom; // Random number generator
    private int myOrder; // Length of WordGrams used
    protected static String END_OF_TEXT = "*** ERROR ***";
    private HashMap<WordGram, List<String>> myMap = new HashMap<>();

    public HashMarkov(int order) {
        myOrder = order;
        myRandom = new Random();
        myWords = null;
    }

    public HashMarkov () {
		this(3);
	}

    @Override
    public void setTraining(String text) {
        myWords = text.split("\\s+");
        if (myMap != null) {
            myMap.clear();
        }
        for (int i = 0; i < myWords.length - myOrder; i++) {
            WordGram currentWG = new WordGram(myWords, i, myOrder);
            if (myMap.containsKey(currentWG)) {
                myMap.get(currentWG).add(myWords[i + myOrder]);
            } else {
                myMap.put(currentWG, new ArrayList<>());
                myMap.get(currentWG).add(myWords[i + myOrder]);
            }
        }
    }

    @Override
    public List<String> getFollows(WordGram wgram) {
        List<String> arr = new ArrayList<>();
        if (!myMap.containsKey(wgram)) {
            return arr;
        }
        arr = myMap.get(wgram);
        return arr;
    }

    @Override
    public String getRandomText(int length) {
        ArrayList<String> randomWords = new ArrayList<>(length);
        int index = myRandom.nextInt(myWords.length - myOrder + 1);
        WordGram current = new WordGram(myWords, index, myOrder);
        randomWords.add(current.toString());

        for (int k = 0; k < length - myOrder; k += 1) {
            String nextWord = getNextWord(current);
            if (nextWord.equals(END_OF_TEXT)) {
                break;
            }
            randomWords.add(nextWord);
            current = current.shiftAdd(nextWord);
        }
        return String.join(" ", randomWords);
    }
    private String getNextWord(WordGram wgram) {
		List<String> follows = getFollows(wgram);
		if (follows.size() == 0) {
			return END_OF_TEXT;
		}
		else {
			int randomIndex = myRandom.nextInt(follows.size());
			return follows.get(randomIndex);
		}
	}
    @Override
    public int getOrder() {
        return myOrder;
    }

    @Override
    public void setSeed(long seed) {
        myRandom.setSeed(seed);
    }

}
