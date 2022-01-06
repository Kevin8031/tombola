package net;

import java.util.Deque;
import java.util.LinkedList;

public class Queue<T> {
	private Deque<Message<T>> queue;

	public Queue() {
		queue = new LinkedList<Message<T>>();
	}

	public void pushFront(Message<T> e) {
		queue.addFirst(e);
	}

	public void pushBack(Message<T> e) {
		queue.addLast(e);
	}

	public int count() {
		int size = queue.size();
		return size;
	}

	public Message<T> popFront() {
		return queue.pop();
	}

	public Message<T> popBack() {
		Message<T> e = queue.getLast();
		queue.removeLast();
		return e;
	}

	public Message<T> front() {
		return queue.getFirst();
	}

	public Message<T> last() {
		return queue.getLast();
	}

	public void clear() {
		queue.clear();
	}
}
