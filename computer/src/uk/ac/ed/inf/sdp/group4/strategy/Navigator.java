package uk.ac.ed.inf.sdp.group4.strategy;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.ac.ed.inf.sdp.group4.controller.Controller;
import uk.ac.ed.inf.sdp.group4.domain.InvalidAngleException;
import uk.ac.ed.inf.sdp.group4.domain.Position;
import uk.ac.ed.inf.sdp.group4.domain.Vector;
import uk.ac.ed.inf.sdp.group4.world.Robot;
import uk.ac.ed.inf.sdp.group4.world.IVisionClient;
import uk.ac.ed.inf.sdp.group4.world.WorldState;
import uk.ac.ed.inf.sdp.group4.utils.Utils;

public class Navigator
{
	private SamuelLJackson negotiator;

	private List<Waypoint> waypoints = Collections.synchronizedList(
			new ArrayList<Waypoint>());
	
	public Navigator(Controller controller, IVisionClient client, RobotColour colour)
	{
		setNegotiator(new SamuelLJackson(controller, client, colour));
		Thread negotiatorThread = new Thread(getNegotiator());
		negotiatorThread.start();
	}
	private SamuelLJackson getNegotiator() {
		return this.negotiator;
	}

	private void setNegotiator(SamuelLJackson samuelLJackson) {
		checkNotNull(samuelLJackson);
		this.negotiator = samuelLJackson;
	}

	public void navigateTo(Position destination, int angle)
	{
		clearWaypoints();
		addWaypoint(destination, angle);
	}
	
	public void addWaypoint(Position destination, int angle)
	{
		checkNotNull(destination);
		checkArgument(angle >= 0, "Angle must be >= 0");
		checkArgument(angle < 360, "Angle must be < 360");

		Waypoint waypoint = new Waypoint(destination, angle);
		this.waypoints.add(waypoint);
	}

	public void clearWaypoints()
	{
		this.waypoints.clear();
		this.negotiator.abortCurrentWaypoint();
	}
	
	private class SamuelLJackson implements Runnable
	{
		private final int MAX_SPEED = 600;
		
		private Controller controller;
		private IVisionClient client;
		private RobotColour colour;
		
		private Waypoint currentWaypoint;
		
		public SamuelLJackson(Controller controller, IVisionClient client,
				RobotColour colour)
		{
			setController(controller);
			setClient(client);
			setColour(colour);
		}
		
		private void setColour(RobotColour colour)
		{		
			checkNotNull(colour);
			this.colour = colour;
		}

		public void setController(Controller controller)
		{
			checkNotNull(controller, "Controller cannot be null");
			this.controller = controller;
		}
		
		public void setClient(IVisionClient client)
		{
			checkNotNull(client, "IVisionClient cannot be null");
			this.client = client;
		}
		
		public void run()
		{
			boolean atDestination = false;
			
			while(true)
			{
				// Get the current waypoint.
				if (atDestination || this.currentWaypoint == null)
				{
					this.currentWaypoint = grabWaypoint();
					if (this.currentWaypoint == null)
					{
						continue;
					}
					atDestination = false;
				}
				
				// Get the current robot position.
				Robot robot = getCurrentPosition();
				
				System.out.println("Robot: [Angle: " + robot.getFacing() + "] [Position: (" + robot.getX() + ", " + robot.getY() + ")]");
				System.out.println("Waypoint: (" + this.currentWaypoint.getX() + ", " + this.currentWaypoint.getY() + ")");
				
				Vector vectorToTarget = null;
				try {
					vectorToTarget = robot.getPosition().calcVectTo(
							this.currentWaypoint.getPosition());
				} catch (InvalidAngleException e) {
					e.printStackTrace();
				}
				
				// Are we at the destination?
				if (vectorToTarget.getMagnitude() < 10)
				{
					atDestination = true;
					continue;
				}
				else
				{
					double angle = vectorToTarget.angleFrom(robot.getFacing())/2;
					int speed = 0;

					if (angle > Math.abs(15))
					{
						speed = MAX_SPEED/2;
					}

					else
					{
						speed = MAX_SPEED;
					}

					System.out.println("S-Angle: " + angle);
					
					if (angle < 0)
					{
						System.out.println("Turning left");
						
						int rightMotorSpeed = Utils.clamp((int)(0.85 * speed), -900, 900);
						int leftMotorSpeed = Utils.clamp((slowerWheelSpeed(angle, speed)), -900, 900);

						controller.setRightMotorSpeed(rightMotorSpeed);
						controller.setLeftMotorSpeed(leftMotorSpeed);
					}
					else
					{
						System.out.println("Turning right");

						int leftMotorSpeed = Utils.clamp(speed, -900, 900);
						int rightMotorSpeed = Utils.clamp(
							(int)(0.85 * slowerWheelSpeed(angle, speed)), -900, 900);

						controller.setLeftMotorSpeed(leftMotorSpeed);
						controller.setRightMotorSpeed(rightMotorSpeed);
					}
					
					Utils.pause(40);
				}
				
			}
		}
		
		public void abortCurrentWaypoint()
		{
			this.currentWaypoint = null;
		}
		
		private Waypoint grabWaypoint()
		{
			synchronized(waypoints)
			{
				if (waypoints.isEmpty())
				{
					return null;
				}
				return waypoints.remove(0);
			}
		}
		
		private Robot getCurrentPosition()
		{
			WorldState state = client.getWorldState();
			return (this.colour == RobotColour.BLUE) ? state.getBlue() : state.getYellow();
		}

		private int slowerWheelSpeed(double val, int speed)
		{		
			return (int) (MAX_SPEED - (MAX_SPEED) * Math.abs(val)/45);
		}
	}
}
