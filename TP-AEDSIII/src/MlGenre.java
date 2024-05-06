import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MlGenre {
    protected String genre;
    protected int quantity;
    protected long adress;

    MlFile ml = new MlFile();

    public MlGenre() {

    }

    public MlGenre(String genre, long adress) {
        this.genre = genre;
        this.adress = adress;
        this.quantity = 1;
    }

    public String toString() {
        return "Genre: " + this.genre + " | quantity: " + this.quantity + " | First Adress: " + this.adress;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(genre);
        dos.writeInt(quantity);
        dos.writeLong(adress);
        dos.close();
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.genre = dis.readUTF();
        this.quantity = dis.readInt();
        this.adress = dis.readLong();
        dis.close();
    }
}