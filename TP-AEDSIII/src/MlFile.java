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

    // set data to the Year Multilist
    // search if the year release of the movie already exists in the file
    public String[] setData(MlYear data) throws IOException {
        pos = 0;
        MlYear comp = new MlYear();
        fileSize = arq.length();
        byte[] readed;
        arq.seek(pos);
        point = arq.getFilePointer();

        // scroll through the file to the end
        while (point < fileSize) {
            readed = new byte[len];
            arq.read(readed);
            comp.fromByteArray(readed);

            // comp is the data of the ML Year, if the year is the same, means the year already is at the ML File
            if(comp.year == data.year) {
                // sum 1 to the quantity of movies release at this year
                comp.quantity = comp.quantity + 1;
                // updathe the file at the current position at the file
                updateMlFile(comp, pos);
                // get the adress where is the first movie release at the year and the adress of the new movie and return it
                String[] result = new String[2];
                result[0] = String.valueOf(comp.adress);
                result[1] = String.valueOf(data.adress);
                return result;                
            }
            // if it's not the same the same year, go to the next data of the file to trying to find
            else {
                pos = pos + 16;
                arq.seek(pos);
                point = arq.getFilePointer();
            }
        }
        
        // if the year is not found at the file, go to the end of the file and create a new year data
        pos = fileSize;
        updateMlFile(data, pos);

        // return null because it is not necessary to update an existing movie file
        String[] result = new String[1];
        result=null;
        return result;

    }

    // update the file
    public void updateMlFile(MlYear file, long pos) throws IOException {
        byte[] f = file.toByteArray();
        arq.seek(pos);
        arq.write(f);
    }

    // Search the position of "banco.db" where start the informed year
    public long searchYear(int searchedYear) throws IOException {
        pos = 0;
        MlYear comp = new MlYear();
        fileSize = arq.length();
        byte[] readed;
        arq.seek(pos);
        point = arq.getFilePointer();

        // scroll through the file to the end
        while (point < fileSize) {
            readed = new byte[16];
            arq.read(readed);
            comp.fromByteArray(readed);

            if(comp.year == searchedYear)
            {   
                // return the adress of the fisrt occurrence of this year
                return comp.adress;
            }

            // go to the next data of the file
            pos = pos + 16;
            point = arq.getFilePointer();

        }

        return 0;
    }

    // procedure that correct the .db Year File
    public void correctFileYear(int year, long beginAdress) throws IOException {
        long pos = 0;
        long adress = 0;
        arq.seek(pos);
        MlYear readedMLY = new MlYear();

        // scroll through the file to the end searching the year at the file
        while (readedMLY.year != year) {
            byte[] mlY;
            len = 16;
            mlY = new byte[len];
            arq.read(mlY);
            readedMLY.fromByteArray(mlY);
            pos = pos+16;
        }

        // if the beginAdress is 0 means the movie to be deleted is not the first, so don't need to change at the file
        if(beginAdress == 0){
            // adjust the quantity of occurrence
            readedMLY.quantity = readedMLY.quantity - 1;
            // get the adress of the data at the MLYear.db to save the new information at the same position
            adress = pos - 16;
        }
        else {
            // adjust the quantity of occurrence
            readedMLY.quantity = readedMLY.quantity - 1;
            // change where is the first occurrence of the year
            readedMLY.adress = beginAdress;
            // get the adress of the data at the MLYear.db to save the new information at the same position
            adress = pos - 16;
        }

        updateMlFile(readedMLY, adress);
    }
}
