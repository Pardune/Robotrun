package pardune;

import lejos.nxt.Motor;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;

public class ObjectsPardune {

	int prevVal, curVal;
	boolean foundPodest;
	DifferentialPilot pilot;
	UltrasonicSensor ultrasonicSensor;
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
		//findPodest();
		alg();
	}

	private void alg() {
		// TODO Auto-generated method stub
		
	}

	private void findPodest() {
		measurements = new int[72];
		differenceBetweenVals = new int[71];	// 0 is between 0 and 1 of measurement-Array, 5 between 5 and 6...
		// measure, compare difference
		// if: corner(pos to neg): continue
		// else: retreive dose (pos to neg to pos)

		// fill measurments and diff-array via loop
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
					if (differenceBetweenVals[i] < 0 && differenceBetweenVals[i] > 15){
						System.out.println("feature at: " +i);
					}
				}
			} else {
				// search first decrease in distance
				for(i = 0; i<differenceBetweenVals.length; i++){
					if (differenceBetweenVals[i] >= 0 && differenceBetweenVals[i] > 15){
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

			// gib distanz aus
			System.out.println("distance to feature: " + measurements[i]);
			System.out.println("Index ist: " + i); 
		}
	}
	
	public void findPodestPar(int vals){
		measurements = new int[vals];
		pilot.rotate((vals-1)/2);
		
	}
	
	public int getPeak(){
		return measurements[i];
	}
}