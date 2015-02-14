import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class FileServer {
		
	public static void main(String[] args) throws Exception 
	{
		// create socket
		ServerSocket Server_Socket = new ServerSocket(12555);
		while (true) 	
		{
			System.out.println("Waiting...");

			Socket Conection_Socket = Server_Socket.accept();		// Wait for a client connection

			OutputStream OutStream 	= Conection_Socket.getOutputStream();
			InputStream InStream 	= Conection_Socket.getInputStream();
		
			System.out.println("Accepted connection : " + Conection_Socket);
			new FileServer().DoAction(OutStream,InStream);
			Conection_Socket.close();
		}
	}

	public void DoAction(OutputStream os, InputStream is) throws Exception 
	{

		DataOutputStream Data_Stream_Out 	= new DataOutputStream( (os));
		DataInputStream Data_Steam_In 	= new DataInputStream((is));
		
		while(is.available()==0);

		int Request_Update = Data_Steam_In.readInt();		// 0 -> Client has an request for files, server sends the files to client
									// 1 -> Client has an updated file, client sends the files to server
				

		if(Request_Update == '0')		// File request from client			
		{
			System.out.println("File Request from Client");
			System.out.println("Sending the following files: ");

			String dirname = "../Dropbox_Folder";

			File F1 = new File(dirname);
			File F1_List[] = F1.listFiles();

			for(int i=0; i<F1_List.length;i++)
			{		
				int j=1+1;
				System.out.println(j+") "+F1_List[i].getName()+"  "+F1_List[i].length()+" Bytes");			
			}

			Data_Stream_Out.writeInt(F1_List.length); // Tell the number of files being transferred to the client
			Data_Stream_Out.flush();

			//Write the file names 
			for(int i = 0 ; i < F1_List.length;i++)
			{
				Data_Stream_Out.writeUTF(F1_List[i].getName());
				Data_Stream_Out.flush();        
			}

			/*  Write the file contents to the client */
			int n = 0;
			byte[]buf = new byte[4092];

			for(int i=0; i<F1_List.length;i++)
			{
				//create new fileinputstream for each file
				FileInputStream File_In_Stream = new FileInputStream(F1_List[i]);							
				
				Data_Stream_Out.writeLong(F1_List[i].length());

				//write file to Data_Stream_Out
				while((n = File_In_Stream.read(buf)) != -1)
				{
					Data_Stream_Out.write(buf,0,n);
					Data_Stream_Out.flush();
				}
				//File_In_Stream.close();
			}
			Data_Stream_Out.close();				
		}

		else if (Request_Update == '1')	// File update from client	
		{
			int Num_Files = Data_Steam_In.readInt();	// The number of files client is sending

			ArrayList<File>files = new ArrayList<File>(Num_Files);

			System.out.println("Number of Files to be received from client: " +Num_Files);

			//Read the file names, add files to arraylist
			for(int i = 0; i< Num_Files;i++)
			{
				File file = new File(Data_Steam_In.readUTF());
				files.add(file);
			}

			int n = 0;
			byte[]buf = new byte[4092];

			/* Get the the input stream from client */

			for(int i = 0; i < files.size();i++)
			{
				System.out.println("Receiving file: " + files.get(i).getName());
				//create a new File_Out_Stream for each new file			

				FileOutputStream File_Out_Stream = new FileOutputStream("../Dropbox_Folder/" +files.get(i).getName());
				
				//read file
				long fileSize = Data_Steam_In.readLong();
				while (fileSize > 0 && (n = Data_Steam_In.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1)      
				{
					File_Out_Stream.write(buf,0,n);
					fileSize -= n;
				}
				File_Out_Stream.close();
			}

		} // End of elseif
		
		else
		{
			System.out.println("Invalid request from client received. Ignoring request...\n");			
		}
	}	// End of DoAction
}	// End of FileServer class