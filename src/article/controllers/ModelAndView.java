package article.controllers;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {//같은 형식의 데이터를 넘기기 위해 사용
	private String viewName;
	private Map<String,Object> model = new HashMap<String,Object>();

	
	public ModelAndView(){}
	
	public ModelAndView(String viewName) {//별도의 생성자 없이 폼으로 접근하기 위해 만든 메소드
		this.viewName = viewName;
	}
	
	public ModelAndView(String viewName, String key, Object obj) {
		this.viewName = viewName;
		model.put(key, obj);
	}
	
	
	
	public Map<String, Object> getModel() {
		return model;
	}
	public void addObject(String key, Object obj) {
		model.put(key, obj);
	}
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	@Override
	public String toString() {
		return "ModelAndView [model=" + model + ", viewName=" + viewName + "]";
	}
	
	
	
}
	
	

