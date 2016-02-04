/**
 * 
 */

/**
 * @author Piyush
 *
 */
public class Digit {
	
	private int value;
	public int[][] matrix;
	public int[] vector;
	private int knnTestValue;
	
	public Digit()
	{
		matrix = new int[28][28];
		vector = new int[28*28];
	}
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(int[][] matrix) {
		this.matrix = matrix;
	}
	public int getKnnTestValue() {
		return knnTestValue;
	}
	public void setKnnTestValue(int knnTestValue) {
		this.knnTestValue = knnTestValue;
	}

}
