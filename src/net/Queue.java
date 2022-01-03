package net;

import java.util.Deque;
import java.util.LinkedList;

public class Queue {
	private Deque<Object> queue;

	Queue() {
		queue = new LinkedList<Object>();
	}

	public void pushFront(Object e) {
		queue.addFirst(e);
	}

	public void pushBack(Object e) {
		queue.addLast(e);
	}

	public int count() {
		return queue.size();
	}

	public Object popFront() {
		return queue.pop();
	}

	public Object popBack() {
		Object e = queue.getLast();
		queue.removeLast();
		return e;
	}

	public Object front() {
		return queue.getFirst();
	}

	public Object last() {
		return queue.getLast();
	}

	public void clear() {
		queue.clear();
	}
}
