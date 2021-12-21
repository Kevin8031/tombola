public enum MessageType {
	NewNumber,

	CheckCombo,
	SetName,

	// multicast
	LAN_SERVER_DISCOVEY;


	public String toString() {
		return name();
	}
}
