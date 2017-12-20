package com.workmarket.search.queue;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class LongDelay implements Delayed {

	protected LongDelay(Long value, long delayTimeMillis) {
		super();
		this.value = value;
		this.delayTime = delayTimeMillis + System.currentTimeMillis();
	}

	private Long value;
	private final long delayTime;

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public int compareTo(Delayed arg0) {
		LongDelay other = (LongDelay) arg0;
		return (int) (this.value - other.getValue());
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(delayTime - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LongDelay other = (LongDelay) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LongDelay [value=").append(value)
				.append(", delayTime=").append(delayTime).append("]");
		return builder.toString();
	}

}
