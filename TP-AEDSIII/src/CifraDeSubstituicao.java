public class CifraDeSubstituicao {

    private static final String ALFABETO = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHAVE = "ZxCvBnMaSdFgHjKlQwErTyUiOpPoIuYtReWqQaZxCvBnMlKjHgFdS";

    public static String cifrar(String texto) {
        StringBuilder textoCifrado = new StringBuilder();

        for (int i = 0; i < texto.length(); i++) {
            char caractere = texto.charAt(i);
            int indice = ALFABETO.indexOf(caractere);

            if (indice != -1) {
                caractere = CHAVE.charAt(indice);
            }

            textoCifrado.append(caractere);
        }

        return textoCifrado.toString();
    }

    public static String decifrar(String textoCifrado) {
        StringBuilder textoDecifrado = new StringBuilder();

        for (int i = 0; i < textoCifrado.length(); i++) {
            char caractere = textoCifrado.charAt(i);
            int indice = CHAVE.indexOf(caractere);

            if (indice != -1) {
                caractere = ALFABETO.charAt(indice);
            }

            textoDecifrado.append(caractere);
        }

        return textoDecifrado.toString();
    }
}
