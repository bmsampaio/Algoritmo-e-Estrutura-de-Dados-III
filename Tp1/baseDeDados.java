import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

class Movies{

    //atributos
    private int id;
    private String name;
    private Date releasedDate;
    private String description;
    private int popularity;
    private String[] genres;

    public Movies(){
        id = 0;
        name = "";
        releasedDate = new Date(0);
        description = "";
        popularity = 0;
        genres = null;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setReleasedDate(Date releasedDate) {
        this.releasedDate = releasedDate;
    }

    public Date getReleasedDate() {
        return releasedDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String getGenres() {
        String aux = "";
        if (genres.length > 1) {
            for (int i = 0; i < genres.length; i++) {
                aux = aux + genres[i];
                if (i != genres.length - 1) {
                    aux = aux + ',' + ' ';
                }
            }
        } else {
            aux = genres[0];
        }
        return aux;
    }
}
public class baseDeDados{

    public static SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
    public static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
    public static Movies movies = new Movies();

    public static String tratarBase(String str) throws Exception{
        
        movies.setId(Integer.parseInt(str.split(";")[0]));
        movies.setName(str.split(";")[1]);
        movies.setReleasedDate(form.parse(str.split(";")[2]));
        movies.setDescription(str.split(";")[3]);
        movies.setPopularity(Integer.parseInt(str.split(";")[4]));
        movies.setGenres(str.split(";")[5].split(","));
        
        return movies.getId() + " " + movies.getName() + " " + dateFormat.format(movies.getReleasedDate()) + " " + movies.getDescription() + " " + movies.getPopularity() + " " + movies.getGenres();
    }

    public static void main(String[] args) throws Exception {
        String[] entrada = new String[10000];
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter("database.db"));
        String linha = "";
        int numEntrada = 0;
        linha = br.readLine();
            while (linha != null) {
                entrada[numEntrada] = linha;
                linha = br.readLine();
                numEntrada++;
            }

            for(int i = 0; i < numEntrada; i++){
                String linhas = tratarBase(entrada[i]);
                buffWrite.append(linhas + "\n");
            }
        br.close();
        buffWrite.close();
    } 
}
