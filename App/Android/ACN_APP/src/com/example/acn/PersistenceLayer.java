package com.example.acn;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class PersistenceLayer extends AsyncTask <String, Void, Void>{


	String path = Environment.getExternalStorageDirectory().getAbsolutePath();
	String finalDirectoryPath = path + File.separator + "DropBox_Folder/";
	Socket socket;
	String foldername= "Download";
	DataOutputStream outputdata;
	DataInputStream inputdata;
	protected Void doInBackground(String... params){
		System.out.println(params[0]);
		System.out.println(finalDirectoryPath);
		//
		//		while(true)
		//		{
		// TODO Auto-generated method stub
		try {

			socket = new Socket("54.69.68.192", 12555);
			System.out.println("Connecting to the server");
			OutputStream os = socket.getOutputStream();
			outputdata = new DataOutputStream(os);
						//downloadFile();
			if(params[0].equalsIgnoreCase("upload"))
				uploadFile();
			else
				downloadFile();
			//receiveFile(is);
			//				Thread.sleep(1000);
			//				socket.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	//	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() 
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() 
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	public void downloadFile()
	{
		try{
			if ((isExternalStorageWritable()) && (isExternalStorageReadable()))
			{
				System.out.println("test");
				//				int request = 0;
				outputdata.writeInt('0');
				inputdata = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				int num_of_files = inputdata.readInt();
				ArrayList<File>files = new ArrayList<File>(num_of_files);
				Log.d("Hello", "BeforeNumberCheck");
				System.out.println("Number of Files to be received: " +num_of_files);

				//read file names, add files to arraylist
				for(int i = 0; i< num_of_files;i++)
				{
					File file = new File(inputdata.readUTF());
					files.add(file);
				}

				int n = 0;
				byte[]buffer = new byte[4092];

				for(int i = 0; i < files.size();i++)
				{
					//					File newFile = new File(path,files.get(i).getName());
					File myDir = new File(finalDirectoryPath);
					if(!myDir.exists())
					{
						myDir.mkdirs();
					}
					File newFile = new File(finalDirectoryPath,files.get(i).getName());
					if (!newFile.exists())
					{
						newFile.createNewFile();
					}
					System.out.println("Receiving file: " + files.get(i).getName());
					//create a new fileoutputstream for each new file

					FileOutputStream fos = new FileOutputStream(newFile);

					//read file

					long fileSize = inputdata.readLong();
					while (fileSize > 0 && (n = inputdata.read(buffer, 0, (int)Math.min(buffer.length, fileSize))) != -1)      
					{
						fos.write(buffer,0,n);
						fileSize -= n;
					}

					fos.close();
				}
				socket.close();
			}
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public void uploadFile()
	{
		try{
			
			outputdata = new DataOutputStream(socket.getOutputStream());
			
			if ((isExternalStorageReadable()))
			{
				Log.d("Hello", "Beforeoutputdata");
				outputdata.writeInt('1');
//				int num_of_files = inputdata.readInt();	// The number of files client is sending
				File F1 = new File(finalDirectoryPath);
				Log.d("Hello", finalDirectoryPath);
				File F1_List[] = F1.listFiles();
				System.out.println(F1_List);
				for(int i=0; i<F1_List.length;i++)
				{		
					int j=i+1;
					System.out.println(j+") "+F1_List[i].getName()+"  "+F1_List[i].length()+" Bytes");			
				}

				outputdata.writeInt(F1_List.length); // Tell the number of files being transferred to the client
				outputdata.flush();

				//Write the file names 
				for(int i = 0 ; i < F1_List.length;i++)
				{
					System.out.println("8677878787");
					outputdata.writeUTF(F1_List[i].getName());
					outputdata.flush(); 
					System.out.println("0000000000");
				}

				/*  Write the file contents to the client */
				int n = 0;
				byte[]buffer = new byte[4092];

				for(int i=0; i<F1_List.length;i++)
				{
					//create new fileinputstream for each file
					FileInputStream fis = new FileInputStream(F1_List[i]);

					outputdata.writeLong(F1_List[i].length());

					//write file to outputdata
					while((n = fis.read(buffer)) != -1)
					{
						outputdata.write(buffer,0,n);
						outputdata.flush();
					}
					fis.close();
				}
			}
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}