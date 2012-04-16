package com.joshdholtz.protocol.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLConnection;
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
		    writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true); // true = Autoflush, important!

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
			    writer.println("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName()));
			    writer.println("Content-Transfer-Encoding: binary");
			    writer.println();
			    
			    
		    	
			    InputStream input = null;
			    try {
			        input = new FileInputStream(file);
			        byte[] buffer = new byte[1024];
			        for (int length = 0; (length = input.read(buffer)) > 0;) {
			            output.write(buffer, 0, length);
			        }
			        output.flush();
			    } finally {
			        if (input != null) try { input.close(); } catch (IOException logOrIgnore) {}
			    }
			    
			    if (i < (fileNames.size() - 1)) {
			    	writer.println("--" + boundary);
			    }
			    
			    writer.println();
		    }

		    writer.println("--" + boundary + "--");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		    if (writer != null) writer.close();
		}
    }

}
