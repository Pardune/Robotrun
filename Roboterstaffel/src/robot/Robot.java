package robot;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;

public class Robot implements Observable{

	DifferentialPilot pilot;
	UltrasonicSensor distanceSensor = new UltrasonicSensor(SensorPort.S1);
	enum stateEnum {WAITING, SEARCHFORCAN, SEARCHFORPODEST, LIFTING, CARRYING, DRIVING, DELIVERING };
	int status;

	/**
	 * @param args
	 */
	public Robot() {
		pilot = new DifferentialPilot(43.2f, 4.4f, Motor.A, Motor.C, false);	// sizes in mm
		go();
		
	}
	
	
	
	private void go(){
		pilot.travel(20, true);
		while (distanceSensor.getDistance() <= 40 ) {
			if (true) pilot.stop();
		}
		//pilot.;
		
	
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param state the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
}
