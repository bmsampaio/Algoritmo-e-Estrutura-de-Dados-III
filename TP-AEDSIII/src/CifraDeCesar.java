public class CifraDeCesar {
    
    public static String cifrar(String texto) {
       return cifrar(texto, 0);
   }

   public static String cifrar(String texto, int i) {
       StringBuilder textoCifrado = new StringBuilder();
       //condicao de parada
       if (i < texto.length()) {
           textoCifrado.append(texto.charAt(i) + 3);
           textoCifrado.append(cifrar(textoCifrado.toString(),i+1));
       }

       return textoCifrado.toString();
   }

   public static String decifrar(String textoCifrado) {
       return decifrar(textoCifrado, 0);
   }

   public static String decifrar(String textoCifrado, int i) { 
       StringBuilder textoDecifrado = new StringBuilder();
       //condicao de parada
       if (i < textoDecifrado.length()) {
           textoDecifrado.append((textoCifrado.charAt(i) - 3));
           textoDecifrado.append(decifrar(textoCifrado, i + 1));
       }

       return textoDecifrado.toString();   
   }
}