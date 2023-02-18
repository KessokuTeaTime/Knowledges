package net.krlite.knowledges.util;

import net.krlite.equator.util.SystemClock;
import net.minecraft.util.math.MathHelper;

public class Ranger {
	private long origin;
	private final long lasting, range;

	public long getLasting() {
		return lasting;
	}

	public Ranger(long lasting, long range) {
		this.origin = SystemClock.queueElapsed();
		this.lasting = lasting;
		this.range = range;
	}

	public boolean isPresent() {
		return SystemClock.queueElapsed() >= origin && SystemClock.queueElapsed() <= origin + lasting;
	}

	public boolean isFinished() {
		return SystemClock.queueElapsed() - origin > lasting;
	}

	public boolean isAscending() {
		return isPresent() && SystemClock.queueElapsed() - origin <= Math.min(range, lasting / 2.0);
	}

	public boolean isDescending() {
		return isPresent() && origin + lasting - SystemClock.queueElapsed() >= Math.min(range, lasting / 2.0);
	}

	public long queueElapsed() {
		return Math.min(SystemClock.queueElapsed() - origin, origin + lasting - SystemClock.queueElapsed());
	}

	public long queue() {
		return MathHelper.clamp(queueElapsed(), 0, range);
	}

	public long queueRaw() {
		return SystemClock.queueElapsed() - origin;
	}

	public double queueAsPercentage() {
		return (double) queue() / range;
	}

	public void reset() {
		this.origin = SystemClock.queueElapsed();
	}
}
