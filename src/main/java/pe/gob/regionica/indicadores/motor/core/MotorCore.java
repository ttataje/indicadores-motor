package pe.gob.regionica.indicadores.motor.core;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import pe.gob.regionica.indicadores.motor.utils.MotorConstants;
import pe.gob.regionica.indicadores.motor.utils.MotorUtils;

public class MotorCore implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(MotorCore.class);

	public byte[] process(JSONObject json) throws IOException, JSONException{
		log.debug("MotorCore.process : " + json.toString());
		
		String title = json.getString(MotorConstants.title); // "Mobile Sales"
		boolean legend = json.getBoolean(MotorConstants.legend); // true
		boolean tooltips = json.getBoolean(MotorConstants.tooltips); // true
		boolean urls = json.getBoolean(MotorConstants.urls); // false
		int width = json.getInt(MotorConstants.width);  // 640
		int height = json.getInt(MotorConstants.height); // 480
		String orientation = json.getString(MotorConstants.orientation); // vertical
		String type = json.getString(MotorConstants.type); // stackedBar
		String labelX = json.getString(MotorConstants.labelX); // null
		String labelY = json.getString(MotorConstants.labelY); // null
		JSONArray seriesJSON = json.getJSONArray(MotorConstants.series);
		String[] series = MotorUtils.convertJSONArrayToStringArray(seriesJSON);
		JSONArray jsonArray = json.getJSONArray(MotorConstants.data);
		AbstractDataset dataset = MotorUtils.getDataset(jsonArray, type, series);
		
		JFreeChart chart = null;
 
		if(MotorConstants.type_graph.pie.equalsIgnoreCase(type)){
			chart = ChartFactory.createPieChart(
					title,   // chart title 
					(DefaultPieDataset)dataset, // data    
					legend, // legend
					tooltips, // tooltips
					urls);	// urls	
		}else if(MotorConstants.type_graph.stackedBar.equalsIgnoreCase(type)){
			chart = ChartFactory.createStackedBarChart(
					title,   // chart title 
					StringUtils.isEmpty(labelX) ? null : labelX, //
					StringUtils.isEmpty(labelY) ? null : labelY, //
					(CategoryDataset)dataset, // data    
					MotorConstants.plot_orientation.vertical.equals(orientation) ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL,
					legend, // legend
					tooltips, // tooltips
					urls);	// urls
			
			GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
			KeyToGroupMap map = new KeyToGroupMap(series[0]);
			renderer.setSeriesToGroupMap(map);
			
			renderer.setItemMargin(0.0);
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setRenderer(renderer);
			plot.setFixedLegendItems(MotorUtils.createLegendItems(MotorUtils.getLegendFromJSONArray(jsonArray)));
			
		}else if(MotorConstants.type_graph.bar.equalsIgnoreCase(type)){
			chart = ChartFactory.createBarChart(
					title,   // chart title 
					StringUtils.isEmpty(labelX) ? null : labelX, //
					StringUtils.isEmpty(labelY) ? null : labelY, //
					(CategoryDataset)dataset, // data    
					MotorConstants.plot_orientation.vertical.equals(orientation) ? PlotOrientation.VERTICAL : PlotOrientation.HORIZONTAL,
					legend, // legend
					tooltips, // tooltips
					urls);	// urls			
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsJPEG( output , chart , width , height );
	    return output.toByteArray();
	}
}
