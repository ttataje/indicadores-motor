package pe.gob.regionica.indicadores.motor.core;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MotorCore implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(MotorCore.class);

	public byte[] process(JSONObject json) throws IOException{
		log.debug("MotorCore.process : " + json.toString());
		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue( "IPhone 5s" , new Double( 20 ) );
		dataset.setValue( "SamSung Grand" , new Double( 20 ) );
		dataset.setValue( "MotoG" , new Double( 40 ) );
		dataset.setValue( "Nokia Lumia" , new Double( 10 ) ); 
		
		JFreeChart chart = ChartFactory.createPieChart(
				"Mobile Sales",   // chart title 
				dataset,          // data    
				true,             // include legend   
				true, 
				false);
		
		int width = 640; 
		int height = 480;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsJPEG( output , chart , width , height );
	    return output.toByteArray();
	}
}
