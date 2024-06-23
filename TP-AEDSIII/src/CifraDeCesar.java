public class CifraDeCesar {
    
    public static String cifrar(String texto) {
       return cifrar(texto, 0);
   }

   public static String cifrar(String texto, int i) {
       String textoCifrado = "";
       //condicao de parada
       if (i < texto.length()) {
           textoCifrado += (char)(texto.charAt(i) + 3);
           textoCifrado += cifrar(texto, i + 1);
       }

       return textoCifrado;
   }

   public static String decifrar(String textoCifrado) {
       return decifrar(textoCifrado, 0);
   }

   public static String decifrar(String textoCifrado, int i) { 
       String textoDecifrado = "";
       //condicao de parada
       if (i < textoCifrado.length()) {
            textoDecifrado += (char)(textoCifrado.charAt(i) - 3);
            textoDecifrado += decifrar(textoCifrado, i + 1);
       }

       return textoDecifrado;   
   }
}
