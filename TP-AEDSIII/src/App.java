import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Flow.Subscriber;

import pck_txt.BinaryToTextConverter;
import pck_search.*;
import pck_search.KMP.Result;


public class App {
    public static void main(String[] args) throws Exception {
        boolean option = true;
        int lastId = 0;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // pass the information for the RandomAccessFile
        File path = new File("banco.db", "rw");
        // create the header and set as 0 because there's no udes ID yet
        Header h = new Header();
        // create a way to the class File
        path.file();

        Scanner scan = new Scanner(System.in);
        char entry;

        
        System.out.println("É o seu primeiro acesso?");
        System.out.println("s - SIM");
        System.out.println("n - NÃO");
        System.out.print("Escolha: ");
        char firstAccess;
        firstAccess = scan.next().charAt(0);
        
        if (firstAccess == 's'){
        System.out.println("Um momento, estamos carregando a base de dados...");
        lastId = addDados();
        System.out.println("Base de dados carregada.");
        System.out.println();
        System.out.println();
        }
        else if (firstAccess == 'n'){
        h = path.getID(h);
        lastId = h.lastID;
        }
         
        h = new Header(lastId);
        
        h = path.getID(h);
        lastId = h.lastID;
        h = new Header(lastId);

        // variables to be used at CRUD
        Dado movie = new Dado();
        MlFile mlFileYear;
        mlFileYear = new MlFile("MLYear.db", "rw");
        mlFileYear.file();

        MlFileGenre mlFileGenre;
        mlFileGenre = new MlFileGenre("MLGenre.db", "rw");
        mlFileGenre.file();

        MlYear mlY;
        MlGenre mlG;
        byte[] b;
        int id = 0;
        SimpleDateFormat dateFormat = null;
        Date data = null;
        LocalDate localDate = null;

        String title = "", overview = "", input = "", genre = "";
        int popularity = 0, quantityGenre = 0;
        long linkYear = 0;
        String[] genres = null;

        try {
            // HEADER
            b = h.toByteArray();
            path.createHeader(b);
        } catch (Exception e) {
            System.out.println("O erro aqui");
            e.printStackTrace();
        }

        while (option) {
            System.out.println("____________________ MENU DE OPÇÕES ____________________");
            System.out.println("1 - crud");
            System.out.println("2 - indicações de filmes similares");
            System.out.println("3 - LZW");
            System.out.println("4 - Busca por padrão");
            System.out.println("ou qualquer outra tecla para encerrar");
            System.out.print("Escolha: ");
            int escolha = scan.nextInt();

            try {

                switch (escolha) {
                    case 1:
                        System.out.println("____________________ MENU CRUD ____________________");
                        System.out.println("c - create");
                        System.out.println("r - read");
                        System.out.println("u - update");
                        System.out.println("d - delete");
                        System.out.println("i - indicações de filmes similares");
                        System.out.println("ou qualquer outra tecla para encerrar");
                        System.out.print("Escolha: ");
                        scan = new Scanner(System.in);
                        entry = scan.next().charAt(0);
                        System.out.println();

                        switch (entry) {

                            // CREATE
                            case 'c':
                                // collecting the information about the data to be created
                                System.out.print("Título: ");
                                title = reader.readLine();
                                System.out.print("Digite a data no formato dd/MM/yyyy: ");
                                String dataString = reader.readLine();

                                // converting the data
                                dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    data = dateFormat.parse(dataString);
                                } catch (ParseException e) {
                                    System.out.println("Formato de data inválido. Use o formato dd/MM/yyyy");
                                    reader.close();
                                    return;
                                }

                                // convert the object Date to LocalDate (only date, without time)
                                localDate = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                                System.out.print("Overview: ");
                                overview = reader.readLine();
                                System.out.print("Popularidade: ");
                                input = reader.readLine();
                                popularity = Integer.parseInt(input);
                                System.out.print("Quantidade de generos: ");
                                input = reader.readLine();
                                quantityGenre = Integer.parseInt(input);
                                System.out.print("Digite os generos separados por virgula: ");
                                genre = reader.readLine();
                                genres = genre.split(",");
                                movie = new Dado(h.lastID + 1, title, localDate, overview, popularity, quantityGenre,
                                        genres);
                                //b = movie.toByteArray();
                                long end = path.create(movie);
                                h.updateID();
                                b = h.toByteArray();
                                path.updateHeader(b);

                                // Year multilist

                                mlY = new MlYear(movie.release.getYear(), end);
                                String[] resultY = mlFileYear.setData(mlY);
                                if (resultY != null) {
                                    long existingAdress = Long.parseLong(resultY[0]);
                                    long newAdress = Long.parseLong(resultY[1]);
                                    path.updateLinkY(existingAdress, newAdress);
                                }

                                // Genre multilist

                                for (int i = 0; i < genres.length; i++) {
                                    mlG = new MlGenre(movie.genres[i], end);
                                    String[] resultG = mlFileGenre.setData(mlG);

                                    if (resultG != null) {
                                        long existingAdress = Long.parseLong(resultG[0]);
                                        long newAdress = Long.parseLong(resultG[1]);
                                        path.updateLinkG(movie.genres[i], existingAdress, newAdress);
                                    }
                                }

                                System.out.println();
                                System.out.println("Filme '" + movie.title + "' criado com sucesso.");
                                System.out.println();
                                break;

                            // READ
                            case 'r':
                                movie = new Dado();
                                // get the searched ID
                                System.out.print("Informe o ID procurado: ");
                                id = scan.nextInt();

                                movie = path.read(id);
                                if (movie == null) {
                                    System.out.println();
                                    System.out.println("Filme não encontrado");
                                    System.out.println();
                                } else {
                                    System.out.println();
                                    System.out.println(movie.toString());
                                    System.out.println();
                                }
                                System.out.println();
                                break;

                            // UPDATE
                            case 'u':
                                // collecting the new information about data file to be updated
                                System.out.print("Informe o ID a ser atualizado: ");
                                String stringID = reader.readLine();
                                int updateID = Integer.parseInt(stringID);
                                System.out.print("Título: ");
                                title = reader.readLine();

                                System.out.print("Digite a data no formato dd/MM/yyyy: ");
                                dataString = reader.readLine();

                                dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                data = null;
                                try {
                                    data = dateFormat.parse(dataString);
                                } catch (ParseException e) {
                                    System.out.println("Formato de data inválido. Use o formato dd/MM/yyyy");
                                    reader.close();
                                    return;
                                }

                                // convert the object Date to LocalDate (only date, without time)
                                localDate = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                                System.out.print("Overview: ");
                                overview = reader.readLine();
                                System.out.print("Popularidade: ");
                                input = reader.readLine();
                                popularity = Integer.parseInt(input);
                                System.out.print("Quantidade de generos: ");
                                input = reader.readLine();
                                quantityGenre = Integer.parseInt(input);
                                System.out.print("Digite os generos separados por virgula: ");
                                genre = reader.readLine();
                                genres = genre.split(",");
                                movie = new Dado(updateID, title, localDate, overview, popularity, quantityGenre,
                                        genres);
                                movie = path.update(movie);
                                if (movie == null) {
                                    System.out.println();
                                    System.out.println("Filme não encontrado");
                                    System.out.println();
                                } else {
                                    System.out.println();
                                    System.out.println("Filme '" + movie.title + "' atualizado com sucesso.");
                                    System.out.println();
                                }
                                break;

                            // DELETE
                            case 'd':
                                System.out.print("Informe o ID a ser deletado: ");
                                id = scan.nextInt();

                                movie = path.read(id);
                                int year = movie.release.getYear();
                                long beginAdress = mlFileYear.searchYear(year);

                                long newBegin = path.prerequisitos(beginAdress, movie);
                                
                                mlFileYear.pre(movie.release.getYear(), newBegin);  
                                
                                path.delete(id);

                                System.out.println();
                                System.out.println("Filme deletado com sucesso.");
                                System.out.println();
                                break;

                        }

                        break;

                    case 2:
                        System.out.println("");
                        System.out.println("Deseja buscar por um ano ou um gênero?");
                        System.out.println("a - ano");
                        System.out.println("g - genero");
                        System.out.print("Escolha: ");
                        String i = reader.readLine();

                        long pos;

                        if (i.equalsIgnoreCase("a")) {
                            System.out.print("Informe o ano: ");
                            input = reader.readLine();
                            int searchYear = Integer.parseInt(input);

                            pos = mlFileYear.searchYear(searchYear);
                            path.showYears(pos);
                        } else if (i.equalsIgnoreCase("g")) {
                            System.out.print("Informe o genero: ");
                            String searchGenre = reader.readLine();
                            pos = mlFileGenre.searchGenre(searchGenre);
                            path.showGenres(pos, "acao");
                        }

                        System.out.println();
                        System.out.println();
                        break;

                    // LZW
                    case 3:
                        //  create txt file
                        BinaryToTextConverter.makeTxt();
                        LZW lzw = new LZW();

                        String inputFilePath = "file.txt";
                        String encodedFilePath = "encoded_output.txt";
                        String decodedFilePath = "decoded_output.txt";
                        
                        // Codificação
                        lzw.encodeFile(inputFilePath, encodedFilePath);

                        // Decodificação
                        lzw.decodeFile(encodedFilePath, decodedFilePath);
                        
                        
                        break;

                    case 4:
                        System.out.println("");
                        System.out.println("Qual o padrão que está buscando?");
                        System.out.print("Padrão: ");
                        String pat = reader.readLine();

                        String txt = BinaryToTextConverter.createTxtString();                    

                        // Força Bruta
                        System.out.println("---------- BRUTE FORCE ----------");
                        BruteForce bf = new BruteForce();
                        BruteForce.Result resultBF = bf.BruteForceSearch(txt, pat);
                        System.out.println("Comparisons: " + resultBF.getComparisons());
                        System.out.println("Total time (miliseconds): " + resultBF.getTotalTime());
                        System.out.println();
                        System.out.println();

                        System.out.println("---------- KMP ----------");
                        KMP kmp = new KMP();                
                        Result resultKMP = kmp.KMPSearch(txt, pat);
                        System.out.println("Comparisons: " + resultKMP.getComparisons());
                        System.out.println("Total time (miliseconds): " + resultKMP.getTotalTime());
                        System.out.println();
                        System.out.println();

                       // Rabin Karp
                        System.out.println("---------- RABIN KARP ----------");
                        RabinKarp rk = new RabinKarp();
                        long[] patternIndex = rk.searchPattern(txt, pat);
                        if (patternIndex[0] != -1) {
                            System.out.println("Found pattern at index " + patternIndex[0] + "\nComparisons: " + patternIndex[2]+ "\nTotal time (miliseconds): " + patternIndex[1]);
                        } else {
                            System.out.println("Pattern not founded.\nComparisons: " + patternIndex[2] + "\nTotal time (miliseconds): " + patternIndex[1]);
                        }
                        
                        break;

                    default:
                        option = false;
                        // close file
                        path.end();
                        System.out.println();
                        System.out.println("Aplicação encerrada com sucesso.");
                        System.out.println();
                        break;
                }

            } catch (Exception e) {
                System.out.println("ERRO:");
                e.printStackTrace();
            }

        }

    }

    // function to add the data from the database to the file and return the number
    // of entries(also the lastId)
    private static int addDados() throws Exception {
        LoadBase loadBase = new LoadBase();
        BufferedReader br = new BufferedReader(new FileReader("moviesDatabase.csv"));
        String linha = "";
        int numEntrada = 0;

        linha = br.readLine();
        while (linha != null) {
            loadBase.tratarBase(linha, ++numEntrada);
            linha = br.readLine();
        }
        br.close();
        return numEntrada;
    }
}
