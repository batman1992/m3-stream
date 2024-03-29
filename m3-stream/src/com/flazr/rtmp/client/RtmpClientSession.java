/*
 * Flazr <http://flazr.com> Copyright (C) 2009  Peter Thomas.
 *
 * This file is part of Flazr.
 *
 * Flazr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Flazr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Flazr.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.flazr.rtmp.client;

import android.util.Log;

import com.flazr.rtmp.RtmpHandshake;
import com.flazr.util.Utils;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RtmpClientSession {

    private String host;
    private int port;
    private String app;    
    private String playName;    
    private String saveAs;    
    private boolean rtmpe;
    private Map<String, Object> params;
    private Object[] args;
    private int playStart;
    private int playDuration = -2;    
    private byte[] swfHash;
    private int swfSize;

    private RtmpClientSession() {}

    public static void main(String[] args) {
        final RtmpClientSession session;
        switch(args.length) {
            case 0:
            	Log.e(RtmpClientSession.class.getName(), "at least 1 argument required"); return;
            case 1:
                session = new RtmpClientSession(args[0]); break;
            case 2:
                session = new RtmpClientSession(args[0], args[1]); break;
            case 3:
                session = new RtmpClientSession(args[0], args[1], args[2], null); break;
            case 4:
                session = new RtmpClientSession(args[0], args[1], args[2], args[3]); break;
            case 5:
                session = new RtmpClientSession(args[0], Integer.parseInt(args[1]), args[2], args[3],
                        args[4], false, null); break;
            case 6:
                session = new RtmpClientSession(args[0], Integer.parseInt(args[1]), args[2], args[3],
                        args[4], Boolean.parseBoolean(args[5]), null); break;
            default:
                session = new RtmpClientSession(args[0], Integer.parseInt(args[1]), args[2], args[3],
                        args[4], Boolean.parseBoolean(args[5]), args[6]); break;
        }
        RtmpClient.connect(session);
    }

    public RtmpClientSession(String host, String app, String playName, String saveAs) {
        this(host, 1935, app, playName, saveAs, false, null);
    }

    public RtmpClientSession(String host, int port, String app, String playName, String saveAs, 
            boolean rtmpe, String swfFile) {
        this.host = host;
        this.port = port;
        this.app = app;
        this.playName = playName;
        this.saveAs = saveAs;
        this.rtmpe = rtmpe;        
        if(swfFile != null) {
            initSwfVerification(swfFile);
        }
    }

    private static final Pattern URL_PATTERN = Pattern.compile(
          "(rtmp.?)://" // 1) protocol
        + "([^/:]+)(:[0-9]+)?/" // 2) host 3) port
        + "([^/]+)?/" // 4) app
        + "(.*)" // 5) play
    );

    public RtmpClientSession(String url, String saveAs) {
        this(url);
        this.saveAs = saveAs;
    }

    public RtmpClientSession(String url) {                      
        Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.matches()) {
            throw new RuntimeException("invalid url: " + url);
        }
        Log.d(this.getClass().getName(), "parsing url: "+ url);
        String protocol = matcher.group(1);
        Log.d(this.getClass().getName(), "protocol = "+  protocol);
        host = matcher.group(2);
        Log.d(this.getClass().getName(), "host = "+ host);
        String portString = matcher.group(3);
        if (portString == null) {
        	Log.d(this.getClass().getName(), "port is null in url, will use default 1935");
        } else {
            portString = portString.substring(1); // skip the ':'
            Log.d(this.getClass().getName(), "port = "+ portString);
        }
        port = portString == null ? 1935 : Integer.parseInt(portString);
        app = matcher.group(4);
        Log.d(this.getClass().getName(), "app = "+  app);
        playName = matcher.group(5);
        Log.d(this.getClass().getName(), "playName = "+ playName);        
        rtmpe = protocol.equalsIgnoreCase("rtmpe");
        if(rtmpe) {
        	Log.d(this.getClass().getName(), "rtmpe requested, will use encryption");
        }        
    }

    public String getApp() {
        return app;
    }

    public String getTcUrl() {
        return (rtmpe ? "rtmpe://" : "rtmp://") + host + ":" + port + "/" + app;
    }

    public void setArgs(Object ... args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public void initSwfVerification(String pathToLocalSwfFile) {
        initSwfVerification(new File(pathToLocalSwfFile));
    }

    public void initSwfVerification(File localSwfFile) {
    	Log.i(this.getClass().getName(), "initializing swf verification data for: " + localSwfFile.getAbsolutePath());
        byte[] bytes = Utils.readAsByteArray(localSwfFile);
        byte[] hash = Utils.sha256(bytes, RtmpHandshake.CLIENT_CONST);
        swfSize = bytes.length;
        swfHash = hash;
        Log.i(this.getClass().getName(), "swf verification initialized - size: "+swfSize+", hash: "+ Utils.toHex(swfHash));
    }
    
    public void putParam(String key, Object value) {
        if(params == null) {
            params = new LinkedHashMap<String, Object>();
        }
        params.put(key, value);
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public int getPlayStart() {
        return playStart;
    }

    public void setPlayStart(int playStart) {
        this.playStart = playStart;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSaveAs() {
        return saveAs;
    }

    public void setSaveAs(String saveAs) {
        this.saveAs = saveAs;
    }

    public boolean isRtmpe() {
        return rtmpe;
    }

    public byte[] getSwfHash() {
        return swfHash;
    }

    public void setSwfHash(byte[] swfHash) {
        this.swfHash = swfHash;
    }

    public int getSwfSize() {
        return swfSize;
    }

    public void setSwfSize(int swfSize) {
        this.swfSize = swfSize;
    }
    
}
