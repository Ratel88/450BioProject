package magictool.image;

import javax.swing.JOptionPane;

public class AllGeneData {
	   /**sums of all the different image data in the order: red FG, red BG, green FG, green BG, first totals, then averages*/
	   protected long[] dataSums;
	   /**sums of the squares of the data*/
	   protected long[] dataSumSquares;
	   /**averages of all the different image data in the order: red FG, red BG, green FG, green BG, first totals, then averages*/
	   protected double[] dataAverages;
	   /**standard deviations of all the different image data in the order: red FG, red BG, green FG, green BG, first totals then averages*/
	   protected double[] dataStDevs;
	   
	   /**ratios for the current method*/
	   protected double[] ratios;
	   
	   /**names of the genes*/
	   protected String[] names;
	   
	   /**red foreground totals*/
	   protected int redFGTotal[];
	   /**red background totals*/
	   protected int redBGTotal[];
	   /**green foreground totals*/
	   protected int greenFGTotal[];
	   /**green background totals*/
	   protected int greenBGTotal[];
	   /**red foreground averages*/
	   protected double redFGAvg[];
	   /**red background averages*/
	   protected double redBGAvg[];
	   /**green foreground averages*/
	   protected double greenFGAvg[];
	   /**green background averages*/
	   protected double greenBGAvg[];
	   /**flag status*/
	   protected boolean flagStatus[]; 
	   
	   /**flag manager*/
	   protected FlagManager flagman;
	   /**automatic flag manager*/
	   protected FlagManager autoflagman;
	   /**grid manager*/
	   protected GridManager manager;
	   /**total number of spots*/
	   protected int nSpots;
	   /**options from the AutoFlaggingOptionsDialog*/
	   protected AutoFlaggingOptionsDialog afod;
	   /**segmentation method*/
	   protected int segmentMethod;
	   /**ratio method*/
	   protected int ratioMethod;
	   /**parameters for segmentation*/
	   protected Object[] params;
	   /**total number of spots automatically flagged*/
	   protected int nSpotsFlagged;
	   /**parent frame*/
	   protected SegmentFrame parent;
	   /**whether or not this AllGeneData is valid*/
	   boolean valid;
	   protected SegmentDisplay idRed;
	   protected SegmentDisplay idGreen;
	     
	   public AllGeneData(GridManager m, FlagManager fgm, FlagManager afgm, SegmentDisplay idR, SegmentDisplay idG, int mthd, int ratioMthd, Object[] par, AutoFlaggingOptionsDialog a, SegmentFrame p) {
		   //set up class variables
		   this.manager = m;
		   this.flagman = fgm;
		   this.autoflagman = afgm;
		   this.idRed = idR;
		   this.idGreen = idG;
		   this.afod = a;
		   this.segmentMethod = mthd;
		   this.ratioMethod = ratioMthd;
		   this.params = par;
		   this.parent = p;
		   
		   nSpots = 0;
		   nSpotsFlagged = 0;
		   valid = false;
		   //find total number of spots
		   for (int i = 0; i < manager.getNumGrids(); i++) {
			   Grid g = manager.getGrid(i);
			   nSpots += g.getNumOfSpots();
		   }
		   
		   dataSums = new long[8];
		   dataSumSquares = new long[8];
		   dataAverages = new double[8];
		   dataStDevs = new double[8];
		   for (int i = 0; i < 8; i++){
			   dataSums[i] = 0L;
			   dataSumSquares[i] = 0L;
			   dataAverages[i] = 0.0;
			   dataStDevs[i] = 0.0;
		   }
		   
		   ratios = new double[nSpots];
		   names = new String[nSpots];
		   redFGTotal = new int[nSpots];
		   redBGTotal = new int[nSpots];
		   greenFGTotal = new int[nSpots];
		   greenBGTotal = new int[nSpots];
		   redFGAvg = new double[nSpots];
		   redBGAvg = new double[nSpots];
		   greenFGAvg = new double[nSpots];
		   greenBGAvg = new double[nSpots];
		   flagStatus = new boolean[nSpots];
		   for (int i = 0; i < nSpots; i++) {
			   ratios[i] = 0.0;
			   redFGTotal[i] = 0;
			   redBGTotal[i] = 0;
			   greenFGTotal[i] = 0;
			   greenBGTotal[i] = 0;
			   redFGAvg[i] = 0.0;
			   redBGAvg[i] = 0.0;
			   greenFGAvg[i] = 0.0;
			   greenBGAvg[i] = 0.0;
			   flagStatus[i] = false;
		   }
	   }
	   
	   public void calculate() {
		   GeneData gd = null;
		   SingleGeneImage currentGene;
		   int[] autoThresh;
		   if (afod.getOK()) autoThresh = afod.getThresholds();
		   else {
			   autoThresh = new int[4];
			   autoThresh[0] = -1;
			   autoThresh[1] = Integer.MAX_VALUE;
			   autoThresh[2] = -1;
			   autoThresh[3] = Integer.MAX_VALUE;
		   }
		   //clear all afgm flags
		   autoflagman.clearAllFlags();
		   int currSpot = 0;
		   //standard deviation algorithm is Algorithm III from http://en.wikipedia.org/w/index.php?title=Algorithms_for_calculating_variance&oldid=134051606
		   
		   double [] mean = new double[8];
		   double [] S = new double[8];
		   for (int i = 0; i < 8; i++) {
			   mean[i] = 0.0;
			   S[i] = 0.0;
		   }
		   for (int i = 0; i < manager.getNumGrids(); i++) {
			   Grid g = manager.getGrid(i);
			   idRed.setSpots(i);
			   int aspot;
			   for (int j = 0; j<g.getNumOfSpots(); j++) {
				   aspot = manager.getActualSpotNum(i, j);
				   currentGene = new SingleGeneImage(idRed.getCellPixels(i, aspot), idGreen.getCellPixels(i, aspot), idRed.getCellHeight(i, aspot), idRed.getCellWidth(i, aspot));
				   gd = currentGene.getData(segmentMethod, params);
				   if (gd!=null) {
					   dataSums[0] += gd.getRedForegroundTotal();
					   dataSumSquares[0] += gd.getRedForegroundTotal()*gd.getRedForegroundTotal();
					   dataSums[1] += gd.getRedBackgroundTotal();
					   dataSumSquares[1] += gd.getRedBackgroundTotal()*gd.getRedBackgroundTotal();
					   dataSums[2] += gd.getGreenForegroundTotal();
					   dataSumSquares[2] += gd.getGreenForegroundTotal()*gd.getGreenBackgroundTotal();
					   dataSums[3] += gd.getGreenBackgroundTotal();
					   dataSumSquares[3] += gd.getGreenBackgroundTotal()*gd.getGreenBackgroundTotal();
					   dataSums[4] += gd.getRedForegroundAvg();
					   dataSumSquares[4] += gd.getRedForegroundAvg()*gd.getRedForegroundAvg();
					   dataSums[5] += gd.getRedBackgroundAvg();
					   dataSumSquares[5] += gd.getRedBackgroundAvg()*gd.getRedBackgroundAvg();
					   dataSums[6] += gd.getGreenForegroundAvg();
					   dataSumSquares[6] += gd.getGreenForegroundAvg()*gd.getGreenForegroundAvg();
					   dataSums[7] += gd.getGreenBackgroundAvg();
					   dataSumSquares[7] += gd.getGreenBackgroundAvg()*gd.getGreenBackgroundAvg();
					   ratios[currSpot] = gd.getRatio(ratioMethod);
					   names[currSpot] = manager.getGeneName(i, aspot);
					   redFGTotal[currSpot] = gd.getRedForegroundTotal();
					   redBGTotal[currSpot] = gd.getRedBackgroundTotal();
					   greenFGTotal[currSpot] = gd.getGreenForegroundTotal();
					   greenBGTotal[currSpot] = gd.getGreenBackgroundTotal();
					   redFGAvg[currSpot] = gd.getRedForegroundAvg();
					   redBGAvg[currSpot] = gd.getRedBackgroundAvg();
					   greenFGAvg[currSpot] = gd.getGreenForegroundAvg();
					   greenBGAvg[currSpot] = gd.getGreenBackgroundAvg();
					   if ((gd.getRedForegroundTotal() < autoThresh[0]) || (gd.getRedBackgroundTotal() > autoThresh[1]) || (gd.getGreenForegroundTotal() < autoThresh[2]) || (gd.getGreenBackgroundTotal() > autoThresh[3])) {
						   //this spot fails threshold for autoflagging - it will be autoflagged
						   autoflagman.flagSpot(i, j);
						   nSpotsFlagged++;
					   }
					   //calculate stuff for stdev
					   int n = currSpot+1;
					   double delta;
					   double x;
					   //first, red FG Total
					   x = (double)redFGTotal[currSpot];
					   delta = x - mean[0];
					   mean[0] = mean[0] + delta/n;
					   S[0] = S[0] + delta*(x - mean[0]);	//this expression uses the new value of mean
					   //second, red BG Total;
					   x = (double)redBGTotal[currSpot];
					   delta = x - mean[1];
					   mean[1] = mean[1] + delta/n;
					   S[1] = S[1] + delta*(x - mean[1]);
					   //third, green FG Total
					   x = (double)greenFGTotal[currSpot];
					   delta = x - mean[2];
					   mean[2] = mean[2] + delta/n;
					   S[2] = S[2] + delta*(x-mean[2]);
					   //fourth, green BG Total
					   x = (double)greenBGTotal[currSpot];
					   delta = x - mean[3];
					   mean[3] = mean[3] + delta/n;
					   S[3] = S[3] + delta*(x-mean[3]);
					   //fifth, red FG Avg
					   x = redFGAvg[currSpot];
					   delta = x - mean[4];
					   mean[4] = mean[4] + delta/n;
					   S[4] = S[4] + delta*(x-mean[4]);
					   //sixth, red BG Avg
					   x = redBGAvg[currSpot];
					   delta = x-mean[5];
					   mean[5] = mean[5] + delta/n;
					   S[5] = S[5] + delta*(x-mean[5]);
					   //seventh, green FG Avg
					   x = greenFGAvg[currSpot];
					   delta = x - mean[6];
					   mean[6] = mean[6] + delta/n;
					   S[6] = S[6] + delta*(x-mean[7]);
					   //eighth, green BG Avg
					   x = greenBGAvg[currSpot];
					   delta = x - mean[7];
					   mean[7] = mean[7] + delta/n;
					   S[7] = S[7] + delta*(x - mean[7]);
				   }
				   else {
					   names[currSpot] = "empty";	//marker for "no gene here!"
				   }
				   flagStatus[currSpot] = autoflagman.checkFlag(i, j) || flagman.checkFlag(i, j);
				   currSpot++;
			   }
		   }
		   //everything has now been found
		   double [] variance = new double[8];
		   try{
			   for (int i = 0; i < 8; i++) {
				   dataAverages[i] = ((double)dataSums[i])/((double)nSpots);
				   variance[i] = S[i] / (nSpots-1);
				   dataStDevs[i] = Math.sqrt(variance[i]); 
			   }
		   } catch(ArithmeticException exc) {
			   JOptionPane.showMessageDialog(parent, "Divide by zero error! Please provide a grid with more than one spot.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
			   for(int i = 0; i < 8; i++) dataAverages[i] = 0.0;
			   return;
		   }
		   valid = true;
	   }
	   
	   /**
	    * returns sums of all the different image data in the order: red FG, red BG, green FG, green BG
	    * @return sums of all the different image data in the order: red FG, red BG, green FG, green BG
	    */
	   public long[] getSums() {
		   return dataSums;
	   }
	   
	   /**
	    * returns averages of all the different image data in the order: red FG, red BG, green FG, green BG
	    * @return averages of all the different image data in the order: red FG, red BG, green FG, green BG
	    */
	   public double[] getAverages() {
		   return dataAverages;
	   }
	   
	   /**
	    * returns standard deviations of all the different image data in the order: red FG, red BG, green FG, green BG
	    * @return standard deviations of all the different image data in the order: red FG, red BG, green FG, green BG
	    */
	   public double[] getStDevs() {
		   return dataStDevs;
	   }
	   
	   /**
	    * returns the array of all calculated ratios
	    * @return the array of all calculated ratios
	    */
	   public double[] getRatios() {
		   return ratios;
	   }
	   
	   /**
	    * returns the specified ratio number, starting from index 0
	    * @param i index ratio number (0 &lt;= i &lt; total number of spots)
	    * @return ratio
	    */
	   public double getRatio(int i) {
		   return ratios[i];
	   }
	   
	   public String[] getGeneNames() { return names; }
	   public int[] getRedFGTotals() { return redFGTotal; }
	   public int[] getRedBGTotals() { return redBGTotal; }
	   public int[] getGreenFGTotals() { return greenFGTotal; }
	   public int[] getGreenBGTotals() { return greenBGTotal; }
	   public double[] getRedFGAvgs() { return redFGAvg; }
	   public double[] getRedBGAvgs() { return redBGAvg; }
	   public double[] getGreenFGAvgs() { return greenFGAvg; }
	   public double[] getGreenBGAvgs() { return greenBGAvg; }
	   public boolean[] getFlagStatuses() { return flagStatus; }

	   public String getGeneName(int i) { return names[i]; }
	   public int getRedFGTotal(int i) { return redFGTotal[i]; }
	   public int getRedBGTotal(int i) { return redBGTotal[i]; }
	   public int getGreenFGTotal(int i) { return greenFGTotal[i]; }
	   public int getGreenBGTotal(int i) { return greenBGTotal[i]; }
	   public double getRedFGAvg(int i) { return redFGAvg[i]; }
	   public double getRedBGAvg(int i) { return redBGAvg[i]; }
	   public double getGreenFGAvg(int i) { return greenFGAvg[i]; }
	   public double getGreenBGAvg(int i) { return greenBGAvg[i]; }
	   public boolean getFlagStatus(int i) { return flagStatus[i]; }
	   
	   public void invalidate() {
		   valid = false;
	   }
}