package com.milliontech.circle.writer;

import java.io.OutputStream;
import java.util.List;

import com.milliontech.circle.data.model.ParameterData;
import com.milliontech.circle.model.Report;

public interface MTWriter {

	public void writer(List dataList, ParameterData data, Report report, OutputStream out) throws Exception;
	
}
