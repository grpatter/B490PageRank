package main.java.lifecycle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import main.java.MonitorConstants;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;

public class InfoPacket {
	private CpuPerc cpuPerc;
	private Mem memInfo;
	private Date recDate;
	private String recDateString;
	
	
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