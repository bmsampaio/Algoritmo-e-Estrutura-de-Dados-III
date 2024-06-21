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

    // set data to the Genre Multilist
    // search if the genre of the movie already exists in the file
    public String[] setData(MlGenre data) throws IOException {
        pos = 0;
        MlGenre comp = new MlGenre();
        fileSize = arq.length();
        byte[] readed;
        arq.seek(pos);
        point = arq.getFilePointer();

        // scroll through the file to the end
        while (point < fileSize) {
            len = arq.readInt();
            readed = new byte[len];
            arq.read(readed);
            comp.fromByteArray(readed);

            // comp is the data of the ML Genre, if the gente is the same, means the genre already is at the ML File
            if(comp.genre.equalsIgnoreCase(data.genre)) {
                // sum 1 to the quantity of movies with this genre
                comp.quantity = comp.quantity + 1;
                // update the file at the current position of the file
                updateMlFile(comp, pos);
                String[] result = new String[2];
                // get the adress where is the first movie with this genre and the adress of the new movie and return it
                result[0] = String.valueOf(comp.adress);
                result[1] = String.valueOf(data.adress);
                return result;                
            }
            else {
                // if it's not the same genre, go to the next data of the file trying to find the genre
                pos = pos + len+4;
                arq.seek(pos);
                point = arq.getFilePointer();
            }
        }

        // if the gente is not found, go to the end of the file and cheate a new genre data
        pos = fileSize;
        updateMlFile(data, pos);

        // return null because it is not necessary to update an axisting genre file
        String[] result = new String[1];
        result=null;
        return result;
    }

    // update the file
    public void updateMlFile(MlGenre file, long pos) throws IOException {
        byte[] f = file.toByteArray();
        arq.seek(pos);
        arq.writeInt(f.length);
        arq.write(f);
    }

    // Search the position of "banco.db" where start the informed genre
    public long searchGenre(String searchedGenre) throws IOException {
        pos = 0;
        MlGenre comp = new MlGenre();
        fileSize = arq.length();
        byte[] readed;
        arq.seek(pos);
        point = arq.getFilePointer();

        // scroll through the file to the end
        while (point < fileSize) {
            len = arq.readInt();
            pos = pos+4;
            arq.seek(pos);
            readed = new byte[len];
            arq.read(readed);
            comp.fromByteArray(readed);

            if(comp.genre.equalsIgnoreCase(searchedGenre)) {
                // return the occurrence of this genre
                return comp.adress;
            }
                
            // go to the next data of the file
            pos = pos + len;
            arq.seek(pos);
            point = arq.getFilePointer();
        }
        
        return 0;
    }

    
    // procedure that correct the .db Genre File
    public void correctFileGenre(String genre, long beginAdress) throws IOException {
        long pos = 0;
        long adress = 0;
        arq.seek(pos);

        MlGenre readedMlGenre = new MlGenre();

        // scroll through the file to the end searching the genre at the file
        while (readedMlGenre.genre == null || !readedMlGenre.genre.equalsIgnoreCase(genre)) {
            byte[] mlG;
            len = arq.readInt();
            mlG = new byte[len];
            arq.read(mlG);
            readedMlGenre.fromByteArray(mlG);
            pos = pos + len + 4;
        }

        // if the beginAdress is 0 means the movie to be deleted is not the first, so don't need to change at the file
        if(beginAdress == 0) {
            // adjust the quantity of occurrence
            readedMlGenre.quantity = readedMlGenre.quantity -1;
            // get the adress of the data at the MLGenre.db to save the new information at the same position
            adress = pos - len - 4;
        }
        else {
            // adjust the quantity of occurrence
            readedMlGenre.quantity = readedMlGenre.quantity -1;
            // change where is the first occurrence of the year
            readedMlGenre.adress = beginAdress;
            // get the adress of the data at the MLGenre.db to save the new information at the same position
            adress = pos - len - 4;
        }

        updateMlFile(readedMlGenre, adress);
        
    }

}
