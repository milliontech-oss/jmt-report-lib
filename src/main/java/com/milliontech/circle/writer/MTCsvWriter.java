package com.milliontech.circle.writer;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.helper.PdfHelper;
import com.milliontech.circle.helper.ValueHelper;
import com.milliontech.circle.model.Report;
import com.milliontech.circle.model.TableHeader;

public class MTCsvWriter implements MTWriter{
	
	private static final Logger log = LoggerFactory.getLogger(MTCsvWriter.class);
	
	public void writer(List dataList, ParameterData data, Report report, OutputStream out) throws Exception {
		 
		 ICsvListWriter listWriter = null;
		 
		 try{
			 listWriter = new CsvListWriter(new OutputStreamWriter(out), CsvPreference.STANDARD_PREFERENCE);
			 List headerList = report.getTableHeaderList().isEmpty() ? report.getFullTableHeaderList() : report.getTableHeaderList();
			 String[] headers = new String[headerList.size()];
			 CellProcessor[] processors = new CellProcessor[headerList.size()];

			 int index = 0;
			 for(Iterator iter = headerList.iterator(); iter.hasNext();){
				 TableHeader h = (TableHeader)iter.next();
				 headers[index] = h.getTitle();
				 processors[index] = new Optional();
				 index++;
			 }
			 
		
			 listWriter.writeHeader(headers);
			 
			 for(Iterator iter = dataList.iterator(); iter.hasNext();){
				 Object obj = (Object)iter.next();
				 
				 List list = new ArrayList();
				 
				 for(Iterator hIter = headerList.iterator(); hIter.hasNext();){
					TableHeader header = (TableHeader)hIter.next();
						
					Object value = ValueHelper.getDataValue(obj.getClass(), obj, header.getMethod(), header.getProperty(), data.getRemapValueMap());
					String strValue = PdfHelper.getCellStringValue(value, header.getFormat());
					list.add(strValue);
									
				 }	
				 		
				 listWriter.write(list, processors);
			 }
			 
		 }catch(Exception e){
			 
			 throw e;
			 
		 }finally{
			 
			 if(listWriter!=null){
				 listWriter.close();
			 }
		 }
		 
		 
		 
		 
		 
		 
		 
		 
	}

}
