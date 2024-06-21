import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MlYear {
    
    protected int year, quantity;
    protected long adress;

    //MlFile ml = new MlFile();

    public MlYear(){

    }

    public MlYear(int year, long adress){
        this.year = year;
        this.quantity = 1;
        this.adress = adress;
    }

    public String toString() {
        return "Year: " + this.year + " | quantity: " + this.quantity + " | First Adress: " + this.adress;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(year);
        dos.writeInt(quantity);
        dos.writeLong(adress);
        dos.close();
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.year = dis.readInt();
        this.quantity = dis.readInt();
        this.adress = dis.readLong();
        dis.close();
    }

}
