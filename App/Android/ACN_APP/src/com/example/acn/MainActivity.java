package com.example.acn;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;



public class MainActivity extends ActionBarActivity{
	PersistenceLayer pl = new PersistenceLayer();;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	} 

	public void download(View v)
	{
		// 0 -> Client has an request for files, server sends the files to client									
		//Read the number of files from the server
//		pl.receiveFile();
		
		//pl.execute();
		//pl.receiveFile();

		String[] param = new String[1];
		param[0]="download";
		pl = new PersistenceLayer();
		pl.execute(param);

	}
	
	public void upload(View v)
	{
//		pl = new PersistenceLayer();
//		PersistenceLayer pl2 = new PersistenceLayer();
		//pl.execute();
		//pl.uploadFile();
		String[] param = new String[1];
		param[0]="upload";
		pl = new PersistenceLayer();
		pl.execute(param);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//pl.download();
		}
		return super.onOptionsItemSelected(item);
	}
}
