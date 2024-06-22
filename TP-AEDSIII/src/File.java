import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.Arrays;

public class File {
    protected String database, mode;
    long pos = 0, pm = 0;
    long fileSize = 0;
    int len;
    RandomAccessFile arq;
    public static ArvoreB Arvore = new ArvoreB(10);

    public File() {
    }

    public File(String database, String mode) {
        this.database = database;
        this.mode = mode;
    }

    // create a RandonAccessFile
    public void file() throws IOException {
        arq = new RandomAccessFile(database, mode);
    }

    public void createHeader(byte[] data) throws IOException {
        arq.seek(0);
        arq.write(data);
    }

    public void updateHeader(byte[] data) throws IOException {
        arq.seek(0);
        arq.write(data);
    }

    // get the last used ID
    public Header getID(Header h) throws IOException {
        byte[] lid;
        arq.seek(0);
        lid = new byte[4];
        arq.read(lid);
        h.fromByteArray(lid);
        return h;
    }

    // create from the CRUD
    public long create(Dado movie) throws IOException {
        byte[] data = movie.toByteArray();
        pos = 0;
        fileSize = arq.length();
        pos = fileSize;
        arq.seek(pos);
        arq.writeInt(data.length);
        arq.write(data);
        Arvore.inserir(movie.id, pos);
        return pos;
    }

    // update the file
    public void fileUpdate(byte[] data) throws IOException {
        arq.write(data);
    }

    // read from the CRUD
    public Dado read(int id) throws IOException {
        Dado movie = null;
        pos = 0;
        arq.seek(pos);
        long point = arq.getFilePointer();
        int lid = arq.readInt();

        if (lid < id) {
            System.out.println("ID ainda não utilizado");
        } else {
            fileSize = arq.length();
            pos = pos + 4;
            point = arq.getFilePointer();

            while (point < fileSize) {
                len = (arq.readInt() - 3);

                pos = pos + 6;
                arq.seek(pos);
                char lapide = (char) arq.read();
                // verify if the file is valid
                if (lapide != '*') {
                    pos = pos + 1;
                    arq.seek(pos);
                    byte[] readed = new byte[len];
                    arq.read(readed);
                    movie = new Dado();
                    movie.fromByteArray(readed);

                    if (movie.id == id) {
                        return movie;
                    } else {
                        pos = pos + len;
                    }
                } else {
                    pos = pos + (len + 1);
                }
                arq.seek(pos);
                point = arq.getFilePointer();
            }
        }
        return null;
    }

    // update from the CRUD
    public void update(Dado newMovie) throws IOException {
        Dado oldMovie = new Dado();
        char lapide;
        pm = 0;
        pos = 0;
        arq.seek(pos);
        long point = arq.getFilePointer();
        int lid = arq.readInt();

        if (lid < newMovie.id) {
            System.out.println("Não existe esse ID");
        } else {
            fileSize = arq.length();
            pos = pos + 4;

            // scroll through the file until the end of the file
            while (arq.getFilePointer() < fileSize) {
                int oldlen = (arq.readInt() - 3);
                pos = pos + 6;
                arq.seek(pos);
                // pm means position memo, save the position of "lapide"
                pm = pos;
                lapide = (char) arq.read();

                // verify if the file is valid
                if (lapide != '*') {
                    // walks to the beging of the data of the movie
                    pos = pos + 1;
                    arq.seek(pos);
                    byte[] readed = new byte[oldlen];
                    arq.read(readed);
                    oldMovie.fromByteArray(readed);
                    Arvore.remover(oldMovie.id, pos);

                    // Verify if it's the same ID
                    if (oldMovie.id == newMovie.id) {
                        newMovie.lapide = "-";
                        byte[] register = newMovie.toByteArray();
                        int tamanho = register.length;

                        // verify if it's possible to overcome the current information considerin the new information size
                        if ((oldlen + 3) >= tamanho) {
                            pos = pm - 2;
                            arq.seek(pos);
                            fileUpdate(register);
                            break;
                        // if the new file doesn't fit, save by the end of the file
                        } else {
                            // sign the "lápide" as no longer valid
                            pos = pm - 1;
                            arq.seek(pos);
                            arq.writeChar('*');

                            // go to the end of the file and save the new inforation
                            pos = fileSize - 1;
                            arq.seek(pos);
                            create(newMovie);
                        }
                    } else {
                        pos = pos + oldlen;
                    }
                } else {
                    pos = pos + (oldlen + 1);
                }
                arq.seek(pos);
                point = arq.getFilePointer();
            }
        }
        Arvore.inserir(newMovie.id, pos);
    }

    // delete from the CRUD
    public void delete(int id) throws IOException {
        Dado movie = null;
        pos = 0;
        arq.seek(pos);

        int lid = arq.readInt();

        if (lid < id) {
            System.out.println("ID ainda não utilizado");
        } else {
            fileSize = arq.length();
            pos = pos + 4;

            while (arq.getFilePointer() < fileSize) {
                arq.seek(pos);
                len = (arq.readInt() - 3);

                pos = pos + 6;
                arq.seek(pos);
                long pm = pos;
                char lapide = (char) arq.read();

                if (lapide != '*') {
                    pos = pos + 1;
                    arq.seek(pos);

                    byte[] readed = new byte[len];
                    arq.read(readed);
                    movie = new Dado();
                    movie.fromByteArray(readed);
                    if (movie.id == id) {
                        movie.lapide = "*";
                        pos = pm - 2;
                        arq.seek(pos);
                        byte[] b = movie.toByteArray();
                        fileUpdate(b);
                        Arvore.remover(movie.id, pos);
                        break;
                    } else {
                        pos = pos + len;
                    }
                } else {
                    pos = pos + (len + 1);
                }
            }

        }
    }

    // update the pointet of the linked Year
    public void updateLinkY(long existingAdress, long newAdress) throws IOException {
        Dado movie = new Dado();
        pos = existingAdress;
        arq.seek(pos);

        boolean b = true;
        while (b) {
            byte[] readed;
            len = (arq.readInt() - 3);
            arq.seek(pos + 7);
            readed = new byte[len];
            arq.read(readed);
            movie.fromByteArray(readed);

            // seacrh for the last entered movie with the year release
            if (movie.linkYear == -1) {
                // set the link to the next data
                movie.linkYear = newAdress;
                b = false;
            } else {
                // if is not the last one, search for the next using the year ponter
                pos = movie.linkYear;
                arq.seek(pos);
            }
        }

        movie.lapide = "-";
        update(movie);
    }

    // update the pointet of the linked Genre
    public void updateLinkG(String genre, long existingAdress, long newAdress) throws IOException {
        Dado movie = new Dado();
        pos = existingAdress;
        arq.seek(pos);
        byte[] readed;
        len = (arq.readInt() - 3);
        arq.seek(pos + 7);
        readed = new byte[len];
        arq.read(readed);
        movie.fromByteArray(readed);

        boolean b = true;
        outerLoop:
        // seach at each link genre of the file
        for (int i = 0; i < movie.quantityGenre; i++) {
            b = true;
            while (b) {
                movie = new Dado();
                arq.seek(pos);
                len = (arq.readInt() - 3);
                arq.seek(pos + 7);
                readed = new byte[len];
                arq.read(readed);
                movie.fromByteArray(readed);
                
                // seacrh for the last entered movie with the genre
                if (movie.genres[i].equalsIgnoreCase(genre)) {
                    if (movie.linkGenre[i] == -1) {
                        // set the link to the next data
                        movie.linkGenre[i] = newAdress;
                        // breke out the loop because the link was made
                        break outerLoop;

                    } else {
                        // if is not the last one, search for the next using the year ponter
                        pos = movie.linkGenre[i];
                        i = 0;
                    }
                } else {
                    b = false;
                }
            }
        }

        movie.lapide = "-";
        update(movie);
    }

    // Show all the movies released at the informed year
    public void showYears(long position) throws IOException {
        if (position == 0)
            System.out.println("Nenhum filme lançado neste ano");
        else {
            Dado movie = new Dado();

            arq.seek(position);
            byte[] readedMovie;
            len = (arq.readInt() - 3);
            arq.seek(position + 7);
            readedMovie = new byte[len];
            arq.read(readedMovie);
            movie.fromByteArray(readedMovie);

            System.out.println(movie.toString());
            System.out.println();
            // recursive call to keep calling the procedure until it's the last linked year
            if (movie.linkYear != -1)
                showYears(movie.linkYear);
        }
    }

    // Show all the movies with the informed genre
    public void showGenres(long position, String genre) throws IOException {
        if (position == 0)
            System.out.println("Nenhum filme com esse gênero");
        else {
            Dado movie = new Dado();

            arq.seek(position);
            byte[] readedMovie;
            len = (arq.readInt() - 3);
            arq.seek(position + 7);
            readedMovie = new byte[len];
            arq.read(readedMovie);
            movie.fromByteArray(readedMovie);

            int i = getPosition(genre, movie);

            System.out.println(movie.toString());
            System.out.println();

            // recursive call to keep calling the procedure until it's the last linked year
            if (movie.linkGenre[i] != -1)
                showGenres(movie.linkGenre[i], movie.genres[i]);
        }
    }

    // get the position where occour the first apperence of the genre
    private int getPosition(String genre, Dado movie) {
        for (int i = 0; i < movie.quantityGenre; i++) {
            if (movie.genres[i].equalsIgnoreCase(genre)) {
                return i;
            }
        }
        return 0;
    }

    // function to load the ArvoreB, where the keys are the movie IDs and the
    // positions are the byte offsets of the movies in the file
    public void loadArvoreB() throws IOException {
        Arvore.Mostrar();
    }

    // procedure to close the RandomAceesFile (arq)
    public void end() throws IOException {
        arq.close();
    }

    // adjust the pointer when a Year File changes
    public long adjustYearPointer(long beginAdress, Dado deletedMovie) throws IOException {
        Dado currentMovie = new Dado();
        Dado beforeMovie = new Dado();
        
        pos = beginAdress;
        arq.seek(pos);

        byte[] readedMovie;
        len = (arq.readInt() - 3);
        arq.seek(pos + 7);
        readedMovie = new byte[len];
        arq.read(readedMovie);
        currentMovie.fromByteArray(readedMovie);

        // if true, the movie is the firt occurrence, so need to change at the ML File Year
        if(currentMovie.id == deletedMovie.id) {
            return currentMovie.linkYear;
        }
        // search the movie to be changed
        while (currentMovie.id != deletedMovie.id) {
            // the current one becomes the previous one 
            beforeMovie = new Dado(currentMovie);

            // get a new movie
            pos = beforeMovie.linkYear;
            arq.seek(pos);
            len = (arq.readInt() - 3);
            arq.seek(pos + 7);
            readedMovie = new byte[len];
            arq.read(readedMovie);
            currentMovie.fromByteArray(readedMovie);
        }

        // the previous movie receive the pointer of the current movie
        beforeMovie.linkYear = currentMovie.linkYear;
        // update the previous movie
        update(beforeMovie);

        return 0;
    }

    // adjust the pointer when a Movie File changes
    public long adjustGenrePointer(long beginGenreAdress, Dado deletedMovie, int order) throws IOException {
        Dado currentMovie = new Dado();
        Dado beforeMovie = new Dado();

        pos = beginGenreAdress;
        arq.seek(pos);
        int local = 0, local2 = 0;

        byte[] readedMovie;
        len = (arq.readInt() - 3);
        arq.seek(pos + 7);
        readedMovie = new byte[len];
        arq.read(readedMovie);
        currentMovie.fromByteArray(readedMovie);

         // if true, the movie is the firt occurrence, so need to change at the ML File Genre
         if(currentMovie.id == deletedMovie.id) {
            // search for which position of the array of genres is necessary to change
            for(int i = 0; i < currentMovie.linkGenre.length; i++) {
                // if find, return the pointer of the genre
                if(currentMovie.genres[i].equalsIgnoreCase(deletedMovie.genres[order]))
                    return currentMovie.linkGenre[i];
            }
        }

        // search the movie to be changed
        while (currentMovie.id != deletedMovie.id) {
            // the current one becomes the previous one 
            beforeMovie = new Dado(currentMovie);

            // get a new movie
            for(int i = 0; i < beforeMovie.linkGenre.length; i++) {
                if(beforeMovie.genres[i].equalsIgnoreCase(deletedMovie.genres[order])) {
                    pos = beforeMovie.linkGenre[i];
                    local = i;
                }
                     
            }
            arq.seek(pos);
            len = (arq.readInt() - 3);
            arq.seek(pos + 7);
            readedMovie = new byte[len];
            arq.read(readedMovie);
            currentMovie.fromByteArray(readedMovie);
        }

        // find the occurrence of the genre at the movie
        for(int i = 0; i < currentMovie.linkGenre.length; i++) {
            if(currentMovie.genres[i].equalsIgnoreCase(deletedMovie.genres[order])) {
                pos = beforeMovie.linkGenre[i];
                local2 = i;
            }              
        }

        // the previous movie receive the pointer of the current movie
        beforeMovie.linkGenre[local] = currentMovie.linkGenre[local2];
        update(beforeMovie);

        return 0;
    }

    
}
