import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// class used to create a header and manipulate the header
public class Header {
    // last used ID
    protected int lastID;

    public Header(){

    }

    public Header(int lastID){
        this.lastID = lastID;
    }
    
    public void updateID(){
        lastID = lastID+1;
    }

    //convert the header to a array of bytes
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.lastID);
        return baos.toByteArray();
    }

    //convert the array of bytes to header
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.lastID = dis.readInt();
    }
}
