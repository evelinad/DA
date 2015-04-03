package abcast;

import java.util.ArrayList;
import java.util.Comparator;

import network.AbstractMessage;

public class TimestampVectorComparator implements Comparator<QueueEntry> {

	@Override
	public int compare(QueueEntry arg0, QueueEntry arg1) {
		for (int i = 0; i < arg0.message.timestamps.size(); i++) {
			if (arg0.message.timestamps.get(i) >= arg1.message.timestamps.get(i)) {
				return 1;
			}
		}
		
		return -1;
	}

}
