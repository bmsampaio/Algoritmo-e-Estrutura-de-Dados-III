import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

// class used to create a data and manipulate the data
public class Dado {
    protected String lapide, title, overview;
    protected String[] genres;
    protected int id, popularity, quantityGenre;
    protected long linkYear;
    protected long[] linkGenre;
    protected LocalDate release;

    public Dado(){
    }

    // creation of a data with information given by the user and the database
    public Dado(int id, String title, LocalDate release, String overview, int popularity,
                int quantityGenre, String[] genres) throws IOException{
        lapide = "-";
        this.id = id;
        this.title = title;
        this.release = release;
        this.linkYear = -1;
        this.overview = overview;
        this.popularity = popularity;
        this.quantityGenre = quantityGenre;
        this.genres = new String[quantityGenre];
        this.linkGenre = new long[quantityGenre];
        for(int i = 0; i < quantityGenre; i++) {
            this.genres[i] = genres[i];
            this.linkGenre[i] = -1;
        }
    }

    public String toString() {
        return "id: " + this.id + " | name: " + this.title + " | release: " + release + " | overview: " + this.overview + " | popularidade: " + popularity + " | quantidade de generos: " + quantityGenre + " | generos: " + Arrays.toString(genres) ;
    }

    // convert the data to a array of bytes
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(this.lapide);
        dos.writeInt(this.id);
        dos.writeUTF(this.title);
        dos.writeLong(this.release.toEpochDay());
        dos.writeLong(linkYear);
        dos.writeUTF(this.overview);
        dos.writeInt(this.popularity);
        dos.writeInt(this.quantityGenre);
        for (int i = 0; i < this.quantityGenre; i++) {
            dos.writeUTF(this.genres[i]);
            dos.writeLong(this.linkGenre[i]);
        }
        
        dos.close();
        return baos.toByteArray();
    }

    // convert the array of bytes to data
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        // this.lapide = dis.readUTF();
        this.id = dis.readInt();
        this.title = dis.readUTF();
        long epochDay = dis.readLong();
        this.release = LocalDate.ofEpochDay(epochDay);
        this.linkYear = dis.readLong();
        this.overview = dis.readUTF();
        this.popularity = dis.readInt();
        this.quantityGenre = dis.readInt();
        this.genres = new String[this.quantityGenre];
        this.linkGenre = new long[this.quantityGenre];
        for (int i = 0; i <this.quantityGenre; i++) {
            this.genres[i] = dis.readUTF();
            this.linkGenre[i] = dis.readLong();
        }
        dis.close();
    }
}