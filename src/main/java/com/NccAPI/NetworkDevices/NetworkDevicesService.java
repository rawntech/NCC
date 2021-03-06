package com.NccAPI.NetworkDevices;

public interface NetworkDevicesService {
    public ApiIfaceData getNetworkDeviceIfaces(String login, String key, Integer id);
    public ApiIfaceData updateNetworkDeviceIfaces(String login, String key, Integer id);
    public ApiIfaceData discoverNetworkDeviceIfaces(String login, String key, Integer id);

    public ApiNetworkDeviceTypeData getNetworkDeviceTypes(String login, String key);

    public ApiNetworkDeviceData createNetworkDevice(String login, String key,
                                    String deviceName,
                                    String deviceIP,
                                    Integer deviceType,
                                    String snmpCommunity,
                                    String addressStreet,
                                    String addressBuild);

    public ApiNetworkDeviceData getNetworkDevices(String login, String key);

    public ApiNetworkDeviceData updateNetworkDevice(String login, String key,
                                                    Integer id,
                                                    String deviceName,
                                                    String deviceIP,
                                                    Integer deviceType,
                                                    String snmpCommunity,
                                                    String addressStreet,
                                                    String addressBuild);

    public ApiNetworkDeviceData deleteNetworkDevice(String login, String key, Integer id);

    public ApiSnmpString getNetworkDeviceSnmpValue(String login, String key, Integer id, String oid);
    public ApiSnmpStrings getNetworkDeviceSnmpValues(String login, String key, Integer id, String oid);
}
