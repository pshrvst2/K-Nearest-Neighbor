import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * 
 */

/**
 * @author Piyush
 *
 */
public class KNNClassifier extends Thread{
	
	private HashMap<Digit, Double> neighborDistMap = new HashMap<Digit, Double>();
	private LinkedHashMap<Digit, Double> sortedMap = new LinkedHashMap<Digit, Double>();
	List<Digit> trainDigitList;
	private Digit testDigit;
	private int k;
	
	public KNNClassifier(Digit testDigit, List<Digit> trainDigitList, int k)
	{
		this.testDigit = testDigit;
		this.trainDigitList = trainDigitList;
		this.k = k;
	}
	
	public void run()
	{
		
		// classify
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
			testDigit.setKnnTestValue(value);
			
			/*if(value == testDigit.getValue())
			{
				//System.out.println();
				//trueClassifed++;
				//System.out.println("Instance: true classified");
			}
			else
			{
				//falseClassifed++;
				//System.out.println("Instance: false classified");
			}*/
			
			iter++;
		}
		
	}
	
	public static double calculateEuclideanDistance(Digit testDigit, Digit trainDigit)
	{
		double sum = 0;
		for(int i = 0; i < 28*28 ; i++)
		{
			sum = sum + (trainDigit.vector[i] - testDigit.vector[i])*(trainDigit.vector[i] - testDigit.vector[i]);
		}
		
		return Math.sqrt(sum);
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
