package com.soveryvaried.homemage.db;

import java.util.StringTokenizer;

public class Node {
	private String address;
	private String name;
	private boolean enabled;
	private int status;
	private String type;
	private boolean dimmable = false;
	private boolean relay = false;
    private int group;

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		
		StringTokenizer st = new StringTokenizer(type, ".");
		String category = st.nextToken();
		if (category.equals("1")) {
			this.dimmable = true;
			this.relay = true;
		} else if (category.equals("2")) {
			this.dimmable = false;
			this.relay = true;
		}
	}
	
	public boolean isDimmable() {
		return dimmable;
	}
	public void setDimmable(boolean dimmable) {
		this.dimmable = dimmable;
	}
	public boolean isRelay() {
		return relay;
	}
	public void setRelay(boolean relay) {
		this.relay = relay;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
}
