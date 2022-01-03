package net;
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
	
	public Message(MessageType head, String body) {
		this.head = head.toString();
		this.body = body;
	}

	public Message(MessageType head) {
		this.head = head.toString();
		this.body = new String();
	}

	public String getBodyAt(int index) {
		int i = 0;
		int j = 0;

		if(index < i) {
			while(i < index) {
				while (body.charAt(j++) == ' ')
					break;
			}

			body.substring(j, getEndIndex(body, i));
			return new String();
		}
		else {
			System.out.println("Index too big");
			return null;
		}

	}

	public int getEndIndex(String body, int beginIndex) {
		while (beginIndex < body.length()) {
			if(body.charAt(beginIndex++) == ' ')
				break;
		}
		return beginIndex - 1;
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
		return body;
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

	public void Add (String s) {
		if(body.length() == 0)
			body += s;
		else
			body += " " + s;
	}
	
	public void Add (int n) {
		if(body.length() == 0)
			body += n;
		else
			body += " " + n;
	}

	@Override
	public String toString() {
		return head + " " + body;
	}
}