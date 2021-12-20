package src;
public class Message {
	private String head;
	private String body;

	Message() {
		head = new String();
		body = new String();
	}

	Message(String head, String body) {
		this.head = head;
		this.body = body;
	}

	Message(MessageType head, String body) {
		this.head = head.toString();
		this.body = body;
	}

	public static Message getHeadAndBody(String s) {
		int i = 0;
		while(i < s.length() - 1) {
			if(s.charAt(i++) == ' ')
			break;
		}
		
		String head = s.substring(0, i - 1);
		String body = s.substring(i, s.length());
		return new Message(head, body);
	}
	
	public String getHead(String s) {
		int i = 0;
		while(i < s.length() - 1) {
			if(s.charAt(i++) == ' ')
				break;
		}

		head = s.substring(0, i - 1);
		return head;
	}

	public String getBody(String s) {
		int i = 0;
		while(i < s.length() - 1) {
			if(s.charAt(i++) == ' ')
				break;
		}

		body = s.substring(i, s.length());
		return head;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return head + " " + body;
	}
}