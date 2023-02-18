package net.krlite.knowledges.util;

import net.krlite.equator.util.Timer;

public class FlaggedTimer extends Timer {
	private boolean flag;

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean isFlagged() {
		return flag;
	}

	/**
	 * Creates a new timer with the origin time set to
	 * the current system time, in milliseconds.
	 *
	 * @param lasting The lasting time, in milliseconds.
	 *                Will take the absolute value.
	 */
	public FlaggedTimer(long lasting) {
		super(lasting);
		this.flag = false;
	}

	public void flag() {
		if (!isFlagged()) reset();
		setFlag(true);
	}

	public void unFlag() {
		setFlag(false);
	}

	@Override
	public long queueElapsed(boolean countStepping) {
		return flag ? super.queueElapsed(countStepping) : 0;
	}

	@Override
	public boolean isPresent() {
		return flag && super.isPresent();
	}

	@Override
	public boolean isFinished() {
		return flag && super.isFinished();
	}
}
