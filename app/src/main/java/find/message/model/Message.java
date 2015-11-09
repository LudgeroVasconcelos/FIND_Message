package find.message.model;


public class Message {

	private String nodeId;
	private String text;
	private String status;
	private long timestamp;
	private long statusTimestamp;
	//private long added;

	public Message(String nodeId, String text, String status, long timestamp,
			long statusTimestamp /*, long added*/) {

		this.nodeId = nodeId;
		this.text = text;
		this.status = status;
		this.timestamp = timestamp;
		this.statusTimestamp = statusTimestamp;
		//this.added = added;
	}

	public String getText() {
		return text;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public long getStatusTimestamp() {
		return statusTimestamp;
	}

	/*
	public long getTimeAdded() {
		return added;
	}*/
	
	public boolean isFrom(String nodeId) {
		return this.nodeId.equals(nodeId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		Message other = (Message) obj;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equalsIgnoreCase(other.text))
			return false;
		return true;
	}

}