package pardune;

import lejos.nxt.Motor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class ObjectsPardune {

	int prevVal, curVal, nexVal;
	boolean foundPodest;
	DifferentialPilot pilot;
	UltrasonicSensor ultrasonicSensor;
	private int numValidMeasurements;

	private int measured;
	private int[] measurements;
	private int[] differenceBetweenVals;
	private int i;

	public ObjectsPardune(){
		// diff Pilot
		pilot = new DifferentialPilot(43.2f, 161f, Motor.A, Motor.B, false);
		pilot.setAcceleration(40);
		pilot.setTravelSpeed(40);
		pilot.setRotateSpeed(80);

		// bools
		foundPodest = false;

		// run the actual program
		alg();
	}
	
	/**
	 *  es gibt 4 arten, das podest zu finden.
	 *  
	 *  kurvendiskussion:
	 *  suchen von maxima (ecken).
	 *  zwischen diesen gibt es lokale minima. diese stellen podeste,
	 *  roboter, oder eine mauer dar.
	 *  
	 *  
	 *  einteilen in dreiecke
	 *  der roboter teilt das Feld mit seiner Position in vier Dreiecke
	 *  ein, deren hypotenuse immer 2m betr�gt.
	 *  die Eckpunkte der Dreiecke sind Ecken des Feldes und damit absolute Maxima.
	 *  ein Podest hat das geringste minimum zwischen den beiden absoluten Maxima.
	 *  
	 *  
	 *  einteilen in rechtecke
	 *  lokale minima stellen w�nde dar, somit kann das feld in vierecke eingeteilt werden.
	 *  In diesen gibt es ein absolutes maxima (ecke) und evtl ein absolutes minimum(podest)
	 *  
	 *  
	 *  fahren in die mitte
	 *  der roboter f�hrt zur gr��ten distanz, stoppt bei der Mittellinie (diese wird er
	 *  tangieren), richtet sich an der linie, in der mitte aus, scannt seine umgebung um
	 *  180� und sucht ein von den bekannten messwerten abweichenden wert. dieser stellt
	 *  das podest dar. 
	 *  
	 *  
	 *  		hilfe ein hai
	 *                   \o/		/(
	 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	        o		o           O 		8
	      �         ��       ��       �
	 
	 *  
	 */
	

	private void alg() {
		// find podest roughly
		measurements = new int[72];
		findPodestRoughly();

		// validate
		// search for maxima in measurements[]
		maximaInMeasurements();

		// get closer, to safe measurement distance
		// increase precision

		// get can

		// dive to delivery point


	}


	private void findPodestRoughly() {
		int tempMeasured;
		for (int i=0; i < 72; i++) {
			measured = 0;
			numValidMeasurements = 0;
			for(int j = 0; j<5; j++) {
				tempMeasured = ultrasonicSensor.getDistance();
				if (tempMeasured != 255) {
					System.out.println("usable value, " + j);
					measured += tempMeasured;
					numValidMeasurements++;
				}
			}
			// check for faulty data
			if(numValidMeasurements == 0) {
				System.out.println("no usable data found, set 255, " +i);
				measurements[i] = 255;
			} else {
				measurements[i] = (int) (measured/numValidMeasurements);
			}
			pilot.rotate(5);				// 5 degrees right
			while(pilot.isMoving()) Thread.yield();
		}
		System.out.println("measurements-Array filled");
	}


	private void maximaInMeasurements() {
		int[] lokalesMaximum = new int[10];
		int lokalesMaximumIndex = 0;

		// es gibt bei jeder drehung maximal 4 (de facto wahrsch 3) maxima.
		// hinzu kommen evtl. lokale f�r
		// 1. das Podest
		// 2. das andere Podest
		// 3. den anderen roboter

		// Maxima in array speichern
		for (int i=1; i < 71; i++) {
			prevVal = measurements[i-1];
			curVal = measurements[i];
			nexVal = measurements[i+1];

			// maximum: werte steigen an, bis ein kleinerer kommt
			if (curVal > prevVal && curVal > nexVal){
				// an Stelle i ist ein Maximum
				lokalesMaximum[lokalesMaximumIndex] = i;
				lokalesMaximumIndex++;
			}
		}
		// alle maxima in ein array gespeichert
		System.out.println("lokale Maxima gef.:" + lokalesMaximumIndex);
	}
	
	//private void (){
		
		
	//}

	/***
	 * 
	 * 
	 * 
	 * 
	 */

	private void findPodest() {
		measurements = new int[72];
		differenceBetweenVals = new int[71];	// 0 is between 0 and 1 of measurement-Array, 5 between 5 and 6...
		// measure, compare difference
		// if: corner(pos to neg): continue
		// else: retreive dose (pos to neg to pos)

		// fill measurements and diff-array via loop
		int numValidMeasurements = 0;
		for (i=0; i < 72; i++) {
			for(int j = 0; j<5; j++) {
				numValidMeasurements = 0;
				measured = ultrasonicSensor.getDistance();
				if (measured != 255) {
					measurements[i] += measured;
					numValidMeasurements++;

				}
				if(numValidMeasurements == 0) {
					measurements[i] = measured;
				} else {
					measurements[i] = (int) (measured/numValidMeasurements);
				}
				pilot.rotate(5);				// 5 degrees left
				while(pilot.isMoving()) Thread.yield();
			}

			// process diff-Array
			for (int k = 0; k<71; k++){
				differenceBetweenVals[k] = measurements[k] - measurements[k+1];
			}

			// print diff-array
			for (int k = 0; k<72;k++){
				if((k+1)%10 == 0){					// every tenth value, enter new line
					System.out.println();
				}
				System.out.print(differenceBetweenVals[k]+ ",");
			}

			// all measurements done, evaluate differences
			boolean distanceIncreasing = (differenceBetweenVals[0] >= 0) ? true : false ;
			if (distanceIncreasing){
				// search first decrease in distance
				for(i = 0; i<differenceBetweenVals.length; i++){	// for each int in array, save value in diff
					if (differenceBetweenVals[i] < 0 && differenceBetweenVals[i] > 15){	//### never the case
						System.out.println("feature at: " +i);
					}
				}																//####### should these if conditions contain multiple diffrence indices?
			} else {
				// search first decrease in distance
				for(i = 0; i<differenceBetweenVals.length; i++){
					if (differenceBetweenVals[i] >= 0 && differenceBetweenVals[i] > 15){ //###differenceBetweenVals[i] >= 0 not needed
						System.out.println("feature at: " + i);
					}
				}
			}	// if ende

			// rotiere zum peak
			if(i>36){
				pilot.rotate((72-i)*5);
			} else{
				pilot.rotate(i*5);
			}
			Delay.msDelay(50000);

			// gib distanz aus
			System.out.println("distance to feature: " + measurements[i]);
			System.out.println("Index ist: " + i); 
		}
	}

	public void findPodestPar(int vals){
		measurements = new   int[vals];
		pilot.rotate((vals-1)/2);

	}

	public int getPeak(){
		return measurements[i];
	}
}
