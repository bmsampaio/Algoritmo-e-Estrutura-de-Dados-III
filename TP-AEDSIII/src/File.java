import java.io.IOException;
import java.io.RandomAccessFile;
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
    public Dado update(Dado newMovie) throws IOException {
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

                    if (oldMovie.id == newMovie.id) {
                        byte[] register = newMovie.toByteArray();
                        int tamanho = register.length;

                        if ((oldlen + 3) >= tamanho) {
                            pos = pm - 2;
                            arq.seek(pos);
                            fileUpdate(register);
                            break;
                        } else {
                            pos = pm - 1;
                            arq.seek(pos);
                            arq.writeChar('*');

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
        return null;
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

            if (movie.linkYear == -1) {
                movie.linkYear = newAdress;
                b = false;
            } else {
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

        boolean b = true;
        for (int i = 0; i < movie.quantityGenre; i++) {
            b = true;
            while (b) {
                
                len = (arq.readInt() - 3);
                arq.seek(pos + 7);
                readed = new byte[len];
                arq.read(readed);
                movie.fromByteArray(readed);
                
                if (movie.genres[i].equalsIgnoreCase(genre)) {
                    if (movie.linkGenre[i] == -1) {
                        movie.linkGenre[i] = newAdress;
                        b = false;
                    } else {
                        pos = movie.linkGenre[i];
                        arq.seek(pos);
                    }
                } else {
                    b = false;
                }
            }
        }

        // movie.lapide = "-";
        update(movie);
    }

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
            if (movie.linkYear != -1)
                showYears(movie.linkYear);
        }
    }

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

            if (movie.linkGenre[i] != -1)
                showYears(movie.linkGenre[i]);
        }
    }

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

    
}
