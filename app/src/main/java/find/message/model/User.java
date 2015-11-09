package find.message.model;

import android.location.Location;

public class User {

	private String userId;
	private double latitude;
	private double longitude;
	private long timestamp;

	public User(String userId, long timestamp, long latitude,
			long longitude) {
		super();
		this.userId = userId;
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;

	}

	public String getUserId() {
		return userId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Location getLocation() {
		Location loc = new Location("");
		loc.setLatitude(latitude);
		loc.setLongitude(longitude);

		return loc;
	}

	public int distanceBetween(User user) {
		Location myLocation = getLocation();
		Location userLocation = user.getLocation();

		return Math.round(myLocation.distanceTo(userLocation));
	}

	public boolean hasLocation() {
		return latitude != 0 && longitude != 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
}
