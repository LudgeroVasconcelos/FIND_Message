package find.message.ui;

import java.util.Collection;
import java.util.Comparator;

import message.find.R;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import find.message.model.Message;
import find.message.model.MessageBoard;
import find.message.model.User;

/**
 * Main Ui class
 * 
 * @author Ludgero
 * 
 */
public class Ui {
	private MessageBoard mBoard;

	private EditText editText;
	private ListView messageList;
	private ListView userList;
	private MessagesArrayAdapter messageAdapter;
	private UsersArrayAdapter userAdapter;
	private DrawerLayout drawer;

	public Ui(MessageBoard mBoard) {
		this.mBoard = mBoard;

		mBoard.setContentView(R.layout.message_board);

		Context context = mBoard.getApplicationContext();

		editText = (EditText) mBoard.findViewById(R.id.editText);

		messageList = (ListView) mBoard.findViewById(R.id.messagesListView);
		messageAdapter = new MessagesArrayAdapter(context,
				R.layout.message_item, mBoard.getMe().getUserId());

		userList = (ListView) mBoard.findViewById(R.id.users_drawer);
		userAdapter = new UsersArrayAdapter(context, R.layout.user_item,
				mBoard.getMe());

		drawer = (DrawerLayout) mBoard.findViewById(R.id.drawer_layout);
	}

	public void start(MessageController mController, UserController uController) {
		editText.setOnKeyListener(mController);
		drawer.setDrawerListener(uController);

		messageList.setAdapter(messageAdapter);
		userList.setAdapter(userAdapter);
	}

	public void addMessages(final Collection<Message> messages,
			final Comparator<Message> comparator) {
		mBoard.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				messageAdapter.clear();

				for (Message m : messages) {
					messageAdapter.add(m);
				}
				messageAdapter.sort(comparator);
			}
		});
	}

	public void addUsers(final Collection<User> users,
			final Comparator<User> userComp) {
		mBoard.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				userAdapter.clear();

				for (User u : users) {
					userAdapter.add(u);
				}
				userAdapter.sort(userComp);
			}
		});
	}

	public void showToast(final String toastText) {
		mBoard.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast toast = Toast.makeText(mBoard, toastText,
						Toast.LENGTH_LONG);
				toast.show();
			}
		});
	}

	public boolean isDrawerOpen() {
		return drawer.isDrawerOpen(mBoard.findViewById(R.id.users_drawer));
	}

	public void openDrawer() {
		drawer.openDrawer(mBoard.findViewById(R.id.users_drawer));
	}

	public void closeDrawer() {
		drawer.closeDrawers();
	}
}
