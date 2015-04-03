package utils;

public class Testing {
	public static int randomFromInterval(int min, int max)
	{
		int range = (max - min) + 1;
		return (int)(Math.random() * range) + min;
	}
}
