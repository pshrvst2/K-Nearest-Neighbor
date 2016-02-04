import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 */

/**
 * @author Piyush
 *
 */
public class Classify {

	public static String testImageFilePath = "C:/UIUC/Fall/CS440/Assignment3/digitdata/testimages";
	public static String trainImageFilePath = "C:/UIUC/Fall/CS440/Assignment3/digitdata/trainingimages";
	public static String testLabelFilePath = "C:/UIUC/Fall/CS440/Assignment3/digitdata/testlabels";
	public static String trainLabelFilePath = "C:/UIUC/Fall/CS440/Assignment3/digitdata/traininglabels";
	public static List<Digit> testDigitList = new ArrayList<Digit>();
	public static List<Digit> trainDigitList = new ArrayList<Digit>();
	public static Map<Integer, List<Digit>> testDigitMap = new HashMap<Integer, List<Digit>>();
	public static Map<Integer, List<Digit>> trainDigitMap = new HashMap<Integer, List<Digit>>();
	public static int k = 1;
	
	public static int trueClassifed = 0;
	public static int falseClassifed = 0;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readTrainingFiles();
		readTestingFiles();
		classify();
		confusionMatrix();
		System.out.println("True  Classification = "+ trueClassifed+testDigitList.size());
		System.out.println("False Classification = "+ falseClassifed);

	}
	
	public static void confusionMatrix()
	{
		int matrix[][] = new int[10][10];
		for(int i =0; i < 10; i++)
			for(int j =0; j<10; j++)
				matrix[i][j] = 0;
		
		for(Digit testDigit : testDigitList)
		{
			int real = testDigit.getValue();
			int classifiedAs = testDigit.getKnnTestValue();
			
			++matrix[real][classifiedAs];
		}
		System.out.print("Classified as:\t");
		for(int i =0; i < 10; i++)
		{
			System.out.print(i+"\t");
		}
		System.out.println();
		for(int i =0; i < 10; i++)
		{
			System.out.print("Digit    "+i+":\t");
			for(int j =0; j<10; j++)
			{
				System.out.print(matrix[i][j]+"\t");
			}
			System.out.println();
		}
	}
	
	public static void classify()
	{
		List<Thread> testThreads = new ArrayList<Thread>();
		 ExecutorService executor = Executors.newFixedThreadPool(50);
		for(Digit testDigit : testDigitList)
		{
			Thread knnThread = new KNNClassifier(testDigit, trainDigitList, k);
			//knnThread.start();
			executor.execute(knnThread);
			testThreads.add(knnThread);
		}
		
		/*while(testThreads.size()>0)
		{
			for(Thread t : testThreads)
			{
				State state = State.TERMINATED;
				if(t.getState() == state)
				{
					testThreads.remove(t);
				}
			}
		}*/
		executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all threads");
	}
	
	/*public static void classify()
	{
		int instanceNum = 1;
		for(Digit testDigit : testDigitList)
		{
			HashMap<Digit, Double> neighborDistMap = new HashMap<Digit, Double>();
			LinkedHashMap<Digit, Double> sortedMap = new LinkedHashMap<Digit, Double>();
			
			for(Digit trainDigit : trainDigitList)
			{
				Double distance = calculateEuclideanDistance(testDigit, trainDigit);
				neighborDistMap.put(trainDigit, distance);
			}
			sortedMap = sortHashMapByValue(neighborDistMap);
			System.out.println();
			int iter = 0;
			for(Entry<Digit, Double> entry : sortedMap.entrySet())
			{
				if(iter == k)
					break;
				Digit nearestNeighbor = entry.getKey();
				int value = nearestNeighbor.getValue();
				
				if(value == testDigit.getValue())
				{
					//System.out.println();
					trueClassifed++;
					System.out.println("Instance: "+instanceNum+" true classified");
				}
				else
				{
					falseClassifed++;
					System.out.println("Instance: "+instanceNum+"false classified");
				}
				
				iter++;
			}
			instanceNum++;
		}
	}*/
	
	public static double calculateEuclideanDistance(Digit testDigit, Digit trainDigit)
	{
		double sum = 0;
		for(int i = 0; i < 28*28 ; i++)
		{
			sum = sum + (trainDigit.vector[i] - testDigit.vector[i])*(trainDigit.vector[i] - testDigit.vector[i]);
		}
		
		return Math.sqrt(sum);
	}
	
	public static void readTrainingFiles()
	{
		try 
		{
			//List<Digit> digitList = new ArrayList<Digit>();
			Scanner scannerImage = new Scanner(new File(trainImageFilePath));
			Scanner scannerLabel = new Scanner(new File(trainLabelFilePath));
			String labelLine = null;
			String imageLine = null;
			
			while(scannerLabel.hasNextLine())
			{
				Digit digit = new Digit();
				labelLine = scannerLabel.nextLine();
				digit.setValue(Integer.valueOf(labelLine));
				int k = 0;
				int i = 0; int j = 0;
				do
				{
					imageLine = scannerImage.nextLine();
					for(j = 0; j < 28 ; j ++)
					{
						if(imageLine.charAt(j) == ' ')
						{
							digit.matrix[i][j] = 0;
							digit.vector[k] = -10;
						}
						else
						{
							digit.matrix[i][j] = 1;
							digit.vector[k] = 10;
						}
						k++;
					}
					i++;
				}while(scannerImage.hasNextLine() & i%28 != 0);
				trainDigitList.add(digit);
				
			}
			scannerImage.close();
			scannerLabel.close();
			
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void readTestingFiles()
	{
		try 
		{
			//List<Digit> digitList = new ArrayList<Digit>();
			Scanner scannerImage = new Scanner(new File(testImageFilePath));
			Scanner scannerLabel = new Scanner(new File(testLabelFilePath));
			String labelLine = null;
			String imageLine = null;
			int count = 0;
			while(scannerLabel.hasNextLine() /*& count < 10*/)
			{
				Digit digit = new Digit();
				labelLine = scannerLabel.nextLine();
				digit.setValue(Integer.valueOf(labelLine));
				
				int i = 0; int j = 0; int k = 0;
				do
				{
					imageLine = scannerImage.nextLine();
					for(j = 0; j < 28 ; j ++)
					{
						if(imageLine.charAt(j) == ' ')
						{
							digit.matrix[i][j] = 0;
							digit.vector[k] = -10;
						}
						else
						{
							digit.matrix[i][j] = 1;
							digit.vector[k] = 10;
						}
						k++;
					}
					i++;
				}while(scannerImage.hasNextLine() & i%28 != 0);
				testDigitList.add(digit);
				count++;
			}
			scannerImage.close();
			scannerLabel.close();
			
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static LinkedHashMap<Digit, Double> sortHashMapByValue(HashMap<Digit, Double> map) 
	{
		List<Digit> mapKeys = new ArrayList<Digit>(map.keySet());
		List<Double> mapValues = new ArrayList<Double>(map.values());
		Collections.sort(mapValues);
		//Collections.reverse(mapValues);

		LinkedHashMap<Digit, Double> sortedMap = new LinkedHashMap<Digit, Double>();

		Iterator<Double> valueIt = mapValues.iterator();
		while (valueIt.hasNext()) 
		{
			Double val = valueIt.next();
			Iterator<Digit> keyIt = mapKeys.iterator();

			while (keyIt.hasNext()) 
			{
				Digit key = keyIt.next();
				String comp1 = map.get(key).toString();
				String comp2 = val.toString();

				if (comp1.equals(comp2)) 
				{
					map.remove(key);
					mapKeys.remove(key);
					sortedMap.put(key, val);
					break;
				}
			}
		}
		return sortedMap;
	}

}
