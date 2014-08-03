package com.soveryvaried.homemage.isy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.soveryvaried.homemage.db.Node;

import android.util.Xml;

public class XMLParser {

	public Node parseNodeDetailXML(InputStream in) {
		Node node = null;
		
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, null);
			parser.nextTag();
			
			parser.require(XmlPullParser.START_TAG, null, "nodeInfo");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the entry tag
				if (name.equals("node")) {
					node = readNode(parser);
				} else {
					skip(parser);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return node;
	}
	
	public ArrayList<Node> parseNodesXML(InputStream in) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, null);
			parser.nextTag();
			
			parser.require(XmlPullParser.START_TAG, null, "nodes");
			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				String name = parser.getName();
				// Starts by looking for the entry tag
				if (name.equals("node")) {
					nodes.add(readNode(parser));
				} else {
					skip(parser);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return nodes;
	}
		
	private Node readNode(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "node");
		Node node = new Node();
		while(parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = parser.getName();
	        if (name.equals("address")) {
	            node.setAddress(readAddress(parser));
	        } else if (name.equals("name")) {
	            node.setName(readName(parser));
	        } else if (name.equals("enabled")) {
	            node.setEnabled(readEnabled(parser));
	        } else if (name.equals("property")) {     
	        	node.setStatus(readStatus(parser));
	        } else if (name.equals("type")) {     
	        	node.setType(readType(parser));
	        } else {
	            skip(parser);
	        }
		}
		return node;
	}
	
	private String readAddress(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, null, "address");
	    String address = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, "address");
	    return address;
	}

	private String readType(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, null, "type");
	    String type = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, "type");
	    return type;
	}
	
	private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, null, "name");
	    String name = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, "name");
	    return name;
	}
	
	private boolean readEnabled(XmlPullParser parser) throws IOException, XmlPullParserException {
	    parser.require(XmlPullParser.START_TAG, null, "enabled");
	    String enabledTxt = readText(parser);
	    parser.require(XmlPullParser.END_TAG, null, "enabled");
	    
	    if (enabledTxt.equals("true")) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
	
	private int readStatus(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String statusTxt = "";
	    parser.require(XmlPullParser.START_TAG, null, "property");
	    String tag = parser.getName();
	    String id = parser.getAttributeValue(null, "id");
	    
	    if (tag.equals("property")) {
	        if (id.equals("ST")){
	            statusTxt = parser.getAttributeValue(null, "value");
	            parser.nextTag();
	        } 
	    }
	    parser.require(XmlPullParser.END_TAG, null, "property");
	    if (statusTxt == null || statusTxt.trim().equals("")) {
	    	return 0;
	    } else {
	    	return Integer.parseInt(statusTxt);
	    }
	}
	
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
	    String result = "";
	    if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	        parser.nextTag();
	    }
	    return result;
	}
	
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }
}
