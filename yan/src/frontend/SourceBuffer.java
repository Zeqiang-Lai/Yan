package frontend;

public class SourceBuffer {

    // Public Methods

    public SourceBuffer(String source) {
        this.offset = 0;
        this.source = source;
        this.length = source.length();
    }

    /**
     * Fetch next character in the source buffer.
     *
     * Notice: current pointer will be set to next location,
     * if you don't want the pointer to be set, use <code>peek</code> instead.
     *
     * @return next character.
     */
    public char next() {
        char ch = offset < length ? source.charAt(offset) : '\0';
        offset += 1;
        return ch;
    }

    /**
     * Have a peek at next character in the source buffer.
     *
     * @return next character.
     */
    public char peek() {
        return offset < length ? source.charAt(offset) : '\0';
    }


    /**
     * Compare a given character with next character in buffer.
     *
     * @param ch character you want to compare.
     * @return false if two char are not equal or buffer is empty.
     * true if two char are the same.
     */
    public boolean peek(char ch) {
        if(peek() == ch) {
            next();
            return true;
        }
        return false;
    }


    /**
     * Set current pointer to the previous location.
     */
    public void back() {
        if(offset > 0)
            offset -= 1;
    }

    /**
     * Get offset of current character.
     * @return offset.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * The same as String's substring.
     */
    public String substring(int beginIndex, int endIndex) {
        return source.substring(beginIndex, endIndex);
    }

    // Private Properties

    private String source;
    private int offset;
    private int length;

}