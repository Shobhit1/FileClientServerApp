import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class FileClient {

	public static int PORT = 12555;

	public static void main(String[] args) throws Exception 
	{
		while(true)
		{
			BufferedReader InputReader = new java.io.BufferedReader(new InputStreamReader(System.in));
			System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
			System.out.println("Provide the action to do:");
			System.out.println("0 --> Request the server to send updated files");
			System.out.println("1 --> Update/send the server with the updated files");

			int readerInput = InputReader.read();

			Socket Conection_Socket = new Socket("54.69.68.192", PORT);	// Connect to the server

			OutputStream OutStream = Conection_Socket.getOutputStream();
			InputStream InStream = Conection_Socket.getInputStream();			

			System.out.println("Connected to the server at port "+PORT);

			new FileClient().DoAction(OutStream,InStream,readerInput);


			Conection_Socket.close();
		}
	}


	public void DoAction(OutputStream OutStream, InputStream InStream, int readerInput) throws Exception 
	{

		DataOutputStream Data_Stream_Out 	= new DataOutputStream((OutStream));
		DataInputStream Data_Steam_In 	= new DataInputStream((InStream));
		int Request_Update = readerInput;

		Data_Stream_Out.writeInt(readerInput);

		if(Request_Update == '0')	// 0 -> Client has an request for files, server sends the files to client									
		{
			//Read the number of files from the server
			int Num_Files = Data_Steam_In.readInt();

			ArrayList<File>files = new ArrayList<File>(Num_Files);

			System.out.println("Number of Files to be received: " +Num_Files);

			//read file names, add files to arraylist
			for(int i = 0; i< Num_Files;i++)
			{
				File file = new File(Data_Steam_In.readUTF());
				files.add(file);
			}

			int n = 0;
			byte[]buf = new byte[4092];

			for(int i = 0; i < files.size();i++)
			{
				System.out.println("Receiving file: " + files.get(i).getName());
				//create a new File_Out_Stream for each new file
				//FileOutputStream File_Out_Stream = new FileOutputStream("C:/Sudarshan/Technical_Works/Java_Programming/ServerClient_FileTransfer/File_Transfer/Client_Dropbox_Folder/" +files.get(i).getName());

				FileOutputStream File_Out_Stream = new FileOutputStream("../Client_Dropbox_Folder/" +files.get(i).getName());

				//read file

				long fileSize = Data_Steam_In.readLong();
				while (fileSize > 0 && (n = Data_Steam_In.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1)      
				{
					File_Out_Stream.write(buf,0,n);
					fileSize -= n;
				}
				File_Out_Stream.close();
			}

		}
		else if(Request_Update == '1')	// 1 -> Client has an updated file, sends the files to server
		{
			System.out.println("\nSending files to server...");

			String dirname = "../Client_Dropbox_Folder";

			File F1 = new File(dirname);
			File F1_List[] = F1.listFiles();

			for(int i=0; i<F1_List.length;i++)
			{		
				int j=i+1;
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

	}
} 