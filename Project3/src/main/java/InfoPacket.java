package main.java;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;

public class InfoPacket implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2519014065393912730L;
	private CpuPerc cpuPerc;
	private Mem memInfo;
	private Date recDate;
	private String recDateString;
	private String reportString;
	
	
	public String getReportString() {
		return reportString;
	}

	public void setReportString(String reportString) {
		this.reportString = reportString;
	}
	
	public void setReportString(){
		String tmp = "";
		tmp += this.getCpuPerc().getSys() + " ";
		tmp += this.getMemInfo().getUsed() + " ";
		tmp += this.getMemInfo().getTotal();
		this.setReportString(tmp);
	}

	public InfoPacket() {
		super();
	}
	
	public InfoPacket(CpuPerc cpuPerc, Mem memInfo, Date recDate) {
		super();
		this.cpuPerc = cpuPerc;
		this.memInfo = memInfo;
		this.recDate = recDate;
	}
	public Date getRecDate() {
		return recDate;
	}
	public void setRecDate(Date recDate) {
		this.recDate = recDate;
		this.setRecDateString();
	}
	public CpuPerc getCpuPerc() {
		return cpuPerc;
	}
	public void setCpuPerc(CpuPerc cpuPerc) {
		this.cpuPerc = cpuPerc;
	}
	public Mem getMemInfo() {
		return memInfo;
	}
	public void setMemInfo(Mem memInfo) {
		this.memInfo = memInfo;
	}
	
	public String getCurrentDateString(Date date) {
		   DateFormat dateFormat = new SimpleDateFormat(MonitorConstants.HIST_DATE_FORMAT);
		   return (dateFormat.format(date));
	}
	public void setRecDateString(){
		this.recDateString = getCurrentDateString(this.recDate);
	}


	public String getRecDateString() {
		return recDateString;
	}
	
	
	
}
