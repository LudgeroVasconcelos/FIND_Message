package find.message.ui;

import message.find.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import find.message.model.User;

public class UsersArrayAdapter extends ArrayAdapter<User> {

	private User me;

	public UsersArrayAdapter(Context context, int resource, User me) {
		super(context, resource);
		this.me = me;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.user_item, parent, false);
		}
		User user = getItem(position);

		TextView id = (TextView) convertView.findViewById(R.id.userId);
		TextView distance = (TextView) convertView
				.findViewById(R.id.userDistance);

		if (user.equals(me)) {
			id.setText("Me");

			// update 'me' to get the most current distances
			if (user.getTimestamp() > this.me.getTimestamp())
				me = user;
		}
		else {
			id.setText(user.getUserId());
			
			if (me.hasLocation() && user.hasLocation())
				distance.setText("Distance: ~ " + user.distanceBetween(me)
						+ "m");
			else
				distance.setText("Distance: Unknown");
		}

		return convertView;
	}
}
