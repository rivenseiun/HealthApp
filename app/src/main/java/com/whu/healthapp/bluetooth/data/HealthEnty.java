package com.whu.healthapp.bluetooth.data;

import java.io.Serializable;

public class HealthEnty implements Serializable {
	private  int id;
	private String userName;
	private String data;
	private  float tiwen;
	private  int xueyang;
	private  int mailv;
	private  int shuzhangya;
	private  int shousuoya;
	private  int dongmaya;
	private  int resprate;
	private  int xinlv;
	private String ecgcad;
	private String jieguo;
	private String xindian;
	private String savname;
	
	public HealthEnty() {
		super();
	}
	public HealthEnty(int id, String userName, String data, float tiwen,
					  int xueyang, int mailv, int shuzhangya, int shousuoya,
					  int dongmaya, int resprate, int xinlv, String ecgcad,
					  String jieguo, String xindian, String savname) {
		super();
		this.id = id;
		this.userName = userName;
		this.data = data;
		this.tiwen = tiwen;
		this.xueyang = xueyang;
		this.mailv = mailv;
		this.shuzhangya = shuzhangya;
		this.shousuoya = shousuoya;
		this.dongmaya = dongmaya;
		this.resprate = resprate;
		this.xinlv = xinlv;
		this.ecgcad = ecgcad;
		this.jieguo = jieguo;
		this.xindian = xindian;
		this.savname = savname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getData() {
		return data;
	}
	
	public String getSavname() {
		return savname;
	}
	public void setSavname(String savname) {
		this.savname = savname;
	}
	public int getXinlv() {
		return xinlv;
	}
	public void setXinlv(int xinlv) {
		this.xinlv = xinlv;
	}
	public void setData(String data) {
		this.data = data;
	}
	public float getTiwen() {
		return tiwen;
	}
	public void setTiwen(float tiwen) {
		this.tiwen = tiwen;
	}
	public int getXueyang() {
		return xueyang;
	}
	public void setXueyang(int xueyang) {
		this.xueyang = xueyang;
	}
	public int getMailv() {
		return mailv;
	}
	public void setMailv(int mailv) {
		this.mailv = mailv;
	}
	public int getShuzhangya() {
		return shuzhangya;
	}
	public void setShuzhangya(int shuzhangya) {
		this.shuzhangya = shuzhangya;
	}
	public int getShousuoya() {
		return shousuoya;
	}
	public void setShousuoya(int shousuoya) {
		this.shousuoya = shousuoya;
	}
	public int getDongmaya() {
		return dongmaya;
	}
	public void setDongmaya(int dongmaya) {
		this.dongmaya = dongmaya;
	}
	public int getResprate() {
		return resprate;
	}
	public void setResprate(int resprate) {
		this.resprate = resprate;
	}
	public String getEcgcad() {
		return ecgcad;
	}
	public void setEcgcad(String ecgcad) {
		this.ecgcad = ecgcad;
	}
	public String getJieguo() {
		return jieguo;
	}
	public void setJieguo(String jieguo) {
		this.jieguo = jieguo;
	}
	public String getXindian() {
		return xindian;
	}
	public void setXindian(String xindian) {
		this.xindian = xindian;
	}
	

}
