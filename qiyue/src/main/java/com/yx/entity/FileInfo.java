package com.yx.entity;



public class FileInfo {
	private String f_name;
	private long f_size;
	private String f_type;
	private String f_time;
	private String f_url;
	private String f_enve;
	private String f_rsaprivate;
	public String getF_name() {
		return f_name;
	}
	public void setF_name(String f_name) {
		this.f_name = f_name;
	}
	public long getF_size() {
		return f_size;
	}
	public void setF_size(long f_size) {
		this.f_size = f_size;
	}
	public String getF_type() {
		return f_type;
	}
	public void setF_type(String f_type) {
		this.f_type = f_type;
	}
	public String getF_time() {
		return f_time;
	}
	public void setF_time(String f_time) {
		this.f_time = f_time;
	}
	public String getF_url() {
		return f_url;
	}
	public void setF_url(String f_url) {
		this.f_url = f_url;
	}
	public String getF_enve() {
		return f_enve;
	}
	public void setF_enve(String f_enve) {
		this.f_enve = f_enve;
	}
	public String getF_rsaprivate() {
		return f_rsaprivate;
	}
	public void setF_rsaprivate(String f_rsaprivate) {
		this.f_rsaprivate = f_rsaprivate;
	}
	public FileInfo() {
		super();
	}
	public FileInfo(String f_name, long f_size, String f_type, String f_time, String f_url, String f_enve,
			String f_rsaprivate) {
		super();
		this.f_name = f_name;
		this.f_size = f_size;
		this.f_type = f_type;
		this.f_time = f_time;
		this.f_url = f_url;
		this.f_enve = f_enve;
		this.f_rsaprivate = f_rsaprivate;
	}
	@Override
	public String toString() {
		return "FileInfo [f_name=" + f_name + ", f_size=" + f_size + ", f_type=" + f_type + ", f_time=" + f_time
				+ ", f_url=" + f_url + ", f_enve=" + f_enve + ", f_rsaprivate=" + f_rsaprivate + "]";
	}
	
	
}
