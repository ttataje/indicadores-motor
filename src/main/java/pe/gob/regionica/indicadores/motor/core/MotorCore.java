package pe.gob.regionica.indicadores.motor.core;

import java.awt.SystemColor;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
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
		
		String title = null;
		Boolean legend = null;
		Boolean tooltips = null;
		Boolean urls = null;
		Integer width = null;
		Integer height = null;
		String orientation = null;
		String type = null;
		String labelX = null;
		String labelY = null;
		JSONArray seriesJSON = null;
		JSONArray jsonArray = null;
		JSONArray jsonArray2 = null;
		
		try{
			title = json.getString(MotorConstants.title); // "Mobile Sales"
		}catch(Exception e){
			log.warn("no title declarated");
			title = "";
		}
		try{
			legend = json.getBoolean(MotorConstants.legend); // true
		}catch(Exception e){
			log.warn("no legend declarated");
			legend = false;
		}
		try{
			tooltips = json.getBoolean(MotorConstants.tooltips); // true
		}catch(Exception e){
			log.warn("no tooltip declarated");
			tooltips = false;
		}
		try{
			urls = json.getBoolean(MotorConstants.urls); // false
		}catch(Exception e){
			log.warn("no urls declarated");
			urls = false;
		}
		try{
			width = json.getInt(MotorConstants.width);  // 640
		}catch(Exception e){
			log.warn("no width declarated");
			width = 640;
		}
		try{
			height = json.getInt(MotorConstants.height); // 480
		}catch(Exception e){
			log.warn("no height declarated");
			height = 480;
		}
		try{
			orientation = json.getString(MotorConstants.orientation); // vertical
		}catch(Exception e){
			log.warn("no orientation declarated");
			orientation = MotorConstants.plot_orientation.vertical;
		}
		try{
			type = json.getString(MotorConstants.type); // stackedBar
		}catch(Exception e){
			log.warn("no type declarated");
			type = MotorConstants.type_graph.bar;
		}
		try{
			labelX = json.getString(MotorConstants.labelX); // null
		}catch(Exception e){
			log.warn("no labelX declarated");
			labelX = null;
		}
		try{
			labelY = json.getString(MotorConstants.labelY); // null
		}catch(Exception e){
			log.warn("no labelY declarated");
			labelY = null;
		}
		try{
			seriesJSON = json.getJSONArray(MotorConstants.series);
		}catch(Exception e){
			log.warn("no seriesJSON declarated");
			seriesJSON = new JSONArray();
		}
		try{
			jsonArray = json.getJSONArray(MotorConstants.data);
		}catch(Exception e){
			log.warn("no data declarated");
			jsonArray = new JSONArray();
		}
		try{
			jsonArray2 = json.getJSONArray(MotorConstants.dataAxis);
		}catch(Exception e){
			log.warn("no dataAxis declarated");
			jsonArray2 = new JSONArray();
		}
		String[] series = MotorUtils.convertJSONArrayToStringArray(seriesJSON);
		AbstractDataset dataset = MotorUtils.getDataset(jsonArray, type, series);
		AbstractDataset datasetAxis = MotorUtils.getDataset(jsonArray2, type, series);
		
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
			
			List<Map<String, String>> legendList = MotorUtils.getLegendFromJSONArray(jsonArray);
			
			GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
			KeyToGroupMap map = new KeyToGroupMap(series[0]);
			renderer.setSeriesToGroupMap(map);
			
			renderer.setItemMargin(0.0);
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setBackgroundPaint(SystemColor.inactiveCaption);
			plot.setRenderer(renderer);
			plot.setFixedLegendItems(MotorUtils.createLegendItems(legendList));
			
			((BarRenderer)plot.getRenderer()).setBarPainter(new StandardBarPainter());
			BarRenderer r = (BarRenderer)chart.getCategoryPlot().getRenderer();
			for(int i = 0; i < legendList.size(); i++){
				Map<String, String> item = legendList.get(i);
				r.setSeriesPaint(i, MotorUtils.hex2Rgb(item.get(MotorConstants.color)));
			}
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
			
			List<Map<String, String>> legendList = MotorUtils.getLegendFromJSONArray(jsonArray);
			
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setBackgroundPaint(SystemColor.inactiveCaption);
			plot.setFixedLegendItems(MotorUtils.createLegendItems(legendList));
			if(plot.getOrientation() == PlotOrientation.HORIZONTAL)
				plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
			
			((BarRenderer)plot.getRenderer()).setBarPainter(new StandardBarPainter());
			BarRenderer r = (BarRenderer)chart.getCategoryPlot().getRenderer();
			for(int i = 0; i < legendList.size(); i++){
				Map<String, String> item = legendList.get(i);
				r.setSeriesPaint(i, MotorUtils.hex2Rgb(item.get(MotorConstants.color)));
			}

			if(datasetAxis != null){
				for(int i = 1 ; i <= jsonArray2.length(); i++){
					plot.setDataset(i, (CategoryDataset)datasetAxis);
					plot.mapDatasetToRangeAxis(i, i);
					
					final CategoryAxis domainAxis = plot.getDomainAxis();
					domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
					if(i == 1){
						JSONObject jsonObject = jsonArray2.getJSONObject(0);
						final ValueAxis axis2 = new NumberAxis(jsonObject.getString(MotorConstants.key));
						plot.setRangeAxis(i, axis2);
					}
					
					final LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
					plot.setRenderer(i, renderer2);
					plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
				}
			}
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsJPEG( output , chart , width , height );
	    return output.toByteArray();
	}
}
