package com.joshdholtz.protocol.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.joshdholtz.protocol.lib.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

public class ProtocolActivity extends Activity {
	
	ImageView img;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		img = (ImageView) this.findViewById(R.id.img);
		
	}

	private void uploadFile() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, 0); 
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

		switch(requestCode) { 
		case 0:
			if(resultCode == RESULT_OK){  
				Uri selectedImage = imageReturnedIntent.getData();
				String[] filePathColumn = {MediaStore.Images.Media.DATA};

				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();

				Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

				File file = new File(filePath);

				List<File> files = new ArrayList<File>();
				files.add(file);
				

			}
		}
	}

}