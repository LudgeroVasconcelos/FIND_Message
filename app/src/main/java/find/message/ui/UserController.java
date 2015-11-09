package find.message.ui;

import java.util.Comparator;

import android.database.Cursor;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.View;
import find.message.model.MessageBoard;
import find.message.model.User;

public class UserController implements DrawerListener, Comparator<User> {

	// query parameters
	private final String[] columns = { "sender_mac", "tempo_geracao", "latitude",
			"longitude" };

	private Ui ui;
	private MessageBoard mBoard;

	public UserController(Ui ui, MessageBoard messageBoard) {
		this.ui = ui;
		this.mBoard = messageBoard;
	}

	public void usersButtonClicked() {
		if (!ui.isDrawerOpen()) {
			ui.openDrawer();
		} else {
			ui.closeDrawer();
		}
	}

	private void checkUsers() {
		Cursor c = mBoard.getContentResolver().query(MessageBoard.INFO_URI,
				columns, null, null, null);

		if (c.getCount() > 0) {
			c.moveToFirst();
			do {
				addUser(c);
			} while (c.moveToNext());
		} else
			c.close();
	}

	private void addUser(Cursor row) {
		String userId = row.getString(row.getColumnIndex(columns[0]));
		long timestamp = row.getLong(row.getColumnIndex(columns[1]));
		long latitude = row.getLong(row.getColumnIndex(columns[2]));
		long longitude = row.getLong(row.getColumnIndex(columns[3]));

//		Log.d(MessageBoard.TAG, userId + " " + timestamp + " " + latitude + " "
//				+ longitude);

		User u = new User(userId, timestamp, latitude, longitude);

		mBoard.add(u);
	}

	public void restoreUsers() {
		if (ui.isDrawerOpen()) {
			showUsers();
		}
	}

	@Override
	public void onDrawerClosed(View arg0) {
	}

	@Override
	public void onDrawerOpened(View arg0) {
		showUsers();
	}

	@Override
	public void onDrawerSlide(View arg0, float arg1) {
	}

	@Override
	public void onDrawerStateChanged(int arg0) {
	}

	@Override
	public int compare(User u1, User u2) {
		User me = mBoard.getMe();

		if (u1.equals(me) || u1.distanceBetween(me) < u2.distanceBetween(me))
			return -1;
		else
			return 1;
	}

	private void showUsers() {
		checkUsers();
		ui.addUsers(mBoard.getUsers(), this);
	}
}