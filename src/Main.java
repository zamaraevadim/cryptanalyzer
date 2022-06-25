import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Main {
    private static final String[] alphabet = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя.,”':-! ?".split("");//

    public static void main(String[] args) throws IOException {
        System.out.println("Vadim");
        System.out.println("Выберите режим :\n Введи 1 для выбора Шифрование / расшифровка \n Введи 2 для выбора Криптоанализ методом brute force");
        Scanner s = new Scanner(System.in);
        String stringChange = s.nextLine();
        if(Integer.parseInt(stringChange) == 1){
            System.out.println("Для шифрования введи Ш, для расшифровки введи Р");
            String change = s.nextLine();
            if(change.equals("Ш")){
                encoder();
            }else if(change.equals("Р")){
                decoder();
            }
        }else if(Integer.parseInt(stringChange) == 2){
            bruteForceDecoder();
        }

    }

    private static void bruteForceDecoder() throws IOException {
        System.out.println("Введите путь к файлу");
        Scanner scanner = new Scanner(System.in);//C:\Users\Vadim\Desktop\1.txt
        String input = scanner.nextLine();

        try(FileChannel fileChannelIn = FileChannel.open(Path.of(input), StandardOpenOption.READ);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            while (fileChannelIn.read(byteBuffer) > 0){
                outputStream.write(byteBuffer.array(),0, byteBuffer.position());
                byteBuffer.clear();
            }
            String text = new String(outputStream.toByteArray(),StandardCharsets.UTF_8);
            int count = 0;
            int stringCount = 0;
            String textBruteForce = "";
            String finish = "";

            for (int x = 1; true ; x++) {
                for (int key = 1; key <= alphabet.length; key++) {
                    textBruteForce = decoderText(text,key);
                    String[] textArr = textBruteForce.split("");

                    for (int i = 0; i < textArr.length; i++) {
                        if(textArr[i].equals(",")){
                            if(i < textArr.length - 1 && textArr[i + 1].equals(" ")){
                                count++;
                            }
                        }else if(textArr[i].equals(".")){
                            if(i < textArr.length - 1 && textArr[i + 1].equals(" ")){
                                count++;
                            }
                        }else if(textArr[i].equals("!")){
                            if(i < textArr.length - 1 && textArr[i + 1].equals(" ")){
                                count++;
                            }
                        }else if(textArr[i].equals("?")){
                            if(i < textArr.length - 1 && textArr[i + 1].equals(" ")){
                                count++;
                            }
                        }else if(textArr[i].equals(" ")){
                            count++;
                        }
                    }

                    if(count >= x){
                        stringCount++;
                        finish = textBruteForce;

                    }
                    count = 0;
                }
                if(stringCount == 1){
                    System.out.println(finish);
                    break;
                }
                stringCount = 0;
            }

        }
    }

    private static void decoder() throws IOException {
        System.out.println("Введите путь к файлу и ключ");
        Scanner scanner = new Scanner(System.in);//C:\Users\Vadim\Desktop\1.txt
        String input = scanner.nextLine();
        int key = Integer.parseInt(scanner.nextLine());
        String[] pathArr = input.split("\\.");
        Path path = Path.of(pathArr[0] + "decrypted." + pathArr[1]);
        Files.createFile(path);

        try(FileChannel fileChannelIn = FileChannel.open(Path.of(input), StandardOpenOption.READ);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            FileChannel fileChannelOut = FileChannel.open(path,StandardOpenOption.WRITE)){

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            while (fileChannelIn.read(byteBuffer) > 0){
                outputStream.write(byteBuffer.array(),0, byteBuffer.position());
                byteBuffer.clear();
            }
            String text = new String(outputStream.toByteArray(),StandardCharsets.UTF_8);


            String someText = decoderText(text,key);

            ByteBuffer byteBuffer2 = ByteBuffer.allocate(someText.getBytes().length);

            byteBuffer2.put(someText.getBytes());

            byteBuffer2.flip();

            fileChannelOut.write(byteBuffer2);
        }
    }
    private static void encoder() throws IOException {
        System.out.println("Введите путь к файлу и ключ");
        Scanner scanner = new Scanner(System.in);//C:\Users\Vadim\Desktop\1.txt
        String input = scanner.nextLine();
        int key = Integer.parseInt(scanner.nextLine());
        String[] pathArr = input.split("\\.");
        Path path = Path.of(pathArr[0] + "encrypted." + pathArr[1]);
        Files.createFile(path);

        try(FileChannel fileChannelIn = FileChannel.open(Path.of(input), StandardOpenOption.READ);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            FileChannel fileChannelOut = FileChannel.open(path,StandardOpenOption.WRITE)){
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            while (fileChannelIn.read(byteBuffer) > 0){
                outputStream.write(byteBuffer.array(),0, byteBuffer.position());
                byteBuffer.clear();
            }
            String text = new String(outputStream.toByteArray(),StandardCharsets.UTF_8);

            String someText = encoderText(text,key);
            ByteBuffer byteBuffer2 = ByteBuffer.allocate(someText.getBytes().length);
            byteBuffer2.put(someText.getBytes());
            byteBuffer2.flip();
            fileChannelOut.write(byteBuffer2);

        }
    }
    private static String encoderText(String text1, int key) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(text1.split("")));
        StringBuilder stringBuilder = new StringBuilder();
        for(String str : list){
            stringBuilder.append(characterEncoder(str,key));
        }

        return stringBuilder.toString();
    }
    private static String decoderText(String text2,int key) {
        ArrayList<String> list1 = new ArrayList<>(Arrays.asList(text2.toString().split("")));
        StringBuilder stringBuilder1 = new StringBuilder();
        for(String str : list1){
            stringBuilder1.append(characterDecoder(str,key));
        }

        return stringBuilder1.toString();
    }
    private static String characterDecoder(String s, int key) {
        for (int i = 0; i < alphabet.length; i++) {
            if(alphabet[i].equals(s)){
                if(i - key >= 0){
                    return alphabet[i - key];
                }else {
                    return alphabet[i - key + alphabet.length];
                }
            }
        }
        return "";
    }
    private static String characterEncoder(String a, int key) {
        for (int i = 0; i < alphabet.length; i++) {
            if(alphabet[i].equals(a)){
                if(i + key <= alphabet.length - 1){
                    return alphabet[i + key];
                }else {
                    return alphabet[i + key - alphabet.length];
                }
            }
        }
        return "";
    }
}