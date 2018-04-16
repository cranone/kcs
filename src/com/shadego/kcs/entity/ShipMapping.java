package com.shadego.kcs.entity;

import java.util.Date;
import java.util.Set;

/**
* @author Maclaine E-mail:deathencyclopedia@gmail.com
* 
*/
public class ShipMapping {
	//api id
	private Integer id;
	//ship no
	private Integer sortno;
	//name
	private String name;
	//after api id
	private Integer aftershipid;
	//filename
	private String filename;
	//update date
	//@JSONField(format="yyyy-MM-dd")
	private Date lastModified;
	//version
	private String[] version;
	private Integer voicef;
	private Set<Integer> soundFileNames;
	public Integer getAftershipid() {
		return aftershipid;
	}
	public String getFilename() {
		return filename;
	}
	public Integer getId() {
		return id;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public String getName() {
		return name;
	}
	
	public Integer getSortno() {
		return sortno;
	}
	public String[] getVersion() {
		return version;
	}
	public void setAftershipid(Integer aftershipid) {
		this.aftershipid = aftershipid;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setSortno(Integer sortno) {
		this.sortno = sortno;
	}
	public void setVersion(String[] version) {
		this.version = version;
	}
	public Integer getVoicef() {
		return voicef;
	}
	public Set<Integer> getSoundFileNames() {
		return soundFileNames;
	}
	public void setVoicef(Integer voicef) {
		this.voicef = voicef;
	}
	public void setSoundFileNames(Set<Integer> soundFileNames) {
		this.soundFileNames = soundFileNames;
	}
	
	
}
