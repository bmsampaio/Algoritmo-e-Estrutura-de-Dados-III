import java.io.IOException;
import java.io.RandomAccessFile;

public class MlFile {
    protected String database, mode;
    File mainFile;
    Dado movie;

    protected long pos = 0, fileSize = 0, point = 0;
    protected int len = 16;
    RandomAccessFile arq;

    public MlFile() {

    }

    public MlFile(String database, String mode) {
        this.database = database;
        this.mode = mode;
    }

    public void file() throws IOException{
        arq = new RandomAccessFile(database, mode);
    }


    public int getQuantity(MlYear mlYear){
        return 0;
    }

    public String[] setData(MlYear data) throws IOException {
        pos = 0;
        MlYear comp = new MlYear();
        fileSize = arq.length();
        byte[] readed;
        arq.seek(pos);
        point = arq.getFilePointer();

        while (point < fileSize) {
            readed = new byte[len];
            arq.read(readed);
            comp.fromByteArray(readed);

            if(comp.year == data.year) {
                comp.quantity = comp.quantity + 1;
                updateMlFile(comp, pos);
                String[] result = new String[2];
                result[0] = String.valueOf(comp.adress);
                result[1] = String.valueOf(data.adress);
                return result;                
            }
            else {
                pos = pos + 16;
                arq.seek(pos);
                point = arq.getFilePointer();
            }
        }
        
        pos = fileSize;
        updateMlFile(data, pos);

        String[] result = new String[1];
        result=null;
        return result;

    }

    public void updateMlFile(MlYear file, long pos) throws IOException {
        byte[] f = file.toByteArray();
        arq.seek(pos);
        arq.write(f);
    }

    public long searchYear(int searchedYear) throws IOException {
        pos = 0;
        MlYear comp = new MlYear();
        fileSize = arq.length();
        byte[] readed;
        arq.seek(pos);
        point = arq.getFilePointer();

        while (point < fileSize) {
            readed = new byte[16];
            arq.read(readed);
            comp.fromByteArray(readed);

            if(comp.year == searchedYear)
            {   
                return comp.adress;
            }

            pos = pos + 16;
            point = arq.getFilePointer();

        }

        return 0;
    }
}
