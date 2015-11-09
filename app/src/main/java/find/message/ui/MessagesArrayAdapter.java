package find.message.ui;

import java.text.SimpleDateFormat;
import java.util.Locale;

import message.find.R;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import find.message.model.Message;

public class MessagesArrayAdapter extends ArrayAdapter<Message> {

	private String myNodeId;
	private Context context;

	public MessagesArrayAdapter(Context context, int textViewResourceId,
			String myNodeId) {
		super(context, textViewResourceId);
		this.myNodeId = myNodeId;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(R.layout.message_item, parent, false);
		}

		LinearLayout wrapper = (LinearLayout) convertView
				.findViewById(R.id.wrapper);
		LinearLayout contents = (LinearLayout) convertView
				.findViewById(R.id.contents);

		Message message = getItem(position);

		// message text
		TextView textView = (TextView) convertView.findViewById(R.id.message);
		textView.setText(message.getText());

		// message status
		TextView statusView = (TextView) convertView.findViewById(R.id.status);
		statusView.setTextColor(Color.DKGRAY);
		
		if (message.getStatus()
				.equals(context.getString(R.string.message_created))){
			statusView.setText("Waiting to send");
		}
		else if (message.getStatus()
				.equals(context.getString(R.string.message_sent))) {
			statusView.setText("Sent to another person");
		}
		else if(message.getStatus()
				.equals(context.getString(R.string.message_rec_vic)) || message.getStatus()
				.equals(context.getString(R.string.message_rec_res))){
			statusView.setText("Received from another person");
		}
		else{
			statusView.setText("Sent to Control Centre");
		}

		// message timestamp
		TextView dateView = (TextView) convertView.findViewById(R.id.date);
		dateView.setText(new SimpleDateFormat("HH'h'mm, d MMM", Locale
				.getDefault()).format(message.getTimestamp()));
		dateView.setTextColor(Color.DKGRAY);
		
		// message bubble
		contents.setBackgroundResource(message.isFrom(myNodeId) ? R.drawable.bubble_green
				: R.drawable.bubble_yellow);
		wrapper.setGravity(message.isFrom(myNodeId) ? Gravity.RIGHT
				: Gravity.LEFT);

		return convertView;
	}
}