package pe.gob.regionica.indicadores.motor.rest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pe.gob.regionica.indicadores.motor.core.MotorCore;

@Controller
public class MotorRest {
	
	private final Logger log = LoggerFactory.getLogger(MotorRest.class);
	
	private static MotorCore motorCore;
	
	public MotorRest(){
		if(motorCore == null) motorCore = new MotorCore();
	}

	@ResponseBody
	@RequestMapping(value = "/generate", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] generate(String json) throws IOException, JSONException  {
		log.debug("json : " + json);
		JSONObject jsonObject = null;
		try{
			jsonObject = new JSONObject(json);
		}catch(Exception e){
			log.error("[MotorRest] Can't parse json input");
		}
		
		if(jsonObject == null) jsonObject = new JSONObject();
	    return motorCore.process(jsonObject);
	}
	
	@ResponseBody
	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] test() throws IOException, JSONException  {
		File file = null;
		try {
			file = new File(this.getClass().getClassLoader().getResource("dummy.json").getFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String json = FileUtils.readFileToString(file, Charset.defaultCharset());
		log.debug("json : " + json);
	    return motorCore.process(new JSONObject(json));
	}
}
