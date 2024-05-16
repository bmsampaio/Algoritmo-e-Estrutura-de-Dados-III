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
        arq.seek(0);
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
        lid = new byte[4];
        arq.read(lid);
        h.fromByteArray(lid);
        return h;
    }

    // create from the CRUD
    public long create(byte[] data) throws IOException{
        // pos = 0;
        fileSize = arq.length();
        pos = fileSize;
        arq.seek(pos);
        arq.writeInt(data.length);
        arq.write(data);
        return pos;
    }
    
    // update the file
    public void fileUpdate(byte[] data) throws IOException {
        arq.write(data);
    }

    // read from the CRUD
    public Dado read(int id) throws IOException{
        Dado movie = null;
        pos = 0;
        arq.seek(pos);
        long point = arq.getFilePointer();
        // lid means last ID, save the last ID used
        int lid = arq.readInt();
        // verify if the ID was already used
        if(lid < id) {
            System.out.println("ID ainda não utilizado");
        }
        else{
            fileSize = arq.length();
            pos = pos + 4;
            point = arq.getFilePointer();
            // scroll through the file until the end of the file
            while (point < fileSize) {
                len = (arq.readInt() - 3);

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
                    if(movie.id == id) {
                        // return the data
                        return movie;
                    }
                    else {
                        // go to the next file
                        pos = pos + len;
                    }                
                } else {
                    // go to the next file
                    pos = pos + (len+1);
                }  
                arq.seek(pos);
                point = arq.getFilePointer();
            }
        }  
        return null;
    }

    // update from the CRUD
    public Dado update(Dado newMovie) throws IOException {
        // the movie to be updated
        Dado oldMovie = new Dado();
        pos = 0;
        arq.seek(pos);
        long point = arq.getFilePointer();
        // lid means last ID, save the last ID used
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
                // odlen means old lenght, keep the lenght of the movie to be updated
                int oldlen = (arq.readInt() - 3);

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
                        if((oldlen+3) >= tamanho) {
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
                        pos = pos + oldlen;
                    }

                }
                else {
                    // go to the next file
                    pos = pos + (oldlen+1);
                }  
                arq.seek(pos);
                point = arq.getFilePointer();
            }

        }
        return null;

    }

    // delete from the CRUD
    public void delete(int id) throws IOException {
        Dado movie = null;
        pos = 0;
        arq.seek(pos);

        // lid means last ID, save the last ID used
        int lid = arq.readInt();
        // verify if the ID was already used
        if(lid < id) {
            System.out.println("ID ainda não utilizado");
        }
        else {
            fileSize = arq.length();
            pos = pos + 4;
            // scroll through the file until the end of the file
            while (arq.getFilePointer() < fileSize) {
                arq.seek(pos);
                len = (arq.readInt() - 3);

                pos = pos + 6;
                arq.seek(pos);
                long pm = pos;
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
                    
                    // rid means read ID, rid is the ID passed by the user that needs to be deleted
                    /*int rid = arq.readInt();
                        
                    // verify if it's the same ID
                    if(rid == id){
                        // go to the tombstone of the file
                        pos = pos - 2;
                        arq.seek(pos);
                        // inform that the file isn't valid
                        arq.writeChar('*');
                        break;
                    }*/
                    if(movie.id == id) {
                        movie.lapide = "*";
                        pos = pm - 2;
                        arq.seek(pos);
                        byte[] b;
                        b = movie.toByteArray();
                        fileUpdate(b);
                    }
                    else {
                        // go to the next file
                        pos = pos + len;;
                    }                

                } else {
                // go to the next file
                pos = pos + (len+1);
                }
            }

        }

        
    }

    public void updateLinkY(long existingAdress, long newAdress) throws IOException {
        Dado movie = new Dado();
        pos = existingAdress;
        arq.seek(pos);

        boolean b = true;
        while (b) {
            byte[] readed;
            len = (arq.readInt()-3);
            arq.seek(pos+7);
            readed = new byte[len];
            arq.read(readed);
            movie.fromByteArray(readed);

            if(movie.linkYear == -1) {
                movie.linkYear = newAdress;
                b = false;
            }
            else {
                pos = movie.linkYear;
                arq.seek(pos);
            }
            
        }

        movie.lapide = "-";
        update(movie);

    }

    public void updateLinkG(String genre, long existingAdress, long newAdress) throws IOException {
        Dado movie = new Dado();
        pos = existingAdress;
        arq.seek(pos);

        byte[] readed;
        len = (arq.readInt()-3);
        arq.seek(pos+7);
        readed = new byte[len];
        arq.read(readed);
        movie.fromByteArray(readed);
        
        boolean b = true;
        for(int i = 0; i < movie.quantityGenre; i++) {
            b = true;
            while (b) {
                if(movie.genres[i].equalsIgnoreCase(genre)){
                    if(movie.linkGenre[i] == -1) {
                        movie.linkGenre[i] = newAdress;
                        b = false;
                    }
                    else {
                        pos = movie.linkGenre[i];
                        arq.seek(pos);
                    }
                }
                else {
                    b = false;
                }
            }
        }

        movie.lapide = "-";
        update(movie);
    }

    // procedure to close the RandomAceesFile (arq)
    public void end() throws IOException{
        //arq.close();
    }

    public void showYears(long position) throws IOException {
        if(position == 0)
            System.out.println("Nenhum filme lançado neste ano");
        else{
            Dado movie = new Dado();

            arq.seek(position);
            byte[] readedMovie;
            len = (arq.readInt()-3);
            arq.seek(position+7);
            readedMovie = new byte[len];
            arq.read(readedMovie);
            movie.fromByteArray(readedMovie);

            System.out.println(movie.toString());
            if(movie.linkYear != -1)
                showYears(movie.linkYear);
        }
        
    }

    public void showGenres(long position, String genre) throws IOException {
        if(position == 0)
            System.out.println("Nenhum filme com esse gênero");
        else {

            Dado movie = new Dado();

            arq.seek(position);
            byte[] readedMovie;
            len = (arq.readInt()-3);
            arq.seek(position+7);
            readedMovie = new byte[len];
            arq.read(readedMovie);
            movie.fromByteArray(readedMovie);

            int i = getPosition(genre, movie);

            System.out.println(movie.toString());
           
            if(movie.linkGenre[i] != -1)
                showYears(movie.linkGenre[i]);
        }
    }

    private int getPosition(String genre, Dado movie) {
        for(int i = 0; i < movie.quantityGenre; i++){
            if(movie.genres[i].equalsIgnoreCase(genre)){
                return i;
            }
        }
        return 0;
    }
}
