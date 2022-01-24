package hangman;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class EvilHangman {
    private static StringBuilder secretWord = new StringBuilder();
    private static final int SEEN_IT_ALREADY = 1;
    private static final int TOO_LONG = 2;
    private static final int NOT_LETTER = 3;
    private static final int EMPTY_INPUT = 4;

    public static void main(String[] args) throws IOException {

        File dictionary = new File(args[0]);
        EvilHangmanGame doesThisWork = new EvilHangmanGame();
        File file = new File(args[0]);
        int numGuesses = Integer.parseInt(args[2]);
        int wordLength = Integer.parseInt(args[1]);
        for(int i = 0; i < wordLength; i++){
            secretWord.append("-");
        }
        try {
            doesThisWork.startGame(file, wordLength);
        }
        catch(EmptyDictionaryException exception){
            System.out.println("Woah, nice try but that dictionary's empty ¯\\_(ツ)_/¯");
            System.exit(1);
        }

        ArrayList<Character> guessedLetters = new ArrayList<>();
        boolean theGameGoesOn = true;
        while(theGameGoesOn) {
            Interface(numGuesses,wordLength,guessedLetters);
            Scanner scan = new Scanner(System.in);
            String input = scan.nextLine().toLowerCase(Locale.ROOT);
            if (CheckValidInput(input, guessedLetters) == EMPTY_INPUT){
                System.out.println("Can't enter an empty input, or we'll be here all day. Try again\n");
                continue;
            } else if (CheckValidInput(input, guessedLetters) == SEEN_IT_ALREADY) {
                System.out.println("That's too long, try again:\n");
                continue;
            } else if (CheckValidInput(input, guessedLetters) == TOO_LONG) {
                System.out.println("Thought you could pull a fast one eh? try again, we've seen that one before:\n");
                continue;
            } else if (CheckValidInput(input, guessedLetters) == NOT_LETTER) {
                System.out.println("That's not a letter, nice try though\n");
                continue;
            }


            char guess = input.charAt(0);
            guessedLetters.add(guess);
            Set<String> words;
            try {
                words = doesThisWork.makeGuess(guess);
            } catch (GuessAlreadyMadeException exception) {
                System.out.println("Thought you could pull a fast one eh? try again, we've seen that one before:");
                continue;
            }
            for (String entry : words) {
            int count = 0;
                for (int i = 0; i < entry.length(); i++){
                    if (entry.charAt(i) == guess){
                        count++;
                        secretWord.replace(i,i+1,input);
                    }
                }
                if (count == 0){
                    System.out.println("Nope, not found, no "+guess+"'s");
                    numGuesses--;
                }
                else {
                    System.out.println("You're in luck! You found the " + guess);
                }
                break;
            }

            if(numGuesses == 0){
                for (String entry : words){

                    System.out.println("Nice try, you lost, but it's not my fault. The word was " + entry + ", silly");
                    break;
                }
                    System.exit(0);
            }
            else if (DidWeWin()){
                System.out.println("Nice job you cheated, the word was " + secretWord.toString());
                System.exit(0);
            }
        }

    }

    public static void Interface(int numGuesses, int wordLength, ArrayList<Character> guessedLetters){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You have " + numGuesses + " guesses left \n");
        stringBuilder.append("Used letters: ");
        Collections.sort(guessedLetters);
        for (int i = 0; i < guessedLetters.size(); ++i){
            stringBuilder.append(guessedLetters.get(i) + ", ");
        }
        stringBuilder.delete(stringBuilder.length() - 2,stringBuilder.length());
        stringBuilder.append("\nWord: " + secretWord);
        stringBuilder.append("\nEnter Guess: ");
        System.out.print(stringBuilder.toString());
    }

    public static int CheckValidInput(String input, ArrayList<Character> guessedLetters){
        if(input.isBlank()){
            return 4;
        }
        if (input.length() > 1){
            return 1;
        }
        for (int i = 0; i < guessedLetters.size(); i++){
            if (input.charAt(0) == guessedLetters.get(i)){
                return 2;
            }
        }
        if(!Character.isAlphabetic(input.charAt(0))){
            return 3;
        }
        return 0;
    }

    public static boolean DidWeWin(){
        boolean status = false;
        for (int i = 0; i < secretWord.length(); ++i){
            if(Character.isAlphabetic(secretWord.charAt(i))){
                status = true;
            }
            else {
                status = false;
                break;
            }

        }
        return status;
    }
}































