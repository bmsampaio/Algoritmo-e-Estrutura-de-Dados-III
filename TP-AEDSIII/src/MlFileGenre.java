import java.io.IOException;
import java.io.RandomAccessFile;

public class MlFileGenre {
    protected String database, mode;
    File mainFile;

    protected long pos = 0, fileSize = 0, point = 0;
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
        arq.seek(pos);
        point = arq.getFilePointer();

        while (point < fileSize) {
            
            len = arq.readInt();
            readed = new byte[len];
            arq.read(readed);
            comp.fromByteArray(readed);

            if(comp.genre.equalsIgnoreCase(data.genre)) {
                comp.quantity = comp.quantity + 1;
                updateMlFile(comp, pos);
                String[] result = new String[2];
                result[0] = String.valueOf(comp.adress);
                result[1] = String.valueOf(data.adress);
                return result;                
            }
            else {
                pos = pos + len+4;
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

    public void updateMlFile(MlGenre file, long pos) throws IOException {
        byte[] f = file.toByteArray();
        arq.seek(pos);
        arq.writeInt(f.length);
        arq.write(f);
    }

    public long searchGenre(String searchedGenre) throws IOException {
        pos = 0;
        MlGenre comp = new MlGenre();
        fileSize = arq.length();
        byte[] readed;
        arq.seek(pos);
        point = arq.getFilePointer();

        while (point < fileSize) {
            len = arq.readInt();
            pos = pos+4;
            arq.seek(pos);
            readed = new byte[len];
            arq.read(readed);
            comp.fromByteArray(readed);

            if(comp.genre.equalsIgnoreCase(searchedGenre))
                return comp.adress;
            
            pos = pos + len;
            arq.seek(pos);
            point = arq.getFilePointer();
        }
        
        return 0;
    }

    

    public void pre(String genre, long beginAdress) throws IOException {
        long pos = 0;
        long adress = 0;
        arq.seek(pos);

        MlGenre readedMlGenre = new MlGenre();

        while (readedMlGenre.genre == null || !readedMlGenre.genre.equalsIgnoreCase(genre)) {
            byte[] mlG;
            len = arq.readInt();
            mlG = new byte[len];
            arq.read(mlG);
            readedMlGenre.fromByteArray(mlG);
            pos = pos + len + 4;
        }

        if(beginAdress == 0) {
            readedMlGenre.quantity = readedMlGenre.quantity -1;
            adress = pos - len - 4;
        }
        else {
            readedMlGenre.quantity = readedMlGenre.quantity -1;
            readedMlGenre.adress = beginAdress;
            adress = pos - len - 4;
        }

        updateMlFile(readedMlGenre, adress);
        
    }

}
