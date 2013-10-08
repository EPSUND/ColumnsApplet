package utils;

public class MathToolbox {
	public static final double epsilon = 0.0001;
	
	public static boolean isZero(double val)
	{
		return Math.abs(val) <= epsilon;
	}
}
