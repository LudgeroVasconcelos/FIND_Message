package find.message.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import message.find.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import find.message.ui.MessageController;
import find.message.ui.Ui;
import find.message.ui.UserController;

public class MessageBoard extends ActionBarActivity {

	public static final String TAG = "receive";

	private static final String FIND_SERVICE = "find.service.net.diogomarques.wifioppish.service.LOSTService";
	private static final String PROVIDER = "content://find.service.net.diogomarques.wifioppish.MessagesProvider";
	private static final String NODEID = PROVIDER + "/status";
	private static final String CUSTOMSEND = PROVIDER + "/customsend";
	private static final String INFO = PROVIDER + "/info";
	private static final String DATA = PROVIDER + "/data";

	public static final Uri NODEID_URI = Uri.parse(NODEID);
	public static final Uri CUSTOMSEND_URI = Uri.parse(CUSTOMSEND);
	public static final Uri INFO_URI = Uri.parse(INFO);
	public static final Uri DATA_URI = Uri.parse(DATA);

	private MessageController mController;
	private UserController uController;

	private Map<Message, Message> messages;
	private Map<String, User> users;

	private String myNodeId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// start the Lost Service
		startService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setIcon(R.drawable.logo);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// users button clicked
		uController.usersButtonClicked();
		return true;
	}

	private void startService() {
		// ensure Lost Service is installed
		Intent scanIntent = getPackageManager().getLaunchIntentForPackage(
				"find.service");

		if (scanIntent == null) {
			// the service is not installed. Display it on google play and close
			displayOnGooglePlay();
			finish();

		} else if (isServiceRunning()) {
			// the service is running. Retrieve node id
			RetrieveDataTask rdt = new RetrieveDataTask();
			rdt.execute();
		} else {
			// ask the user to start the service
			Builder builder = buildAlertDialog(this);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	private void displayOnGooglePlay() {
		Toast.makeText(this, getString(R.string.requires_installation),
				Toast.LENGTH_LONG).show();

		Intent marketIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("market://details?id=find.service"));

		Intent googlePlayIntent = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://play.google.com/store/apps/details?id=find.service"));

		try {
			startActivity(marketIntent);
		} catch (ActivityNotFoundException e2) {
			startActivity(googlePlayIntent);
		}
	}

	private boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {

			if (FIND_SERVICE.equals(service.service.getClassName()))
				return true;
		}

		return false;
	}

	/**
	 * Builds the alert dialog to ask the user to start the service.
	 * 
	 * @return The alert dialog's builder
	 */
	private Builder buildAlertDialog(final Activity myActivity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(R.string.dialog_message).setTitle(
				R.string.dialog_title);

		// start service if the OK button is pressed
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				// start service
				//Intent svcIntent = new Intent(FIND_SERVICE + ".START_SERVICE");
				Intent svcIntent = new Intent();
				svcIntent.setComponent(new ComponentName("find.service", FIND_SERVICE));
				//svcIntent.setAction(FIND_SERVICE + ".START_SERVICE");

				ComponentName cn = startService(svcIntent);
				if(cn == null){
					Log.d(TAG, "cn is null");
				} else {
					Log.d(TAG, cn.getPackageName() + " " + cn.getClassName());
				}

				// retrieve node id
				RetrieveDataTask rdt = new RetrieveDataTask();
				rdt.execute();
			}
		});

		// close activity if the Cancel button or the back button is pressed
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});

		return builder;
	}

	private class RetrieveDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			final String[] columns = { "statuskey", "statusvalue" };
			final String whereClause = columns[0] + "= ?";
			final String[] whereArgs = { "nodeid" };

			String nodeId = "Unknown";

			do {
				Cursor c = getContentResolver().query(NODEID_URI, columns,
						whereClause, whereArgs, null);

				if (c != null) {
					if (c.getCount() > 0) {

						c.moveToFirst();
						nodeId = c.getString(1);
					}
					c.close();
				}

				if (nodeId.equals("Unknown")) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			} while (nodeId.equals("Unknown"));

			return nodeId;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("TAG", "My node id is: " + result);

			// the service is running and the node id has been retrieved.
			// It's time to start our APP
			startApp(result);
		}
	}

	private void startApp(String myNodeId) {
		this.myNodeId = myNodeId;

		// create list of messages
		this.messages = new HashMap<Message, Message>();

		// create list of users and add myself
		this.users = new HashMap<String, User>();
		users.put(myNodeId, new User(myNodeId, 0, 0, 0));

		// create UI
		Ui ui = new Ui(this);

		// create controllers
		mController = new MessageController(ui, this);
		uController = new UserController(ui, this);

		// connect components
		ui.start(mController, uController);

		// restore previous messages
		mController.addMessages(false);
		uController.restoreUsers();

		// listen for new incoming messages
		getContentResolver().registerContentObserver(INFO_URI, true,
				mController);
	}

	public boolean add(Message message) {

		if (messages.containsKey(message)) {

			Message sameMessage = messages.get(message);

			long t1 = message.getStatusTimestamp();
			long t2 = sameMessage.getStatusTimestamp();

			// update message to get an updated message status
			if (t1 > t2) {
				messages.put(message, message);
				return true;
			}
		} else {
			messages.put(message, message);
			Log.i(TAG, "new message added: " + message.getText());
			return true;
		}
		return false;
	}

	public void add(User user) {
		String id = user.getUserId();

		if (users.containsKey(id)) {

			User sameUser = users.get(id);
			long t1 = user.getTimestamp();
			long t2 = sameUser.getTimestamp();

			// update user to get the most current one
			if (t1 > t2)
				users.put(id, user);

		} else {
			Log.i(TAG, "new user added: " + user.getUserId());
			users.put(id, user);
		}
	}

	public User getMe() {
		return users.get(myNodeId);
	}

	public Collection<User> getUsers() {
		return users.values();
	}

	public List<Message> getMessages() {
		return new ArrayList<Message>(messages.values());
	}

	@Override
	protected void onDestroy() {
		if (mController != null)
			mController.stop();

		super.onDestroy();
	}
}