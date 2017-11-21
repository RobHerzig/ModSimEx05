package simulation.lib.counter;

import simulation.lib.Simulator;

public class WaitingTimeCounter extends Counter{

	private long lastSampleTime;
	private long firstSampleTime;
	private double lastSampleSize;
	private Simulator sim;
	
	/**
	 * Basic constructor
	 * @param variable the variable to observe
	 * @param sim the considered simulator
	 */	
	public WaitingTimeCounter (String variable, Simulator sim) {
		super(variable, "waiting time counter");
		this.sim = sim;
	}
	
	/**
	 * @see Counter#getMean()
	 */
	@Override
	public double getMean() {
		long interval = lastSampleTime - firstSampleTime;
		return interval > 0 ? getSumPowerOne()/(double) interval : 0;
	}
	
	/**
	 * @see Counter#getVariance()
	 */
	@Override
	public double getVariance() {
		long interval = lastSampleTime - firstSampleTime;
		return interval > 0 ? getSumPowerTwo() / (double) interval - getMean()*getMean() : 0;
	}

	/**
	 * @see Counter#count(double)
	 */
	@Override
	public void count(double x) {
		super.count(x);
		long interval = sim.getSimTime() - lastSampleTime;
		increaseSumPowerOne(lastSampleSize*interval);
		increaseSumPowerTwo(lastSampleSize*lastSampleSize*interval);
		lastSampleSize = x;
		lastSampleTime = sim.getSimTime();
	}

	/**
	 * @see Counter#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		firstSampleTime = sim.getSimTime();
		lastSampleTime = sim.getSimTime();
		lastSampleSize = 0;
	}
	
}
