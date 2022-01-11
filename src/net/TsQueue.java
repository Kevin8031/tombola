package net;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TsQueue<T> {
	private Deque<OwnedMessage<T>> queue;
	private Lock lock;
	private Thread thread;

	public TsQueue() {
		queue = new LinkedList<OwnedMessage<T>>();
		lock = new ReentrantLock(true);
	}

	public void pushFront(Message<T> msg) {
		lock.lock();
		try {
			queue.addFirst(new OwnedMessage<T>(msg));
		} finally {
			lock.unlock();
		}
	}

	public void pushBack(Message<T> msg) {
		lock.lock();
		try {
			queue.addLast(new OwnedMessage<T>(msg));
		} finally {
			lock.unlock();
			synchronized(thread) {
				thread.notify();
			}
		}
	}

	public void pushFront(OwnedMessage<T> msg) {
		lock.lock();
		try {
			queue.addFirst(msg);
		} finally {
			lock.unlock();
			synchronized(thread) {
				thread.notify();
			}
		}
	}

	public void pushBack(OwnedMessage<T> msg) {
		lock.lock();
		try {
			queue.addLast(msg);
		} finally {
			lock.unlock();
			synchronized(thread) {
				thread.notify();
			}
		}
	}

	public int count() {
		lock.lock();
		try {
			int size = queue.size();
			return size;
		} finally {
			lock.unlock();
		}
	}

	public OwnedMessage<T> popFront() {
		lock.lock();
		try {
			return queue.pop();
		} finally {
			lock.unlock();
		}
	}

	public OwnedMessage<T> popBack() {
		lock.lock();
		try {
			OwnedMessage<T> msg = queue.getLast();
			queue.removeLast();
			return msg;
		} finally {
			lock.unlock();
		}
	}

	public OwnedMessage<T> front() {
		lock.lock();
		try {
			return queue.getFirst();
		} finally {
			lock.unlock();
		}
	}

	public OwnedMessage<T> last() {
		lock.lock();
		try {
			return queue.getLast();
		} finally {
			lock.unlock();
		}
	}

	public void clear() {
		lock.lock();
		try {
			queue.clear();
		} finally {
			lock.unlock();
		}
	}

	public boolean empty() {
		lock.lock();
		try {
			return queue.isEmpty();
		} finally {
			lock.unlock();
		}
	}

	public void Wait() {
		while (empty()) {
			try {
				thread = Thread.currentThread();
				synchronized(thread) {
					thread.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
