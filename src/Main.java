import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static String command = "enc";
    private static String messageInput = "";
    private static boolean isInputString;
    private static String messageOutput;
    private static String outputFile;
    private static String algorithmType = "shift";
    private static int encryptKey;
    public static MessageReader messageReader = new MessageReader();

    public static void main(String[] args) throws IOException {

        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-mode":
                    command = args[i + 1];
                    break;
                case "-key":
                    encryptKey = Integer.parseInt(args[i + 1]);
                    break;
                case "-data":
                    isInputString = true;
                    messageReader.setMessageFromString(args[i + 1]);
                    messageInput = messageReader.getMessage();
                    break;
                case "-in":
                    if (!isInputString){
                        messageReader.setMessageFromFile(args[i + 1]);
                        messageInput = messageReader.getMessage();
                    }
                    break;
                case "-out":
                    outputFile = args[i + 1];
                    break;
                case "-alg":
                    algorithmType = args[i + 1];
                    break;
            }
        }

        if (command.equals("dec")) {
            encryptKey = (-1) * (encryptKey);
        }

        if (algorithmType.equals("unicode")){
            UnicodeTransform unicodeTransformMessage = new UnicodeTransform(messageInput, encryptKey);
            messageOutput = unicodeTransformMessage.getEncryptMessage();
        } else if (algorithmType.equals("shift")) {
            ShiftTransform shiftTransformMessage = new ShiftTransform(messageInput, encryptKey);
            messageOutput = shiftTransformMessage.getEncryptMessage();
        }

        Print printMessage = new Print(messageOutput);
        if (outputFile == null){
            printMessage.printInline();
        } else {
            printMessage.printToFile(outputFile);
        }

    }
}

class MessageReader {
    private static String message = "";

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public void setMessageFromString(String message) {
        this.message = message;
    }

    public void setMessageFromFile(String inputFile) throws IOException {
        this.message = readFileAsString(inputFile);
    }

    public String getMessage() {
        return message;
    }

}

class Print{
    String message;
    public Print(String message){
        this.message = message;
    }
    public void printInline(){
        System.out.println(message);
    }

    public void printToFile(String filename){
        File file = new File(filename);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            printWriter.print(message);
        } catch (IOException e) {
            System.out.printf("An exception occurs %s", e.getMessage());
        }
    }
}

interface TransformInterface {
    void setEncryptMessageArray();
}

abstract class Transform implements TransformInterface {

    protected static char[] messageArray = new char[0];
    protected static List<String> encryptMessageArray = new ArrayList<String>();
    protected static String encryptedMessage;
    int encryptKey;

    public Transform(String message, int encryptKey) {
        this.messageArray = message.toCharArray();
        this.encryptKey = encryptKey;
        setEncryptMessageArray();
    }

    public static String getEncryptMessage() {
        encryptedMessage = String.join("", encryptMessageArray);
        return encryptedMessage;
    }
}

class ShiftTransform extends Transform {

    public ShiftTransform(String message, int encryptKey) {
        super(message, encryptKey);
    }

    public static int findIndexOfArray(char[] inputArray, char letter) {
        for (int i = 0; i < inputArray.length; i++)
            if (inputArray[i] == Character.toLowerCase(letter))
                return i;
        return -100;
    }

    @Override
    public void setEncryptMessageArray() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        char[] alphabetArray = alphabet.toCharArray();

        for (char x : messageArray) {
            boolean isSpecialChar = false;
            int charIndex = 0;

            boolean isUpper = Character.isUpperCase(x);
            charIndex = findIndexOfArray(alphabetArray, x);
            if (charIndex == -100) {
                isSpecialChar = true;
            }

            int finalIndex = charIndex + encryptKey;

            if (finalIndex > alphabetArray.length) {
                finalIndex -= alphabetArray.length;
            } else if (finalIndex < 0) {
                finalIndex += alphabetArray.length;
            }

            if (!isSpecialChar) {
                if (isUpper) {
                    encryptMessageArray.add(String.valueOf(Character.toUpperCase(alphabetArray[finalIndex])));
                } else {
                    encryptMessageArray.add(String.valueOf(alphabetArray[finalIndex]));
                }
            } else {
                encryptMessageArray.add(String.valueOf(x));
            }
        }
    }
}

class UnicodeTransform extends Transform {

    public UnicodeTransform(String message, int encryptKey) {
        super(message, encryptKey);
    }

    @Override
    public void setEncryptMessageArray() {
        for (char x : messageArray) {
            int numericChar = (int) x + encryptKey;
            char encryptedChar = (char) numericChar;
            encryptMessageArray.add(String.valueOf(encryptedChar));
        }
    }
}

