public enum MessageType {
	NewNumber,

	CheckCombo,
	SetName,

	Disconnect,

	// multicast
	LAN_SERVER_DISCOVEY;


	public String toString() {
		return name();
	}
}
