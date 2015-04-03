package cbcast;

import java.util.ArrayList;
import java.util.Comparator;

import network.AbstractMessage;

public class TimestampVectorComparator implements Comparator<AbstractMessage> {

	@Override
	public int compare(AbstractMessage arg0, AbstractMessage arg1) {
		CBCASTMessage msg0 = (CBCASTMessage) arg0;
		CBCASTMessage msg1 = (CBCASTMessage) arg1;
		for (int i = 0; i < msg0.timestamps.size(); i++) {
			if (msg0.timestamps.get(i) >= msg1.timestamps.get(i)) {
				return 1;
			}
		}
		
		return -1;
	}

}
