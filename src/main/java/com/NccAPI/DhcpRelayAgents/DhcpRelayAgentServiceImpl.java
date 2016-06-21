package com.NccAPI.DhcpRelayAgents;

import com.NccAPI.NccAPI;
import com.NccRelayAgent.NccRelayAgent;
import com.NccRelayAgent.NccRelayAgentData;
import com.NccRelayAgent.NccRelayAgentException;
import com.NccRelayAgent.NccRelayAgentType;

import java.util.ArrayList;

public class DhcpRelayAgentServiceImpl implements DhcpRelayAgentService {
    public ArrayList<NccRelayAgentData> getDhcpRelayAgent(String apiKey) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        try {
            ArrayList<NccRelayAgentData> agents = new NccRelayAgent().getRelayAgent();
            return agents;
        } catch (NccRelayAgentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccRelayAgentData getDhcpRelayAgent(String apiKey, Integer id) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        try {
            NccRelayAgentData relayAgentData = new NccRelayAgent().getRelayAgent(id);
            return relayAgentData;
        } catch (NccRelayAgentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccRelayAgentData getDhcpRelayAgentByIP(String apiKey, Long ip) {
        if (!new NccAPI().checkKey(apiKey)) return null;

        try {
            NccRelayAgentData relayAgentData = new NccRelayAgent().getRelayAgentByIP(ip);
            return relayAgentData;
        } catch (NccRelayAgentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<NccRelayAgentType> getDhcpRelayAgentTypes(String apiKey){
        if (!new NccAPI().checkKey(apiKey)) return null;

        try {
            ArrayList<NccRelayAgentType> types = new NccRelayAgent().getRelayAgentTypes();
            return types;
        } catch (NccRelayAgentException e) {
            e.printStackTrace();
        }


        return null;
    }

    public Integer createDhcpRelayAgent(String apiKey, String agentName, Integer agentType, Long agentIP, Integer agentPool, String agentStreet, String agentBuild, Integer agentUnbindedPool){
        if (!new NccAPI().checkKey(apiKey)) return null;

        NccRelayAgentData relayAgentData = new NccRelayAgentData();

        relayAgentData.agentName = agentName;
        relayAgentData.agentType = agentType;
        relayAgentData.agentIP = agentIP;
        relayAgentData.agentPool = agentPool;
        relayAgentData.agentStreet = agentStreet;
        relayAgentData.agentBuild = agentBuild;
        relayAgentData.agentUnbindedPool = agentUnbindedPool;

        try {
            Integer id = new NccRelayAgent().createRelayAgent(relayAgentData);

            return id;
        } catch (NccRelayAgentException e) {
            e.printStackTrace();
        }

        return null;
    }
}