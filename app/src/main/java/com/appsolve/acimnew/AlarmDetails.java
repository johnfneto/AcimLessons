package com.appsolve.acimnew;

public class AlarmDetails {
	private Integer id;
	private Boolean state;
	private String name;
	private Integer time;
	private Integer repeat;	
	private String ringtone;	
	private Boolean vibrate;
	
	
	public int getId() {
		return id;
		}
	public void setId(int id) {
		this.id = id;
		}	
		
	public String getName() {
		return name;
		}
	public void setName(String name) {
		this.name = name;
		}	
	
	public Boolean getState() {
		return state;
		}
	public void setState(Boolean state) {
		this.state = state;
		}	
	
	public int getTime() {
		return time;
		}
	public void setTime(int time) {
		this.time = time;
		}
	
	public int getRepeat() {
		return repeat;
		}
	public void setRepeat(int repeat) {
		this.repeat = repeat;
		}	
	
	public String getRingtone() {
		return ringtone;
		}
	public void setRingtone(String ringtone) {
		this.ringtone = ringtone;
		}

	public Boolean getVibrate() {
		return vibrate;
		}
	public void setVibrate(Boolean vibrate) {
		this.vibrate = vibrate;
		}	
}
