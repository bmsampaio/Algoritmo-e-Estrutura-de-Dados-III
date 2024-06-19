import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class LoadBase{
    public static SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
    public static Dado[] dado = new Dado[10000];
    public static File file = new File();

    static String[] genres;

    // function treat the data and call the function to load the data in the database
    public static void tratarBase(String str, int i) throws Exception{
        Date data = null;
        String title = str.split(";")[1];
        data = form.parse(str.split(";")[2]);
        LocalDate localDate = data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String overview = str.split(";")[3];
        int popularity = Integer.parseInt(str.split(";")[4]);
        genres = str.split(";")[5].split(",");
        int quantityGenre = genres.length;
        loadDatabase(i,title, localDate, overview, popularity,quantityGenre, genres);
    }

    // funtion to load the date in the database
    private static void loadDatabase(int i,String title, LocalDate localDate, String overview, int popularity,int quantityGenre, String[] genre) throws Exception {
        Dado movie = new Dado();
        // pass the information for the RandomAccessFile
        File path = new File("banco.db", "rw");
        // create the header and set as 0 because there's no udes ID yet
        Header h = new Header(i);
        // create a way to the class File
        path.file();
        byte[] b;

        try {
            // HEADER
            b = h.toByteArray();
            path.createHeader(b);
        } catch (Exception e) {
            System.out.println("O erro aqui");
            e.printStackTrace();
        }

        movie = new Dado(h.lastID, title, localDate, overview, popularity,quantityGenre, genre);
        long end = path.create(movie);
        h.updateID();
        b = h.toByteArray();
        path.updateHeader(b);

        
        MlYear mlY;
        MlFile mlFileYear = new MlFile("MLYear.db", "rw");
        mlFileYear.file();
        mlY = new MlYear(movie.release.getYear(), end);
        String[] resultY = mlFileYear.setData(mlY);
        if (resultY != null) {
            long existingAdress = Long.parseLong(resultY[0]);
            long newAdress = Long.parseLong(resultY[1]);
            path.updateLinkY(existingAdress, newAdress);
        }

        // Genre multilist
        MlGenre mlG;
        MlFileGenre mlFileGenre = new MlFileGenre("MLGenre.db", "rw");
        mlFileGenre.file();
        for (int j = 0; j < genres.length; j++) {
            mlG = new MlGenre(movie.genres[j], end);
            String[] resultG = mlFileGenre.setData(mlG);

            if (resultG != null) {
                System.out.println();
                long existingAdress = Long.parseLong(resultG[0]);
                long newAdress = Long.parseLong(resultG[1]);
                path.updateLinkG(movie.genres[j], existingAdress, newAdress);
            }
        }
    }
}