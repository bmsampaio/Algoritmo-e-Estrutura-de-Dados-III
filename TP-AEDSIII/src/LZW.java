import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZW {
    public LZW() {

    }

    public static List<Integer> encode(String text) {
        int dicSize = 256;
        Map<String, Integer> dictionary = new HashMap<>();

        // Inicializa o dicionário com os caracteres ASCII
        for (int i = 0; i < dicSize; i++) {
            dictionary.put(String.valueOf((char) i), i);
        }

        String foundChars = "";
        List<Integer> result = new ArrayList<>();
        for (char character : text.toCharArray()) {
            String charsToAdd = foundChars + character;
            if (dictionary.containsKey(charsToAdd)) {
                foundChars = charsToAdd;
            } else {
                result.add(dictionary.get(foundChars));
                dictionary.put(charsToAdd, dicSize++);
                foundChars = String.valueOf(character);
            }
        }
        // Adiciona o último conjunto de caracteres encontrados
        if (!foundChars.isEmpty()) {
            result.add(dictionary.get(foundChars));
        }
        return result;
    }

    public static String decode(List<Integer> compressed) {
        int dicSize = 256;
        Map<Integer, String> dictionary = new HashMap<>();

        // Inicializa o dicionário com os caracteres ASCII
        for (int i = 0; i < dicSize; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }

        // Remove o primeiro valor da lista comprimida e o converte em string
        String characters = String.valueOf((char) compressed.remove(0).intValue());
        StringBuilder result = new StringBuilder(characters);
        for (int code : compressed) {
            String entry = dictionary.containsKey(code)
                    ? dictionary.get(code)
                    : characters + characters.charAt(0);
            result.append(entry);
            dictionary.put(dicSize++, characters + entry.charAt(0));
            characters = entry;
        }
        return result.toString();
    }

    public static void encodeFile(String inputFilePath, String outputFilePath) {
        try {
            // Lê o conteúdo do arquivo de entrada
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            StringBuilder textBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line).append("\n");
            }
            reader.close();
            
            // Codifica o conteúdo do arquivo
            String text = textBuilder.toString();
            List<Integer> encoded = encode(text);

            // Escreve o resultado codificado no arquivo de saída
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            for (int code : encoded) {
                writer.write(code + " ");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void decodeFile(String inputFilePath, String outputFilePath) {
        try {
            // Lê o conteúdo do arquivo codificado
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            StringBuilder textBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line).append(" ");
            }
            reader.close();

            // Converte o conteúdo lido em uma lista de inteiros
            String[] encodedStrings = textBuilder.toString().trim().split("\\s+");
            List<Integer> encoded = new ArrayList<>();
            for (String encodedString : encodedStrings) {
                encoded.add(Integer.parseInt(encodedString));
            }

            // Decodifica o conteúdo
            String decodedText = decode(encoded);

            // Escreve o resultado decodificado no arquivo de saída
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            writer.write(decodedText);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}