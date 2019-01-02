package CountersComparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.mobius.software.coap.testsuite.common.model.Counters;
import com.mobius.software.coap.testsuite.common.model.MessageType;

public class CountersComparator implements Comparator<Counters>
{
	private static final Map<MessageType, Integer> counterWeight = new HashMap<>();
	static
	{
		counterWeight.put(MessageType.SUBSCRIBE, 1);
		counterWeight.put(MessageType.SUBACK, 2);
		counterWeight.put(MessageType.PUBLISH, 3);
		counterWeight.put(MessageType.PUBACK, 4);
		counterWeight.put(MessageType.UNSUBSCRIBE, 5);
		counterWeight.put(MessageType.UNSUBACK, 6);
		counterWeight.put(MessageType.PINGREQ, 7);
		counterWeight.put(MessageType.PINGRESP, 8);
		counterWeight.put(MessageType.INVALID, 9);
	}

	@Override
	public int compare(Counters o1, Counters o2)
	{
		MessageType type2 = o2.getIn().getCommand();
		if (type2 == null)
			return -1;
		MessageType type1 = o1.getIn().getCommand();
		if (type1 == null)
			return 1;
		Integer weight2 = counterWeight.get(type2);
		if (weight2 == null)
			return -1;
		Integer weight1 = counterWeight.get(type1);
		if (weight1 == null)
			return 1;
		return Integer.compare(weight1, weight2);
	}
}
