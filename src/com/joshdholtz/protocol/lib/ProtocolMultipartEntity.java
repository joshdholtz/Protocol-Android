package com.joshdholtz.protocol.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.joshdholtz.protocol.lib.helpers.ProtocolConstants;

public class ProtocolMultipartEntity extends BasicHttpEntity {
	
	private String boundary;
	private List<BasicNameValuePair> params;
	private Map<String, File> files;
	
	private long size = 0;
	int forRealSize = 0;
	
	public ProtocolMultipartEntity (String boundary, File file) {
		this.boundary = boundary;
		this.params = new ArrayList<BasicNameValuePair>();
		this.files = new HashMap<String, File>();
		this.files.put("file1", file);
	}
	
	public ProtocolMultipartEntity (String boundary, List<File> files) {
		this.boundary = boundary;
		this.params = new ArrayList<BasicNameValuePair>();;
		this.files = new HashMap<String, File>();
		
		for (int i = 0; i < files.size(); ++i) {
			this.files.put("file" + (i+1), files.get(i));
		}
	}
	
	public ProtocolMultipartEntity (String boundary, List<BasicNameValuePair> params, List<File> files) {
		this.boundary = boundary;
		this.params = params;
		this.files = new HashMap<String, File>();
		
		for (int i = 0; i < files.size(); ++i) {
			this.files.put("file" + (i+1), files.get(i));
		}
	}
	
	public ProtocolMultipartEntity (String boundary, List<BasicNameValuePair> params, Map<String, File> files) {
		this.boundary = boundary;
		this.params = params;
		this.files = files;
	}
	
	@Override
    public void writeTo(final OutputStream output) throws IOException {
		
		PrintWriter writer = null;
		try {
			CountingOutputStream countingOutputStream = new CountingOutputStream(output);
		    writer = new PrintWriter(new OutputStreamWriter(countingOutputStream, "UTF-8"), true); // true = Autoflush, important!
		    

		    for (int i = 0; i < params.size(); ++i) {
			    writer.println("--" + boundary);
			    writer.println("Content-Disposition: form-data; name=\"" + params.get(i).getName() + "\"");
			    writer.println("Content-Type: text/plain; charset=UTF-8");
			    writer.println();
			    writer.println(params.get(i).getValue());
		    }
		    
		    List<String> fileNames = new ArrayList<String>(files.keySet());
		    for (int i = 0; i < fileNames.size(); ++i) {
		    	File file = files.get(fileNames.get(i));
		    	
		    	writer.println("--" + boundary);
			    writer.println("Content-Disposition: form-data; name=\"" + fileNames.get(i) + "\"; filename=\"" + i + file.getName() + "\"");
			    Log.d(ProtocolConstants.LOG_TAG, "Content-Disposition: form-data; name=\"" + fileNames.get(i) + "\"; filename=\"" + i + file.getName() + "\"");
//			    writer.println("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()));
			    writer.println("Content-Type: application/octet-stream");
//			    writer.println("Content-Transfer-Encoding: binary");
			    writer.println();
		    	
			    InputStream input = null;
			    try {
			        input = new FileInputStream(file);
			        byte[] buffer = new byte[1024];
			        for (int length = 0; (length = input.read(buffer)) > 0;) {
			        	countingOutputStream.write(buffer, 0, length);
			        }
			        countingOutputStream.flush();
			    } finally {
			        if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
			    }
			    
//			    if (i < (fileNames.size() - 1)) {
//			    	writer.println("--" + boundary);
//			    }
			    
			    writer.println();
		    }

		    writer.println("--" + boundary + "--");
		    
		    Log.d(ProtocolConstants.LOG_TAG, "Counting size - " + countingOutputStream.getByteCount());
		    size = countingOutputStream.getByteCount();
		    
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    if (writer != null) writer.close();
		}
    }
	
	@Override
	public boolean isRepeatable() {
		return false;
	}
	
	@Override
	public long getContentLength() {
		Log.d(ProtocolConstants.LOG_TAG, "Calling mutlipart getContentLength() - " + forRealSize);
		return forRealSize;
	}
	
	public long forRealSize() {
		    for (int i = 0; i < params.size(); ++i) {
			    println("--" + boundary);
			    println("Content-Disposition: form-data; name=\"" + params.get(i).getName() + "\"");
			    println("Content-Type: text/plain; charset=UTF-8");
			    println();
			    println(params.get(i).getValue());
		    }
		    
		    List<String> fileNames = new ArrayList<String>(files.keySet());
		    for (int i = 0; i < fileNames.size(); ++i) {
		    	File file = files.get(fileNames.get(i));
		    	
		    	println("--" + boundary);
			    println("Content-Disposition: form-data; name=\"" + fileNames.get(i) + "\"; filename=\"" + i + file.getName() + "\"");
			    Log.d(ProtocolConstants.LOG_TAG, "Content-Disposition: form-data; name=\"" + fileNames.get(i) + "\"; filename=\"" + i + file.getName() + "\"");
//			    writer.println("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()));
			    println("Content-Type: application/octet-stream");
//			    println("Content-Transfer-Encoding: binary");
			    println();
		    	
			   forRealSize += file.length();
			    
			    println();
		    }

		    println("--" + boundary + "--");
		    
		  return forRealSize;
	}
	
	public void println() {
		forRealSize += "\n".getBytes().length;
	}
	
	public void println(String str) {
		str = str + "\n";
		forRealSize += str.getBytes().length;
	}
	
	public class ProxyOutputStream extends FilterOutputStream {

	    /**
	     * Constructs a new ProxyOutputStream.
	     * 
	     * @param proxy  the OutputStream to delegate to
	     */
	    public ProxyOutputStream(OutputStream proxy) {
	        super(proxy);
	        // the proxy is stored in a protected superclass variable named 'out'
	    }

	    /**
	     * Invokes the delegate's <code>write(int)</code> method.
	     * @param idx the byte to write
	     * @throws IOException if an I/O error occurs
	     */
	    public void write(int idx) throws IOException {
	        out.write(idx);
	    }

	    /**
	     * Invokes the delegate's <code>write(byte[])</code> method.
	     * @param bts the bytes to write
	     * @throws IOException if an I/O error occurs
	     */
	    public void write(byte[] bts) throws IOException {
	        out.write(bts);
	    }

	    /**
	     * Invokes the delegate's <code>write(byte[])</code> method.
	     * @param bts the bytes to write
	     * @param st The start offset
	     * @param end The number of bytes to write
	     * @throws IOException if an I/O error occurs
	     */
	    public void write(byte[] bts, int st, int end) throws IOException {
	        out.write(bts, st, end);
	    }

	    /**
	     * Invokes the delegate's <code>flush()</code> method.
	     * @throws IOException if an I/O error occurs
	     */
	    public void flush() throws IOException {
	        out.flush();
	    }

	    /**
	     * Invokes the delegate's <code>close()</code> method.
	     * @throws IOException if an I/O error occurs
	     */
	    public void close() throws IOException {
	        out.close();
	    }

	}
	
	public class CountingOutputStream extends ProxyOutputStream {

	    /** The count of bytes that have passed. */
	    private long count;

	    /**
	     * Constructs a new CountingOutputStream.
	     * 
	     * @param out  the OutputStream to write to
	     */
	    public CountingOutputStream( OutputStream out ) {
	        super(out);
	    }

	    //-----------------------------------------------------------------------
	    /**
	     * Writes the contents of the specified byte array to this output stream
	     * keeping count of the number of bytes written.
	     *
	     * @param b  the bytes to write, not null
	     * @throws IOException if an I/O error occurs
	     * @see java.io.OutputStream#write(byte[])
	     */
	    public void write(byte[] b) throws IOException {
	        count += b.length;
	        super.write(b);
	    }

	    /**
	     * Writes a portion of the specified byte array to this output stream
	     * keeping count of the number of bytes written.
	     *
	     * @param b  the bytes to write, not null
	     * @param off  the start offset in the buffer
	     * @param len  the maximum number of bytes to write
	     * @throws IOException if an I/O error occurs
	     * @see java.io.OutputStream#write(byte[], int, int)
	     */
	    public void write(byte[] b, int off, int len) throws IOException {
	        count += len;
	        super.write(b, off, len);
	    }

	    /**
	     * Writes a single byte to the output stream adding to the count of the
	     * number of bytes written.
	     *
	     * @param b  the byte to write
	     * @throws IOException if an I/O error occurs
	     * @see java.io.OutputStream#write(int)
	     */
	    public void write(int b) throws IOException {
	        count++;
	        super.write(b);
	    }

	    //-----------------------------------------------------------------------
	    /**
	     * The number of bytes that have passed through this stream.
	     * <p>
	     * NOTE: From v1.3 this method throws an ArithmeticException if the
	     * count is greater than can be expressed by an <code>int</code>.
	     * See {@link #getByteCount()} for a method using a <code>long</code>.
	     *
	     * @return the number of bytes accumulated
	     * @throws ArithmeticException if the byte count is too large
	     */
	    public synchronized int getCount() {
	        long result = getByteCount();
	        if (result > Integer.MAX_VALUE) {
	            throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
	        }
	        return (int) result;
	    }

	    /** 
	     * Set the byte count back to 0. 
	     * <p>
	     * NOTE: From v1.3 this method throws an ArithmeticException if the
	     * count is greater than can be expressed by an <code>int</code>.
	     * See {@link #resetByteCount()} for a method using a <code>long</code>.
	     *
	     * @return the count previous to resetting
	     * @throws ArithmeticException if the byte count is too large
	     */
	    public synchronized int resetCount() {
	        long result = resetByteCount();
	        if (result > Integer.MAX_VALUE) {
	            throw new ArithmeticException("The byte count " + result + " is too large to be converted to an int");
	        }
	        return (int) result;
	    }

	    /**
	     * The number of bytes that have passed through this stream.
	     * <p>
	     * NOTE: This method is an alternative for <code>getCount()</code>.
	     * It was added because that method returns an integer which will
	     * result in incorrect count for files over 2GB.
	     *
	     * @return the number of bytes accumulated
	     * @since Commons IO 1.3
	     */
	    public synchronized long getByteCount() {
	        return this.count;
	    }

	    /** 
	     * Set the byte count back to 0. 
	     * <p>
	     * NOTE: This method is an alternative for <code>resetCount()</code>.
	     * It was added because that method returns an integer which will
	     * result in incorrect count for files over 2GB.
	     *
	     * @return the count previous to resetting
	     * @since Commons IO 1.3
	     */
	    public synchronized long resetByteCount() {
	        long tmp = this.count;
	        this.count = 0;
	        return tmp;
	    }

	}

}
