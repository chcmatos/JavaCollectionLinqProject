package atomatus.linq;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

/**
 * Analyze a spreadsheet file, how like a data table.
 * How like, data frames, to modeling datas and generating results.
 * @author Carlos Matos
 */
public abstract class Analyzer extends IterableResultGroup<String, String> implements Closeable {

    protected static final char EMPTY_SEPARATOR_CHAR;

    private String filename;
    private char separatorChar;
    private boolean closed;
    private final boolean requestSeparatorChar;

    //region load
    private enum FileType {
        CSV;

        static FileType fromName(String name){
            for (FileType t : values()) {
                if(t.name().equalsIgnoreCase(name)){
                    return t;
                }
            }
            throw new RuntimeException(String.format("File type %1$s is not valid!", name));
        }
    }

    private static FileType getFileTypeFromFilename(String filename){
        int index = filename.lastIndexOf('.');
        if(index == -1) {
            return FileType.CSV; //default file type.
        } else {
            String ext = filename.substring(index + 1);
            return FileType.fromName(ext);
        }
    }

    /**
     * Load spreadsheet (data frame) from file.
     * @param filename data frame file name full path.
     * @return instance of Analyzer.
     */
    public static Analyzer load(String filename) {
        return load(getFileTypeFromFilename(filename), filename, EMPTY_SEPARATOR_CHAR);
    }

    /**
     * Load spreadsheet (data frame) from file.
     * @param filename data frame file name full path.
     * @param separatorChar identify separator char to split values.
     * @return instance of Analyzer.
     */
    public static Analyzer load(String filename, char separatorChar) {
        return load(getFileTypeFromFilename(filename), filename, separatorChar);
    }

    /**
     * Load spreadsheet (data frame) from file.
     * @param type data frame file type.
     * @param filename data frame file name full path.
     * @param separatorChar identify separator char to split values.
     * @return instance of Analyzer.
     */
    private static Analyzer load(FileType type, String filename, char separatorChar) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (type){
            case CSV:
                return new AnalyzerCSV(filename, separatorChar);
            default:
                throw new UnsupportedOperationException();
        }
    }
    //endregion

    static {
        EMPTY_SEPARATOR_CHAR = '\0';
    }

    protected Analyzer(String filename, char separatorChar, boolean requestSeparatorChar) {
        this.filename               = requestFileExists(filename);
        this.separatorChar          = separatorChar;
        this.requestSeparatorChar   = requestSeparatorChar;
    }

    protected Analyzer(String filename) {
        this(filename, EMPTY_SEPARATOR_CHAR, false);
    }

    //region filename and separatorChar
    protected String getFilename() {
        this.requireNonClosed();
        return filename;
    }

    protected char getSeparatorChar() {
        this.requireNonClosed();
        return separatorChar;
    }

    protected void setSeparatorChar(char separatorChar){
        this.separatorChar = separatorChar;
    }

    protected boolean hasNotSeparatorChar() {
        return separatorChar == EMPTY_SEPARATOR_CHAR;
    }

    protected boolean isRequestSeparatorChar() {
        return requestSeparatorChar;
    }

    protected String requestFileExists(String filename) {
        File file = new File(Objects.requireNonNull(filename));
        if(!file.exists()) {
            throw new RuntimeException(new FileNotFoundException("File not exists!"));
        } else if(!file.canRead()) {
            throw new RuntimeException("No file read permission!");
        }
        return filename;
    }
    //endregion

    //region Closeable
    protected final void requireNonClosed(){
        if(closed){
            throw new UnsupportedOperationException("Analyzer was closed and disposed!");
        }
    }

    protected void onClose() { }

    @Override
    public final void close() {
        if(!closed) {
            onClose();
            closed = true;
            separatorChar = EMPTY_SEPARATOR_CHAR;
            filename = null;
        }
    }
    //endregion
}
