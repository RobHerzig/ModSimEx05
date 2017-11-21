package study;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import jdk.nashorn.internal.runtime.regexp.joni.MatcherFactory;
import simulation.lib.Simulator;
import simulation.lib.counter.ContinuousCounter;
import simulation.lib.counter.Counter;
import simulation.lib.counter.DiscreteAutocorrelationCounter;
import simulation.lib.counter.DiscreteConfidenceCounter;
import simulation.lib.counter.DiscreteConfidenceCounterWithRelativeError;
import simulation.lib.counter.DiscreteCounter;
import simulation.lib.histogram.ContinuousHistogram;
import simulation.lib.histogram.DiscreteHistogram;
import simulation.lib.randVars.RandVar;
import simulation.lib.randVars.continous.ErlangK;
import simulation.lib.randVars.continous.Exponential;
import simulation.lib.randVars.continous.HyperExponential;
import simulation.lib.rng.StdRNG;
import simulation.lib.statistic.IStatisticObject;

/**
 * Represents a simulation study. Contains diverse counters for statistics and
 * program/simulator parameters. Starts the simulation.
 */
public class SimulationStudy {

	 // e.g. protected cNInit = ...
	 //protected cCvar = ... <- configuration Parameter for cVar[IAT]
	public int cNInit = 1000; //nInit is already given to another variable TODO: Maybe change name for better readability
	public int lBatch = 1;
	public double cCVar = 1d;
	double relativeErrorThreshold = 0.05;
	double absoluteErrorThreshold = 0.0001;
	double cSystemUtilization = 0.95;
	

	/**
	 * Main method
	 * 
	 * @param args
	 *            - none
	 */
	public static void main(String[] args) {
		/*
		 * create simulation object
		 */
		Simulator sim = new Simulator();
		/*
		 * run simulation
		 */
		sim.start();
		/*
		 * print out report
		 */
		sim.report();
	}

	// PARAMETERS
	/**
	 * Turn on/off debug report in console.
	 */
	protected boolean isDebugReport = true;

	/**
	 * Turn on/off report in csv-files.
	 */
	protected boolean isCsvReport = true;

	/**
	 * inter arrival time of customers (in simulation time).
	 */
	public long interArrivalTime;

	/**
	 * service time of a customer (in simulation time).
	 */
	public long serviceTime;

	/**
	 * Number of customers for initialization.
	 */
	public long nInit;

	/**
	 * Length of batches.
	 */
	public long batchLength;

	/**
	 * Coefficient of variation.
	 */
	public double cVar;

	/**
	 * Random number generator for inter arrival times.
	 */
	public RandVar randVarInterArrivalTime;

	/**
	 * random number generator for service times
	 */
	public RandVar randVarServiceTime;

	// STATISTICS
	/**
	 * Map that contains all statistical relevant object such as counters and
	 * histograms.
	 */
	public HashMap<String, IStatisticObject> statisticObjects;

	/**
	 * Maximum queue size.
	 */
	public long maxQS;

	/**
	 * Minimum queue size.
	 */
	public long minQS;

	/**
	 * Number of batches in simulation.
	 */
	public long numBatches;

	/*
	 * TODO Finished? Problem 5.1 - naming your statistic objects
	 * Here you have to set some names (as Sting objects) for all your statistic objects
	 * They are later used to retrieve them from the dictionary
	 */
	// Strings used for receiving statisticobjects later in the dictionary.
	public String dtcWaitingTime = "discreteTimeCounterWaitingTime";
	public String dthWaitingTime = "discreteTimeHistogramWaitingTime";
	public String dtcServiceTime = "discreteTimeCounterServiceTime";
	public String dthServiceTime = "discreteTimeHistogramServiceTime";
	public String ctcQueueOccupancy = "continuousTimeCounterQueueOccupancy";
	public String cthQueueOccupancy = "continuousTimeHistogramQueueOccupancy";
	public String ctcServerUtilization = "continuousTimeCounterServerUtilization";
	public String cthServerUtilization = "continuousTimeHistogramServerUtilization";
	public String dtcBatchWaitingTime = "discreteTimeCounterBatchWaitingTime";
	public String tempdtcBatchWaitingTime = "temporaryDiscreteTimeCounterBatchWaitingTime";
	public String dtcBatchServiceTime = "discreteTimeCounterBatchServiceTime";
	public String tempdtcBatchServiceTime = "temporaryDiscreteTimeCounterBatchServiceTime";
	public String ccreBatchWaitingTime = "confidenceCounterWithRelativeErrorBatchWaitingTime";
	public String ccreWaitingTime = "confidenceCounterWithRelativeErrorWaitingTime";
	
	//Extra names
	public String discrCounterWithRelError = "discreteConfidenceCounterWithRelativeError";
	public String confCounterForIndivWaitingTimeSamples = "confidenceCounterIndivWaitingSamples";
	public String confCounterBatchMeans = "confidenceCounterBatchMeans";
	public String counterWaitingBatchMeans = "counterMeanWaitingBatchMeans";
	
	public String discreteConfidenceCounterBatches = "discreteConfidenceCounterBatches";
	public String relativeErrorCounterBatch = "relativeErrorCounterBatchMeans";
	public String relativeErrorCounterWOBatch = "relativeErrorCounterWithoutBatch";
	public String autoCorrelationCounter = "autoCorrelationCounter";
	public String probabilityExcess = "probabilityExcess";
	public String naiveWaitingCounter = "NaiveWaitingCounter";
	

	public long numWaitingTimeExceeds5TimesServiceTime;
	public long numBatchWaitingTimeExceeds5TimesBatchServiceTime;
	public long numWaitingTimeExceeds0;
	public String dtacBatchWaitingTime = "discreteTimeAutocorrelationCounterBatchWaitingTime";

	private Simulator simulator;

	/**
	 * Constructor
	 * @param sim Simulator instance.
	 */
	public SimulationStudy(Simulator sim) {
		simulator = sim;
		simulator.setSimTimeInRealTime(1000);
		setSimulationParameters();
		initStatistics();
	}

	/**
	 * Sets simulation parameters, converts real time to simulation time if
	 * needed.
	 */
	private void setSimulationParameters() {

		/*
		 * TODO Finished? Problem 5.1.1 - Set simulation parameters
		 * Hint: Take a look at the attributes of this class which have no usages yet (This may be indicated by your IDE)
		 */
		// this.nInit = cNInit;
		// this.cVar = ...
		//QUOTE "!!! Make sure to use StdRNG objects with different seeds !!!"
		StdRNG rng1 = new StdRNG(123789456);
		StdRNG rng2 = new StdRNG(456789456);
		StdRNG rng3 = new StdRNG(789789456);
		StdRNG rngService = new StdRNG(System.currentTimeMillis());
		
		this.nInit = cNInit;
		this.cVar = cCVar;
		this.batchLength = lBatch;
		
		//granularity of 0.05 -> 1/20
		double EST = 1d; //fixed value from exercise
		double EIAT = Math.round(Math.min(Math.max(cSystemUtilization, 0.05), 0.95) * 20d) * 0.05; //only allows steps of 1/20th (0.05), cannot be 0
		double rho = EST / EIAT; //from 5.1.3 - systemutilization
		
		if(/*this.cVar >= 0.5 && */this.cVar < .95) {
			this.randVarInterArrivalTime = new ErlangK(rng1, rho, 4);
		}else if(this.cVar < 1.95) {
			this.randVarInterArrivalTime = new Exponential(rng2, rho);
		} else {
			this.randVarInterArrivalTime = new HyperExponential(rng3, rho, 2);
		}

		randVarServiceTime = new Exponential(rngService, 1);
	}

	/**
	 * Initializes statistic objects. Adds them into Hashmap.
	 */
	private void initStatistics() {
		maxQS = Long.MIN_VALUE;
		minQS = Long.MAX_VALUE;

		// Init numBatches
		numBatches = 0;

		statisticObjects = new HashMap<>();
		statisticObjects.put(dtcWaitingTime, new DiscreteCounter("waiting time/customer"));
		statisticObjects.put(dthWaitingTime, new DiscreteHistogram("waiting_time_per_customer", 80, 0, 80));

		statisticObjects.put(dtcServiceTime, new DiscreteCounter("service time/customer"));
		statisticObjects.put(dthServiceTime, new DiscreteHistogram("service_time_per_customer", 80, 0, 80));

		statisticObjects.put(ctcQueueOccupancy, new ContinuousCounter("queue occupancy/time", simulator));
		statisticObjects.put(cthQueueOccupancy,
				new ContinuousHistogram("queue_occupancy_over_time", 80, 0, 80, simulator));

		statisticObjects.put(ctcServerUtilization, new ContinuousCounter("server utilization/time", simulator));
		statisticObjects.put(cthServerUtilization,
				new ContinuousHistogram("server_utilization_over_time", 80, 0, 80, simulator));

		
		statisticObjects.put(discrCounterWithRelError, new DiscreteConfidenceCounterWithRelativeError("Mean of Batches below rel. error bound", relativeErrorThreshold));
		statisticObjects.put(discreteConfidenceCounterBatches, new DiscreteConfidenceCounter("batches", relativeErrorThreshold)); 
		statisticObjects.put(relativeErrorCounterBatch, new DiscreteConfidenceCounterWithRelativeError("mean of batches relError less 5%", relativeErrorThreshold)); 
		statisticObjects.put(relativeErrorCounterWOBatch, new DiscreteConfidenceCounterWithRelativeError("mean of Samples relError less 5%", relativeErrorThreshold)); 
		statisticObjects.put(autoCorrelationCounter, new DiscreteAutocorrelationCounter("mean of batches autocorrelated?", (int) (relativeErrorThreshold * 200))); 
		statisticObjects.put(probabilityExcess, new DiscreteCounter("Probability Excess Waiting time"));
		statisticObjects.put(naiveWaitingCounter, new DiscreteCounter("Naive Waiting Counter"));
		
		//statisticObjects.put("WaitingTimeCounter5.1.4", new TimeWeightingCounter("WaitingTimeCounter5.1.4", simulator));
		//statisticObjects.put("TimeWeightingServerUtilisation", new TimeWeightingCounter("TimeWeightingServerUtilisation", simulator));

		
		/*
		 * TODO Problem 5.1.4 - Create counter to calculate the mean waiting time with batch means method
		 */
		/*
		 * TODO Problem 5.1.4 - Provide means to keep track of E[WT] > 5 * E[ST]
		 * !!! This is also called "waiting probability" in the sheet !!!
		 */
		/*
		 * TODO Problem 5.1.4 - Create confidence counter for individual waiting time samples
		 */
		/*
		 * TODO Problem 5.1.4 - Create confidence counter for to count waiting times with batch means method
		 */
		/*
		 * TODO Problem 5.1.5 - Create a DiscreteAutocorrelationCounter for batch means
		 */

	}


	/**
	 * Report results. Print to console if isDebugReport = true. Print to csv
	 * files if isCsvReport = true. Note: Histogramms are only printed to csv
	 * files.
	 */
	public void report() {
		String sd = new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date(System.currentTimeMillis()));
		String destination = sd + this.getClass().getSimpleName();

		if (isCsvReport) {
			File file = new File(destination);
			file.mkdir();
			for (IStatisticObject so : statisticObjects.values()) {
				so.csvReport(destination);
			}
		}
		if (isDebugReport) {
			/*
			 * TODO Finished? Problem 5.1 - Output reporting information!
			 * Print your statistic objects which are needed to answer the questions in the exercise sheet
			 */
			for (IStatisticObject statisticObjectInstance : statisticObjects.values()) {
				System.out.println(statisticObjectInstance.report());
			}

		}

	}
}
