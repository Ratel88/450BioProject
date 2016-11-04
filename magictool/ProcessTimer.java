/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2005  Laurie Heyer
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *   Contact Information:
 *   Laurie Heyer
 *   Dept. of Mathematics
 *   PO Box 6959
 */

package magictool;

/**
 * @author Mackenzie Cowell
 * 
 * This class can be used to determine the amount of time between two timepoints,
 * useful for tracking the speed of long calculations.  A ProcessTimer object sets
 * its initial time point to the moment of its construction, and its ending timepoint
 * to the moment finish() is called on it.  Its constructor may optionally be passed
 * a string description of the process or interval being measured, which will later be
 * reported out at the end of the measurement.
 */

/* When a ProcessTimer object is constructed, it immediately stores the current time in 
 * a Date.  When finish() is called on the ProcessTimer, the current time is placed into 
 * a second Date, and the difference in milliseconds between the two of them is calculated
 * and used to construct a third Date, which should be exactly the length of the measured
 * interval later than the Epoch.  This third date is formatted and then printed to the
 * terminal.
 */

public class ProcessTimer {
	private long start, finish, elapsed;
	private int DAY=0, HOUR=0, MIN=0, SEC=0, MSEC=0;		//the variables that will hold the parsed time
	private String interval = "";
	private String process = null;
	private ProgressFrame progBar = null;
	

	/**
	 * Construct a new ProcessTimer with the moment of construction marking the beginning of the
	 * time interval to be measured.
	 */
	public ProcessTimer(){
		new ProcessTimer("");
	}
	
	/**
	 * Construct a new ProcessTimer with the moment of construction marking the beginning of the
	 * time interval to be measured.
	 * @param process a string describing the process or interval measured; e.g. "writeClustToFile-1"
	 */
	public ProcessTimer( String process ){
		start = System.currentTimeMillis();
		this.process = process;
	}
	
	/**
	 * Sets the end timepoint to the current moment, calculates the length of the interval, then prints it
	 * to the terminal.  It also prints the descriptive string, if supplied during construction.
	 */
	public void finish(){
		getTime();
		if ( process != null ) {
			System.out.println( "Elapsed time for process " + process + ": " + interval );
		}else{
			System.out.println( "Elapsed time: " + interval );
		}
	}
		
	/**
	 * Returns a string representing the length of time since the timer began, in the format
	 * "days:hours:minutes:seconds:milliseconds", unless the interval hasn't been calculated
	 * (perhaps for lack of end timepoint?), in which case it returns null.
	 * @return
	 */	
	public String toString(){
		getTime();
		return interval;
	}
	
	/**
	 * returns the number of milliseconds since the timer was started (i.e. initialized)
	 * @return number of milliseconds since the timer started (long).
	 */
	public long elapsedMilliseconds(){
		getTime();
		return elapsed;
	}
	
	/**
	 * ETA is useful for extrapolating the amount of time a process will take.  An example
	 * of its use might be to initialize a new ProcessTimer object, start the process to be
	 * timed, then after a/b percent of the process has completed, then call ETA( (int)b/a ).
	 * @param scale the current time sample multiplied by this integer = extrapolated total time
	 * @return String in the form "days:hours:min:sec:ms" estimated until completion
	 */
	public String ETA( int scale ){
		getTime();
		elapsed = elapsed*scale;
		
		if ( elapsed >= 24*60*60*1000 ){
			DAY = (int) (elapsed / (24*60*60*1000));
			elapsed = elapsed - DAY*(24*60*60*1000);
		}
		if ( elapsed >= 60*60*1000 ){
			HOUR = (int) (elapsed / (60*60*1000));
			elapsed = elapsed - HOUR*(60*60*1000);
		}
		if ( elapsed >= 60*1000 ){
			MIN = (int) (elapsed / (60*1000));
			elapsed = elapsed - MIN*(60*1000);
		}
		if ( elapsed >= 1000 ){
			SEC = (int) (elapsed / 1000);
			elapsed = elapsed - SEC*1000;
		}
		return DAY+"d:" + HOUR+"h:" + MIN+"m:" + SEC+"s:" + elapsed+"ms";
	}
	
	/**
	 * Resets the initial timepoint, the final timepoint, and the length of the interval all to the
	 * current moment.  The descriptive string is left unchanged.
	 */
	public void reset(){
		start = finish = elapsed = System.currentTimeMillis();
	}
	
	private void getTime(){
 		finish = System.currentTimeMillis();
		elapsed = finish - start;
		
		if ( elapsed >= 24*60*60*1000 ){
			DAY = (int) (elapsed / (24*60*60*1000));
			elapsed = elapsed - DAY*(24*60*60*1000);
		}
		if ( elapsed >= 60*60*1000 ){
			HOUR = (int) (elapsed / (60*60*1000));
			elapsed = elapsed - HOUR*(60*60*1000);
		}
		if ( elapsed >= 60*1000 ){
			MIN = (int) (elapsed / (60*1000));
			elapsed = elapsed - MIN*(60*1000);
		}
		if ( elapsed >= 1000 ){
			SEC = (int) (elapsed / 1000);
			elapsed = elapsed - SEC*1000;
		}
		MSEC = (int) elapsed;

		interval = DAY+"d:" + HOUR+"h:" + MIN+"m:" + SEC+"s:" + MSEC+"ms";
	}
}
