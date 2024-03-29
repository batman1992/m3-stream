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

package com.flazr.rtmp;

import android.util.Log;

import com.flazr.util.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class RtmpConfig {

    public static enum Type { SERVER, SERVER_STOP, PROXY, PROXY_STOP }

    public static String SERVER_HOME_DIR = "home";
    public static int TIMER_TICK_SIZE = 100;
    public static int SERVER_PORT = 1935;
    public static int SERVER_STOP_PORT = 1934;
    public static int PROXY_PORT = 8000;
    public static int PROXY_STOP_PORT = 7999;
    public static String PROXY_REMOTE_HOST = "127.0.0.1";
    public static int PROXY_REMOTE_PORT = 1935;

    public static void configureServer() {
        configure(Type.SERVER);
        addShutdownHook(SERVER_STOP_PORT);
    }

    public static int configureServerStop() {
        configure(Type.SERVER_STOP);
        return SERVER_STOP_PORT;
    }

    public static void configureProxy() {
        configure(Type.PROXY);
        addShutdownHook(PROXY_STOP_PORT);
    }

    public static int configureProxyStop() {
        configure(Type.PROXY_STOP);
        return PROXY_STOP_PORT;
    }

    private static void configure(Type type) {
        Utils.outputCopyrightNotice();
        File propsFile = new File("conf/flazr.properties");
        if(!propsFile.exists()) {
        	Log.w(RtmpConfig.class.getName(), propsFile.getAbsolutePath() +" not found, will use configuration defaults");
        } else {
        	Log.i(RtmpConfig.class.getName(), "loading config from: "+ propsFile.getAbsolutePath());
            Properties props = loadProps(propsFile);
            switch(type) {
                case SERVER:
                case SERVER_STOP:
                    Integer serverStop = parseInt(props.getProperty("server.stop.port"));
                    if(serverStop != null) SERVER_STOP_PORT = serverStop;
                    if(type == Type.SERVER_STOP) {
                        break;
                    }
                    Integer serverPort = parseInt(props.getProperty("server.port"));
                    if(serverPort != null) SERVER_PORT = serverPort;
                    SERVER_HOME_DIR = props.getProperty("server.home", "home");
                    File homeFile = new File(SERVER_HOME_DIR);
                    if(!homeFile.exists()) {
                    	Log.e(RtmpConfig.class.getName(), "home dir does not exist, aborting: "+ homeFile.getAbsolutePath());
                        throw new RuntimeException("home dir does not exist: " + homeFile.getAbsolutePath());
                    }
                    Log.i(RtmpConfig.class.getName(), "home dir: "+ homeFile.getAbsolutePath());
                    Log.i(RtmpConfig.class.getName(), "server port: "+SERVER_PORT+" (stop "+SERVER_STOP_PORT+")");
                    break;
                case PROXY:
                case PROXY_STOP:
                    Integer proxyStop = parseInt(props.getProperty("proxy.stop.port"));
                    if(proxyStop != null) PROXY_STOP_PORT = proxyStop;
                    if(type == Type.PROXY_STOP) {
                        break;
                    }
                    Integer proxyPort = parseInt(props.getProperty("proxy.port"));
                    if(proxyPort != null) PROXY_PORT = proxyPort;
                    PROXY_REMOTE_HOST = props.getProperty("proxy.remote.host", "127.0.0.1");
                    Integer proxyRemote = parseInt(props.getProperty("proxy.remote.port"));
                    if(proxyRemote != null) PROXY_REMOTE_PORT = proxyRemote;
                    Log.i(RtmpConfig.class.getName(), "proxy port: "+PROXY_PORT+" (stop "+PROXY_STOP_PORT+")");
                    Log.i(RtmpConfig.class.getName(), "proxy remote host: "+PROXY_REMOTE_HOST+" port: "+ PROXY_REMOTE_PORT);
                    break;
            }
        }        
    }

    private static class ServerShutdownHook extends Thread {

        private final int port;

        public ServerShutdownHook(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            Utils.sendStopSignal(port);
        }

    }

    private static void addShutdownHook(final int port) {
        Runtime.getRuntime().addShutdownHook(new ServerShutdownHook(port));
    }

    private static Properties loadProps(final File file) {
        InputStream is = null;
        final Properties props = new Properties();
        try {
            try {
                is = new FileInputStream(file);
                props.load(is);
            } finally {
                is.close();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return props;
    }

    private static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch(Exception e) {
        	Log.w(RtmpConfig.class.getName(),"unable to parse into integer value: "+ e.getMessage());
            return null;
        }
    }

}
