/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libliflin.games.wordrelease;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author william.deans@gmail.com
 * @author william.laffin@gmail.com
 *
 * Written to work with byte arrays requiring address space larger than 32 bits.
 *
 * Written to work with stax.
 *
 */
public class BigFileInMemory extends InputStream {

    private final long CHUNK_SIZE = 1024 * 1024 * 1024; //1GiB
    long size;
    byte[][] data;

    public BigFileInMemory(long size) {
        this.size = size;
        if (size == 0) {
            data = null;
        } else {
            int chunks = (int) (size / CHUNK_SIZE);
            int remainder = (int) (size - ((long) chunks) * CHUNK_SIZE);
            data = new byte[chunks + (remainder == 0 ? 0 : 1)][];
            for (int idx = chunks; --idx >= 0;) {
                data[idx] = new byte[(int) CHUNK_SIZE];
            }
            if (remainder != 0) {
                data[chunks] = new byte[remainder];
            }
        }
    }

    public byte get(long index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Error attempting to access data element " + index + ".  Array is " + size + " elements long.");
        }
        int chunk = (int) (index / CHUNK_SIZE);
        int offset = (int) (index - (((long) chunk) * CHUNK_SIZE));
        return data[chunk][offset];
    }

    public void set(long index, byte b) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Error attempting to access data element " + index + ".  Array is " + size + " elements long.");
        }
        int chunk = (int) (index / CHUNK_SIZE);
        int offset = (int) (index - (((long) chunk) * CHUNK_SIZE));
        data[chunk][offset] = b;
    }

    /**
     * Simulates a single read which fills the entire array via several smaller
     * reads.
     *
     * @param fileInputStream
     * @throws IOException
     */
    public void read(FileInputStream fileInputStream) throws IOException {
        if (size == 0) {
            return;
        }
        for (int idx = 0; idx < data.length; idx++) {
            if (fileInputStream.read(data[idx]) != data[idx].length) {
                throw new IOException("short read");
            }
        }
    }

    public InputStream getInputStream() {
        return this;
    }

    public long size() {
        return size;
    }
    /**
     * An array of bytes that was provided by the creator of the stream.
     * Elements
     * <code>buf[0]</code> through
     * <code>buf[count-1]</code> are the only bytes that can ever be read from
     * the stream; element
     * <code>buf[pos]</code> is the next byte to be read.
     */
    //protected byte buf[];
    // buf is data
    /**
     * The index of the next character to read from the input stream buffer.
     * This value should always be nonnegative and not larger than the value of
     * <code>count</code>. The next byte to be read from the input stream buffer
     * will be
     * <code>buf[pos]</code>.
     */
    protected long pos;
    /**
     * The currently marked position in the stream. ByteArrayInputStream objects
     * are marked at position zero by default when constructed. They may be
     * marked at another position within the buffer by the
     * <code>mark()</code> method. The current buffer position is set to this
     * point by the
     * <code>reset()</code> method. <p> If no mark has been set, then the value
     * of mark is the offset passed to the constructor (or 0 if the offset was
     * not supplied).
     *
     * @since JDK1.1
     */
    protected long mark = 0;

    /**
     * The index one greater than the last valid character in the input stream
     * buffer. This value should always be nonnegative and not larger than the
     * length of
     * <code>buf</code>. It is one greater than the position of the last byte
     * within
     * <code>buf</code> that can ever be read from the input stream buffer.
     */
    //protected int count;
    // long size.
    // buf needs to be switched to data
    // count needs to be switched to size
    // add > Integer.maxvalue checks.
    /**
     * Reads the next byte of data from this input stream. The value byte is
     * returned as an
     * <code>int</code> in the range
     * <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value
     * <code>-1</code> is returned. <p> This
     * <code>read</code> method cannot block.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     * stream has been reached.
     */
    public synchronized int read() {
        if (pos < size) {
            return this.get(pos++) & 0xff;
        } else {
            return -1;
        }
    }

    /**
     * Reads up to
     * <code>len</code> bytes of data into an array of bytes from this input
     * stream. If
     * <code>pos</code> equals
     * <code>count</code>, then
     * <code>-1</code> is returned to indicate end of file. Otherwise, the
     * number
     * <code>k</code> of bytes read is equal to the smaller of
     * <code>len</code> and
     * <code>count-pos</code>. If
     * <code>k</code> is positive, then bytes
     * <code>buf[pos]</code> through
     * <code>buf[pos+k-1]</code> are copied into
     * <code>b[off]</code> through
     * <code>b[off+k-1]</code> in the manner performed by
     * <code>System.arraycopy</code>. The value
     * <code>k</code> is added into
     * <code>pos</code> and
     * <code>k</code> is returned. <p> This
     * <code>read</code> method cannot block.
     *
     * @param b the buffer into which the data is read.
     * @param off the start offset in the destination array <code>b</code>
     * @param len the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or
     * <code>-1</code> if there is no more data because the end of the stream
     * has been reached.
     * @exception NullPointerException If <code>b</code> is <code>null</code>.
     * @exception IndexOutOfBoundsException If <code>off</code> is negative,
     * <code>len</code> is negative, or <code>len</code> is greater than
     * <code>b.length - off</code>
     */
    public synchronized int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }

        if (pos >= size) {
            return -1;
        }

        long l_avail = size - pos;
        if (l_avail < Integer.MAX_VALUE) {
            int i_avail = (int) (l_avail);
            if (len > i_avail) {
                len = i_avail;
            }
        }
        if (len <= 0) {
            return 0;
        }

        int chunk = (int) (pos / CHUNK_SIZE);
        int local_offset = (int) (pos - (((long) chunk) * CHUNK_SIZE));
        if(local_offset + len < CHUNK_SIZE){
            System.arraycopy(data[chunk], local_offset, b, off, len);
        }else{
            int copySize1 = (int)(CHUNK_SIZE - local_offset);
            int copySize2 = (int)((long)local_offset + (long)len - CHUNK_SIZE);
            System.arraycopy(data[chunk], local_offset, b, off, copySize1 );
            System.arraycopy(data[chunk+1],    0, b, off+copySize1, copySize2 );
        }
//        return data[chunk][offset];
        pos += (long)len;
        return len;
    }

    /**
     * Skips
     * <code>n</code> bytes of input from this input stream. Fewer bytes might
     * be skipped if the end of the input stream is reached. The actual number
     * <code>k</code> of bytes to be skipped is equal to the smaller of
     * <code>n</code> and
     * <code>count-pos</code>. The value
     * <code>k</code> is added into
     * <code>pos</code> and
     * <code>k</code> is returned.
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     */
    public synchronized long skip(long n) {
        long k = size - pos; // about of bytes left
        if (n < k) {
            k = n < 0 ? 0 : n;
        }

        pos += k;
        return k;
    }

    /**
     * Returns the number of remaining bytes that can be read (or skipped over)
     * from this input stream. <p> The value returned is
     * <code>count&nbsp;- pos</code>, which is the number of bytes remaining to
     * be read from the input buffer.
     *
     * @return the number of remaining bytes that can be read (or skipped over)
     * from this input stream without blocking.
     */
    public synchronized int available() {
        long left = size - pos;
        if(left > Integer.MAX_VALUE){
            return Integer.MAX_VALUE;
        }else{
            return (int)left;
        }
    }

    /**
     * Tests if this
     * <code>InputStream</code> supports mark/reset. The
     * <code>markSupported</code> method of
     * <code>ByteArrayInputStream</code> always returns
     * <code>true</code>.
     *
     * @since JDK1.1
     */
    public boolean markSupported() {
        return true;
    }

    /**
     * Set the current marked position in the stream. ByteArrayInputStream
     * objects are marked at position zero by default when constructed. They may
     * be marked at another position within the buffer by this method. <p> If no
     * mark has been set, then the value of the mark is the offset passed to the
     * constructor (or 0 if the offset was not supplied).
     *
     * <p> Note: The
     * <code>readAheadLimit</code> for this class has no meaning.
     *
     * @since JDK1.1
     */
    public void mark(int readAheadLimit) {
        mark = pos;
    }

    /**
     * Resets the buffer to the marked position. The marked position is 0 unless
     * another position was marked or an offset was specified in the
     * constructor.
     */
    public synchronized void reset() {
        pos = mark;
    }

    /**
     * Closing a <tt>ByteArrayInputStream</tt> has no effect. The methods in
     * this class can be called after the stream has been closed without
     * generating an <tt>IOException</tt>. <p>
     */
    public void close() throws IOException {
    }
}
