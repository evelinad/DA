package threephase;

import java.util.Comparator;

public class TimestampComparator implements Comparator<QueueEntry> {

	@Override
	public int compare(QueueEntry o1, QueueEntry o2) {
		if ((int)(o1.timestamp - o2.timestamp) == 0) {
			return o1.message.tag.compareTo(o2.message.tag);
		}
		
		return (int)(o1.timestamp - o2.timestamp);
	}

}
