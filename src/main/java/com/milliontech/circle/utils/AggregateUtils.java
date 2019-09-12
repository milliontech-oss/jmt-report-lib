package com.milliontech.circle.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AggregateUtils {
		
	private BigDecimal min;
	private BigDecimal max;
	private BigDecimal average;
	private BigDecimal median;
	private BigDecimal sum;
	private BigDecimal upperQuartile;
	private BigDecimal lowerQuartile;
	
	public AggregateUtils(List dataList){
		
		if(dataList == null || dataList.isEmpty()){
			return;
		}
		
		List bdList = new ArrayList(dataList.size());
		
		for(Iterator iter = dataList.iterator(); iter.hasNext();){
			Object obj = iter.next();
			
			if(obj == null){
				continue;
			}
			
			BigDecimal d = obj instanceof BigDecimal ? (BigDecimal) obj : new BigDecimal(obj.toString());
			
			if(min != null){
				min = min.min(d);				
			} else {
				min = d;
			}
			
			if(max != null){
				max = max.max(d);
			} else {
				max = d;
			}
			
			if(sum == null){
				sum = new BigDecimal(0);
			}
			
			sum = sum.add(d);
			
			bdList.add(d);
		}
		
		if(bdList.isEmpty()){
			return;
		}
		
		// sort in decending order
		Collections.sort(bdList, Collections.reverseOrder());
		
		if(bdList.size() % 2 == 0){
			
			BigDecimal a = (BigDecimal) bdList.get(bdList.size() / 2);
			BigDecimal b = (BigDecimal) bdList.get(bdList.size() / 2 - 1);
			
			median = a.add(b).divide(new BigDecimal(2), BigDecimal.ROUND_HALF_UP);
			
		}else{
			
			median = (BigDecimal) bdList.get(bdList.size() / 2);
		}		
		
		if(sum != null){
			average = sum.divide(new BigDecimal(bdList.size()), BigDecimal.ROUND_HALF_UP);
		}
		
		int upperQtrIndex = getUpperQuartileIndex(bdList.size());
		int lowerQtrIndex = getLowerQuartileIndex(bdList.size());
		
		if(upperQtrIndex == -1){
			upperQuartile = new BigDecimal(0);
		} else {
			upperQuartile = (BigDecimal) bdList.get(upperQtrIndex);
		}
		
		if(lowerQtrIndex == -1){
			lowerQuartile = new BigDecimal(0);
		} else {
			lowerQuartile = (BigDecimal) bdList.get(lowerQtrIndex);
		}
		
		min = min.setScale(8, BigDecimal.ROUND_HALF_UP);
		max = max.setScale(8, BigDecimal.ROUND_HALF_UP);
		average = average.setScale(8, BigDecimal.ROUND_HALF_UP);
		median = median.setScale(8, BigDecimal.ROUND_HALF_UP);
		sum = sum.setScale(8, BigDecimal.ROUND_HALF_UP);
		upperQuartile = upperQuartile.setScale(8, BigDecimal.ROUND_HALF_UP);
		lowerQuartile = lowerQuartile.setScale(8, BigDecimal.ROUND_HALF_UP);
	}
	
	private int getUpperQuartileIndex(int nonEmptyValCount){
		int index = (new BigDecimal(nonEmptyValCount)).divide(new BigDecimal(4), BigDecimal.ROUND_CEILING).intValue();
		if(index == 0){
			index = 1;
		} else if (index > nonEmptyValCount){
			index = nonEmptyValCount;
		}
		
		// since excel is 1-index based and java is 0-based, so need to - 1
		return index - 1;
	}
	
	private int getLowerQuartileIndex(int nonEmptyValCount){
		
		int index = (new BigDecimal(nonEmptyValCount))
				.subtract(((new BigDecimal(nonEmptyValCount)).divide(new BigDecimal(4), BigDecimal.ROUND_FLOOR)))
				.add(new BigDecimal(1))
				.intValue();
				
		if(index == 0){
			index = 1;
		} else if (index > nonEmptyValCount){
			index = nonEmptyValCount;
		}
		
		// since excel is 1-index based and java is 0-based, so need to - 1
		return index - 1;
	}
	
	
	public BigDecimal getValue(String mode){
		if("min".equalsIgnoreCase(mode)){
			return min;
		}else if("max".equalsIgnoreCase(mode)){
			return max;
		}else if("".equalsIgnoreCase(mode)){
			return average;
		}else if("average".equalsIgnoreCase(mode)){
			return average;
		}else if("median".equalsIgnoreCase(mode)){
			return median;
		}else if("sum".equalsIgnoreCase(mode)){
			return sum;
		}else if("upperQuartile".equalsIgnoreCase(mode)){
			return upperQuartile;
		}else if("lowerQuartile".equalsIgnoreCase(mode)){
			return lowerQuartile;
		}
		return null;
	}
	
	public BigDecimal min(){		
		return min;
	}

	public BigDecimal max(){
		return max;
	}
	
	public BigDecimal average(){
		return average;
	}
	
	public BigDecimal median(){
		return median;
	}
	
	public BigDecimal sum(){
		return sum;
	}
}
