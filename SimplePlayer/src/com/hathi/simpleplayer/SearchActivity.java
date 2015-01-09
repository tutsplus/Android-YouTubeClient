package com.hathi.simpleplayer;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SearchActivity extends Activity {

	private EditText searchInput;
	private ListView videosFound;
	
	private Handler handler;
	
	private List<VideoItem> searchResults;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		searchInput = (EditText)findViewById(R.id.search_input);
		videosFound = (ListView)findViewById(R.id.videos_found); 
		
		handler = new Handler();
		
		addClickListener();
		
		searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {			
				if(actionId == EditorInfo.IME_ACTION_DONE){
					searchOnYoutube(v.getText().toString());
					return false;
				}
				return true;
			}
		});
				
	}
	
	private void addClickListener(){
		videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos,
					long id) {				
				Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
				intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
				startActivity(intent);
			}
			
		});
	}
	
	private void searchOnYoutube(final String keywords){
		new Thread(){
			public void run(){
				YoutubeConnector yc = new YoutubeConnector(SearchActivity.this);
				searchResults = yc.search(keywords);				
				handler.post(new Runnable(){
					public void run(){
						updateVideosFound();
					}
				});
			}
		}.start();
	}
	
	private void updateVideosFound(){
		ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.video_item, searchResults){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if(convertView == null){
					convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
				}
				ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
				TextView title = (TextView)convertView.findViewById(R.id.video_title);
				TextView description = (TextView)convertView.findViewById(R.id.video_description);
				
				VideoItem searchResult = searchResults.get(position);
				
				Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
				title.setText(searchResult.getTitle());
				description.setText(searchResult.getDescription());
				return convertView;
			}
		};			
		
		videosFound.setAdapter(adapter);
	}
}
