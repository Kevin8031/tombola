package net;
public enum MessageType {
	NewNumber,

	CheckCombo,
	SetName,
	GetTabella,

	Disconnect,

	// multicast
	LAN_SERVER_DISCOVEY;


	public String toString() {
		return name();
	}
}
