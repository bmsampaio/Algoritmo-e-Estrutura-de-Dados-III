import java.io.IOException;
import java.io.RandomAccessFile;

public class File {
    protected String database, mode;
    //long p;
    long pos = 0;
    long fileSize = 0;
    int len;
    RandomAccessFile arq;

    public File(){

    }

    public File(String database, String mode){
        this.database = database;
        this.mode = mode;
    }

    // create a RandonAccessFile
    public void file() throws IOException{
        arq = new RandomAccessFile(database, mode);
    }

    public void createHeader(byte[] data) throws IOException{
        arq.write(data);
    }  

    public void updateHeader(byte[] data) throws IOException{
        arq.seek(0);
        arq.write(data);
    }

    
    // get the last used ID
    public Header getID(Header h) throws IOException{
        byte[] lid;
        arq.seek(0);
        len = arq.readInt();
        lid = new byte[len];
        arq.read(lid);
        h.fromByteArray(lid);
        return h;
    }

    // create from the CRUD
    public void create(byte[] data) throws IOException{
        pos = 0;
        fileSize = arq.length();
        pos = fileSize;
        arq.seek(pos);
        arq.writeInt(data.length);
        arq.write(data);
    }
    
    // update the file
    public void fileUpdate(byte[] data) throws IOException {
        arq.write(data);
    }

    // read from the CRUD
    public Dado read( int id) throws IOException{
        Dado movie = null;
        pos = 0;
        arq.seek(0);
        int lid = arq.readInt();
        // verify if the ID was already used
        if(lid < id) {
            System.out.println("Não existe esse ID");
        }
        else{
            fileSize = arq.length();
            pos = pos+4;
            // scroll through the file until the end of the file
            while (arq.getFilePointer() < fileSize) {
                arq.seek(pos);
                int len = arq.readInt();

                pos = pos + 6;
                arq.seek(pos);
                char lapide = (char) arq.read();

                // verify if the file is valid
                if(lapide != '*'){
                    pos = pos + 1;
                    arq.seek(pos);
                    byte[] readed;
                    readed = new byte[len];
                    arq.read(readed);
                    movie = new Dado();
                    // transform the file data from bytes to Dado
                    movie.fromByteArray(readed);
                    
                    // verify if it's the same ID
                    if(movie.id == id){
                        // return the data
                        return movie;
                    }
                    else {
                        // go to the next file
                        pos = pos + (len-3);
                    }                
                }
            }
        }  
        return null;
    }

    // update from the CRUD
    public void update(Dado newMovie) throws IOException {
        // the movie to be updated
        Dado oldMovie = new Dado();

        pos = 0;
        arq.seek(pos);

        //lid means last ID, save the last ID used
        int lid = arq.readInt();
        // verify if the ID was already used
        if(lid < newMovie.id) {
            System.out.println("Não existe esse ID");
        }
        else {
            fileSize = arq.length();
            pos = pos+4;
            // scroll through the file until the end of the file
            while (arq.getFilePointer() < fileSize) {
                arq.seek(pos);
                // odlen means old lenght, keep the lenght of the movie to be updated
                int oldlen = arq.readInt();

                pos = pos + 6;
                arq.seek(pos);
                //pm means position memo, save the position of "lapide"
                long pm = pos;
                char lapide = (char) arq.read();

                // verify if the file is valid
                if(lapide != '*'){
                    // walks to the beging of the data of the movie
                    pos = pos + 1;
                    arq.seek(pos);
                    byte[] readed;
                    readed = new byte[oldlen];
                    arq.read(readed);
                    // transform the file data from bytes to Dado
                    oldMovie.fromByteArray(readed);
                    
                    // verify if the ID searched to be updated
                    if(oldMovie.id == newMovie.id){
                        byte[] register;
                        // transform the new movie to bytes to keep the size
                        register = newMovie.toByteArray();
                        int tamanho = register.length;
                        // verify if the size of the movie to be updated and the updtate is the same or if the new one is smaller
                        if(oldlen >= tamanho) {
                            // if its samaller, than the data is updated
                            pos = pm - 2;
                            arq.seek(pos);
                            fileUpdate(register);
                            break;
                        }
                        else {
                            // if it's not, means that the new data is bigger. So the data gonna be write at the end of the file

                            // signals that the data is no longer valid
                            pos = pm-1;
                            arq.seek(pos);
                            arq.writeChar('*');

                            // write the new information at the end of the file
                            pos = fileSize - 1;
                            arq.seek(pos);
                            byte[] b;
                            b = newMovie.toByteArray();
                            create(b);
                        }
                    
                    }
                    else {
                        // go o the next file
                        pos = pos + (len-3);
                    }

                }
                else {
                    // go to the next file
                    pos = pos + (len-3);
                }   

            }

        }

    }

    // delete from the CRUD
    public void delete(int id) throws IOException {
        pos = 4;
        arq.seek(pos);
        fileSize = arq.length();
        // verify if the ID was already used
        while (arq.getFilePointer() < fileSize) {
            len = arq.readInt();

            pos = pos + 6;
            arq.seek(pos);
            char lapide = (char) arq.read();

            // verify if the file is valid
            if(lapide != '*'){
                pos = pos + 1;
                arq.seek(pos);
                
                // rid means read ID, rid is the ID passed by the user that needs to be deleted
                int rid = arq.readInt();
                    
                // verify if it's the same ID
                if(rid == id){
                    // go to the tombstone of the file
                    pos = pos - 1;
                    arq.seek(pos);
                    // inform that the file isn't valid
                    arq.writeChar('*');
                    break;
                }
                else {
                    // go to the next file
                    pos = pos + (len-3);
                }                

            }

        }
    }

    // procedure to close the RandomAceesFile (arq)
    public void end() throws IOException{
        arq.close();
    }
}
