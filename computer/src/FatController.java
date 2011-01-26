import lejos.pc.comm.*;
import java.io.*;

public class FatController
{
	private NXTComm nxtComm;
	private DataInputStream dataIn;
	private DataOutputStream dataOut;

	public FatController()
	{
		try
		{
			System.out.println("Attempting connection with robot...");
			nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			NXTInfo nxtInfo = new NXTInfo(NXTCommFactory.BLUETOOTH, "WAR BASTARD", "00:16:53:0A:07:1D");
			nxtComm.open(nxtInfo);
			System.out.println("Connection achieved!");
		}
		catch (NXTCommException e)
		{
			System.out.println("FAILED");
			e.printStackTrace();
		}

		dataOut = new DataOutputStream(nxtComm.getOutputStream());
		dataIn = new DataInputStream(nxtComm.getInputStream());
	}

	public void drivef(int distance)
	{
		//calls drive on the robot
		sendCommand(0, distance);
	}

	public void driveb(int distance)
	{
		//calls drive on the robot
		sendCommand(1, distance);
	}

	public void shoot()
	{
		//calls shoot on the robot
		sendCommand(2, 0);
	}

	public void beserk(int beserk)
	{
		if (beserk < 2 && beserk >= 0)
		{
			sendCommand(3, beserk);
		}
	}

	public void finish()
	{
		//calls finish on robot
		sendCommand(99, 0);

		try
		{
			dataIn.close();
			dataOut.close();
			nxtComm.close();
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	//change this back!!!!!!!
	public void sendCommand(int command, int argument)
	{
		try
		{
			dataOut.writeInt(command);
			dataOut.writeInt(argument);
			dataOut.flush();
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}