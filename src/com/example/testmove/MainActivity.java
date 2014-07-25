package com.example.testmove;

import com.finals.net.NetUtil;
import com.finals.net.OnConnectionOver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnConnectionOver {

	Button test;
	TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		test = (Button) findViewById(R.id.test);
		text = (TextView) findViewById(R.id.text);
		test.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	boolean isClicked = false;

	@Override
	public void onClick(View view) {
		if (!isClicked) {
			test.setText("ÏÂÔØ");
			isClicked = true;
			NetUtil.PostExce("http://developer.android.com/tools/sdk/tools-notes.html", NetUtil.GET, null, this);
		} else {
			test.setText("È¡Ïû");
			isClicked = false;
			NetUtil.CancelExce("http://developer.android.com/tools/sdk/tools-notes.html");
		}
	}

	@Override
	public void ConnectionOver(final String result, boolean isCancel) {
		System.out.println("resut" + result);
	}

}
