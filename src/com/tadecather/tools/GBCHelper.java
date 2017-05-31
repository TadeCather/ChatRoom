package com.tadecather.tools;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GBCHelper extends GridBagConstraints{
	
	public GBCHelper(int x, int y, int w, int h){
		this.gridx = x;
		this.gridy = y;
		this.gridwidth = w;
		this.gridheight = h;
		
	}

	public GBCHelper setAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}

	public GBCHelper setInsets(int insetct) {
		Insets insets = new Insets(insetct, insetct, insetct, insetct);
		this.insets = insets;
		return this;
	}

	public GBCHelper setIpad(int ipadx, int ipady) {
		this.ipadx = ipadx;
		this.ipady = ipady;
		return this;
	}

	public GBCHelper setWeight(int weightx, int weighty) {
		this.weightx = weightx;
		this.weighty = weighty;
		return this;
	}
	
	

}
