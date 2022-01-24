package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
    private HashSet<String> dictionarySet;
    private ArrayList<Character> guessedLetters;
    private String wordSoFar;


    public EvilHangmanGame() {
        dictionarySet = new HashSet<String>();
        guessedLetters = new ArrayList<>();
        wordSoFar = "";

    }


    @Override
    public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        dictionarySet.clear();
        guessedLetters.clear();
        try {
            CreateDictionary(dictionary, wordLength);
        } catch (IOException exception) {

        }
        ArrayList<Character> guessedLetters = new ArrayList<Character>();
    }

    @Override
    public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
        guess = Character.toLowerCase(guess);
        for (int i = 0; i < guessedLetters.size(); i++) {
            if (guessedLetters.get(i).compareTo(guess) == 0) {
                throw new GuessAlreadyMadeException();
            }
        }
        HashMap<String, HashSet<String>> dictionarySubsets = new HashMap<>();
        guessedLetters.add(guess);

        Iterator<String> dictIterator = dictionarySet.iterator();
        while (dictIterator.hasNext()) {
            String word = dictIterator.next();
            StringBuilder wordKey = new StringBuilder();

            for (int i = 0; i < word.length(); ++i) {
                Character letterlocation = word.charAt(i);
                if (letterlocation.compareTo(guess) == 0) {
                    wordKey.append(guess);
                } else {
                    wordKey.append('-');
                }
            }

            if (dictionarySubsets.containsKey(wordKey.toString())) {
                dictionarySubsets.get(wordKey.toString()).add(word);
            } else {
                dictionarySubsets.put(wordKey.toString(), new HashSet<String>());
                dictionarySubsets.get(wordKey.toString()).add(word);
            }
        }
        HashSet<String> largestSet = new HashSet<>();
        String largestSetKey = "";
        int largestSetValue = 0;
        for (Map.Entry<String, HashSet<String>> entry : dictionarySubsets.entrySet()) {
            if (entry.getValue().size() == largestSetValue) {
                //check to see if one key is all blanks, if so update accordingly
                if (!isAllBlanks(largestSetKey)) {
                    if (isAllBlanks(entry.getKey())) {
                        largestSet = entry.getValue();
                        largestSetValue = entry.getValue().size();
                        largestSetKey = entry.getKey();
                        wordSoFar = entry.getKey();
                        dictionarySet = entry.getValue();
                        continue;
                    }

                    if(HaveSameAmtLetters(entry.getKey(), largestSetKey, guess)){
                        if (!KeyHasRightMost(entry.getKey(), largestSetKey, guess)){
                            largestSet = entry.getValue();
                            largestSetValue = entry.getValue().size();
                            largestSetKey = entry.getKey();
                            wordSoFar = entry.getKey();
                            dictionarySet = entry.getValue();
                            continue;
                        }
                    }
                    else if (KeyHasMoreLetters(entry.getKey(), largestSetKey, guess)){
                        largestSet = entry.getValue();
                        largestSetValue = entry.getValue().size();
                        largestSetKey = entry.getKey();
                        wordSoFar = entry.getKey();
                        dictionarySet = entry.getValue();
                        continue;
                    }
                    else continue;
                }
                else continue;
            }

            if (entry.getValue().size() > largestSetValue) {
                largestSet = entry.getValue();
                largestSetValue = entry.getValue().size();
                largestSetKey = entry.getKey();
                wordSoFar = entry.getKey();
                dictionarySet = entry.getValue();
            }
        }
        //dictionarySubsets.clear();
        return largestSet;
    }

    @Override
    public SortedSet<Character> getGuessedLetters() {
        return null;
    }

    public void CreateDictionary(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
        Scanner scanner = new Scanner(dictionary);
        // checking if the dictionary is empty
        if (!scanner.hasNext()) {
            throw new EmptyDictionaryException();
        }
        // creating our dictionary
        do {
            String word = scanner.next().toLowerCase(Locale.ROOT);
            if (word.length() == wordLength) {
                dictionarySet.add(word);
            }
        } while (scanner.hasNext());
        // testing edge cases of word lengths
        if (dictionarySet.isEmpty()) {
            throw new EmptyDictionaryException();
        }
    }

    public void Interface(int numGuesses, int wordLength, ArrayList<Character> guessedLetters) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You have " + numGuesses + " guesses left \n");
        stringBuilder.append("Used letters: ");
        for (int i = 0; i < guessedLetters.size(); ++i) {
            stringBuilder.append(guessedLetters.get(i) + ", ");
        }
        if (guessedLetters.size() > 0) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }
        stringBuilder.append("\nWord: ");
        for (int i = 0; i < wordLength; i++) {
            stringBuilder.append("-");
        }
        stringBuilder.append("\nEnter Guess: ");
        System.out.println(stringBuilder.toString());
    }

    public boolean isAllBlanks(String word) {
        boolean isAllBlank = false;
        for (int i = 0; i < word.length(); i++) {
            if (!Character.isAlphabetic(word.charAt(i))) {
                isAllBlank = true;
            } else if (Character.isAlphabetic(word.charAt(i))) {
                isAllBlank = false;
                break;
            }
        }
    return isAllBlank;
    }

    public boolean HaveSameAmtLetters(String word, String largestSetKey, char guess){
        int wordcounter = 0;
        int largestSetKeycounter = 0;
        for (int i = 0; i < word.length(); i++){
            if (word.charAt(i) == guess){
                wordcounter++;
            }
            if (largestSetKey.charAt(i) == guess){
                largestSetKeycounter++;
            }
        }
        if(largestSetKeycounter == wordcounter){
            return true;
        }
        else return false;
    }


    public boolean KeyHasMoreLetters(String word, String largestSetKey, char guess){
        int wordcounter = 0;
        int largestSetKeycounter = 0;
        for (int i = 0; i < word.length(); i++){
            if (word.charAt(i) == guess){
                wordcounter++;
            }
            if (largestSetKey.charAt(i) == guess){
                largestSetKeycounter++;
            }
        }
        if(largestSetKeycounter > wordcounter){
            return true;
        }
        else return false;
    }

    public boolean KeyHasRightMost(String word, String largestSetKey, char guess){
        int wordIndexCount = 0;
        int keyIndexCount = 0;
        for (int i = 0; i < word.length(); ++i){
            if(word.charAt(i) == guess){
                wordIndexCount += i;
            }
            if(largestSetKey.charAt(i) == guess){
                keyIndexCount += i;
            }
        }
        if(wordIndexCount > keyIndexCount){
            return false;
        }
        else return true;
    }
}


