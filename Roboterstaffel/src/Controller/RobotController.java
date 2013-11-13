package Controller;

import java.util.*;
import robot.Robot;

public class RobotController implements EventListener {
	private Robot robot;
	/**
	 * @param args
	 */
	public RobotController(){
		robot = new Robot();
		
		init();
	}
	
	private void init() {
		// let robots search for podests
		
		// somehow (?) evaluate which robot gets the Can initially
		
		// assign roles to Robots (fetch the can / get in position to receive the can)
		// start communication between Robots
		
		
	}	

}
