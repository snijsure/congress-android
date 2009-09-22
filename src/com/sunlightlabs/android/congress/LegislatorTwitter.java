package com.sunlightlabs.android.congress;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LegislatorTwitter extends ListActivity {
	static final int LOADING_TWEETS = 0;
	
	private String username;
	private Status[] tweets;
    	
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	username = getIntent().getStringExtra("username");
    	
    	loadTweets();
	}
	
    // Define the Handler that receives messages from the thread and update the progress
    final Handler handler = new Handler();
    final Runnable updateTweets = new Runnable() {
        public void run() {
        	setListAdapter(new TweetAdapter(LegislatorTwitter.this, tweets));
        	dismissDialog(LOADING_TWEETS);
        }
    };
	
	protected void loadTweets() {
		Thread twitterThread = new Thread() {
	        public void run() { 
	        	try {
	        		Twitter twitter = new Twitter();
	        		List<Status> tweetList = twitter.getUserTimeline(username);
	        		tweets = (Status[]) tweetList.toArray(new Status[0]);
	        	} catch(TwitterException e) {
	        		Log.e("ERROR", e.getMessage());
	        	}
	        	handler.post(updateTweets);
	        }
	    };
	    twitterThread.start();
	    
		showDialog(LOADING_TWEETS);
	}
    
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case LOADING_TWEETS:
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Plucking tweets from the air...");
            return dialog;
        default:
            return null;
        }
    }
    
    protected class TweetAdapter extends BaseAdapter {
    	private Activity context;
    	private Status[] tweets;
    	LayoutInflater inflater;

        public TweetAdapter(Activity c, Status[] tw) {
            context = c;
            tweets = tw;
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		public int getCount() {
			return tweets.length;
		}

		public Object getItem(int position) {
			return tweets[position];
		}

		public long getItemId(int position) {
			Status tweet = (Status) getItem(position);
			return tweet.getId();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView text;
			if (convertView == null) {
				//text = (View) inflater.inflate(R.layout.legislator_tweet, null);
				text = new TextView(context);
			} else {
				text = (TextView) convertView;
			}
			Status tweet = (Status) getItem(position);
			text.setText(tweet.getText());
			return text;
		}

    }

}