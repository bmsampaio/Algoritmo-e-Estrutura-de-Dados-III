import java.io.IOException;
import java.io.RandomAccessFile;

public class MlFileGenre {
    protected String database, mode;
    File mainFile;

    protected long pos = 0, fileSize = 0;
    protected int len = 16;
    RandomAccessFile arq;

    public MlFileGenre() {

    }

    public MlFileGenre(String database, String mode) {
        this.database = database;
        this.mode = mode;
    }

    public void file() throws IOException{
        arq = new RandomAccessFile(database, mode);
    }

    public String[] setData(MlGenre data) throws IOException {
        pos = 0;
        MlGenre comp = new MlGenre();
        fileSize = arq.length();
        byte[] readed;

        while (arq.getFilePointer() < fileSize) {
            arq.seek(pos);
            readed = new byte[len];
            arq.read(readed);
            comp.fromByteArray(readed);

            if(comp.genre == data.genre) {
                comp.quantity = comp.quantity + 1;
                updateMlFile(comp, pos);
                String[] result = new String[2];
                result[0] = String.valueOf(comp.adress);
                result[1] = String.valueOf(data.adress);
                System.out.println(result);
                return result;                
            }
            else {
                pos = pos + 16;
                
            }
        }
        pos = fileSize;
        updateMlFile(data, pos);

        String[] result = new String[1];
        result=null;
        System.out.println(result);
        return result;
    }

    public void updateMlFile(MlGenre file, long pos) throws IOException {
        byte[] f = file.toByteArray();
        arq.seek(pos);
        arq.write(f);
    }
}
