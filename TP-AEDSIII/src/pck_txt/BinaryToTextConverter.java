package pck_txt;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class BinaryToTextConverter {
    public static void makeTxt() {
        String inputFilePath = "banco.db";
        String outputFilePath = "file.txt";

        try (FileInputStream fis = new FileInputStream(inputFilePath);
             FileWriter writer = new FileWriter(outputFilePath)) {

            int byteRead;
            while ((byteRead = fis.read()) != -1) {
                // Converte o byte em uma representação hexadecimal e escreve no arquivo de texto
                String hexString = String.format("%02X", byteRead);
                writer.write(hexString + " ");
            }

            System.out.println("Conversão concluída com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
