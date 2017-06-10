package pe.gob.regionica.indicadores.motor.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MotorUtils {

	
	public static AbstractDataset getDataset(JSONArray data, String type, String[] series) throws JSONException{
		AbstractDataset dataset = null;
		if(MotorConstants.type_graph.pie.equalsIgnoreCase(type)){
			dataset = new DefaultPieDataset();
		}else{
			dataset = new DefaultCategoryDataset();
		}

		if(data.length() > 0){
			for(int i = 0; i < data.length(); i ++){
				JSONObject item = data.getJSONObject(i);
				JSONArray values = item.getJSONArray(MotorConstants.values);
				if(MotorConstants.type_graph.pie.equalsIgnoreCase(type)){
					//dataset.addValue(20.3, "Product 1 (US)", "Jan 04");
				}else{
					for(int j = 0; j < values.length(); j++){
						((DefaultCategoryDataset)dataset).addValue(values.getDouble(j), item.getString(MotorConstants.key), series[j]);
					}
				}
			}
		}
		return dataset;
	}

	public static String[] convertJSONArrayToStringArray(JSONArray seriesJSON) throws JSONException {
		if(seriesJSON != null){
			String[] series = new String[seriesJSON.length()];
			for(int i = 0; i < seriesJSON.length(); i++){
				series[i] = seriesJSON.getString(i);
			}
			return series;
		}
		return null;
	}
	
	public static List<Map<String,String>> getLegendFromJSONArray(JSONArray data) throws JSONException{
		List<Map<String, String>> result = null;
		for(int i = 0; i < data.length(); i++){
			if(result == null) result = new ArrayList<Map<String, String>>();
			JSONObject jsonObject = data.getJSONObject(i);
			Map<String, String> item = new HashMap<String, String>();
			item.put(MotorConstants.label, jsonObject.getString(MotorConstants.key));
			item.put(MotorConstants.color, jsonObject.getString(MotorConstants.color));
			result.add(item);
		}
		return result;
	}
	
	
	public static LegendItemCollection createLegendItems(List<Map<String,String>> legend) {
		LegendItemCollection result = new LegendItemCollection();
		for(int i = 0; i < legend.size(); i++){
			Map<String, String> _legend = legend.get(i);
			LegendItem item = new LegendItem(_legend.get(MotorConstants.label), hex2Rgb(_legend.get(MotorConstants.color)));
			result.add(item);
		}
		return result;
	}

	/**
	 * 
	 * @param colorStr e.g. "#FFFFFF"
	 * @return 
	 */
	public static Color hex2Rgb(String colorStr) {
	    return new Color(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}
}
