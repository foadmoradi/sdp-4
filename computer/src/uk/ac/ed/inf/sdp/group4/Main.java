package uk.ac.ed.inf.sdp.group4;

import java.io.*;

import uk.ac.ed.inf.sdp.group4.world.VisionClient;
import uk.ac.ed.inf.sdp.group4.world.WorldState;
import uk.ac.ed.inf.sdp.group4.controller.FatController;
import uk.ac.ed.inf.sdp.group4.controller.ThinController;
import uk.ac.ed.inf.sdp.group4.controller.Controller;
import uk.ac.ed.inf.sdp.group4.strategy.RobotColour;
import uk.ac.ed.inf.sdp.group4.strategy.Strategy;
import uk.ac.ed.inf.sdp.group4.strategy.TrackBallStrategy;
import uk.ac.ed.inf.sdp.group4.sim.Launcher;
import uk.ac.ed.inf.sdp.group4.strategy.KeyboardStrategy;

public class Main
{
	public static void main(String[] args) throws InterruptedException, IOException
	{
		BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("      ____    _    ____ _____ _     _____");
		System.out.println("     / ___|  / \\  / ___|_   _| |   | ____|");
		System.out.println("    | |     / _ \\ \\___ \\ | | | |   |  _|");
		System.out.println("    | |___ / ___ \\ ___) || | | |___| |___");
		System.out.println("     \\____/_/   \\_\\____/ |_| |_____|_____|");
		System.out.println();
		System.out.println("      ____ ____      _    ____  _   _ _____ ____  ____");
		System.out.println("     / ___|  _ \\    / \\  / ___|| | | | ____|  _ \\/ ___|");
		System.out.println("    | |   | |_) |  / _ \\ \\___ \\| |_| |  _| | |_) \\___ \\ ");
		System.out.println("    | |___|  _ <  / ___ \\ ___) |  _  | |___|  _ < ___) |");
		System.out.println("     \\____|_| \\_\\/_/   \\_\\____/|_| |_|_____|_| \\_\\____/");
		System.out.println();
		System.out.println("  > 1. Blue");
		System.out.println("  > 2. Yellow");
		System.out.println("What colour are we?");
		int colourChoice = Integer.parseInt(keyboard.readLine());
		RobotColour colour;
		if (colourChoice == 1)
		{
			colour = RobotColour.BLUE;
		}
		else
		{
			colour = RobotColour.YELLOW;
		}
		System.out.println("Menu:");
		System.out.println("  > 1. Keyboard Control");
		System.out.println("  > 2. Navigate to Ball");
		System.out.println("  > 3. Simulator");
		System.out.println("Where would you like to go today?");
		int option = Integer.parseInt(keyboard.readLine());
		VisionClient client = new VisionClient();
		Controller controller;
		Strategy strategy = null;
		switch (option)
		{
			case 1:
				controller = new FatController();
				strategy = new KeyboardStrategy(client, controller, colour);
				break;
			case 2:
				controller = new FatController();
				strategy = new TrackBallStrategy(client, controller, colour);
				break;
			case 3:
				WorldState state = new WorldState();
				controller = new ThinController(state);
				strategy = new TrackBallStrategy(client, controller, colour);
				Launcher launcher = new Launcher(state);
			default:
				System.out.println("Goddammit. Give me a real number!");
		}

		strategy.runStrategy();
	}
}
