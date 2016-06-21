package com.NccDhcp;

import com.NccPools.NccPoolData;
import com.NccPools.NccPools;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccDhcpLeases {
    private static Logger logger = Logger.getLogger(NccDhcpServer.class);
    private NccQuery query;

    public NccDhcpLeases() {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    private NccDhcpLeaseData fillLeaseData(CachedRowSetImpl rs) {
        NccDhcpLeaseData leaseData = new NccDhcpLeaseData();
        if (rs != null) {
            try {
                leaseData.id = rs.getInt("id");
                leaseData.leaseStart = rs.getLong("leaseStart");
                leaseData.leaseExpire = rs.getLong("leaseExpire");
                leaseData.leaseIP = rs.getLong("leaseIP");
                leaseData.leaseRouter = rs.getLong("leaseRouter");
                leaseData.leaseNetmask = rs.getLong("leaseNetmask");
                leaseData.leaseDNS1 = rs.getLong("leaseDNS1");
                leaseData.leaseDNS2 = rs.getLong("leaseDNS2");
                leaseData.leaseNextServer = rs.getLong("leaseNextServer");
                leaseData.leaseClientMAC = rs.getString("leaseClientMAC");
                leaseData.leaseRemoteID = rs.getString("leaseRemoteID");
                leaseData.leaseCircuitID = rs.getString("leaseCircuitID");
                leaseData.leaseRelayAgent = rs.getLong("leaseRelayAgent");
                leaseData.leasePool = rs.getInt("leasePool");
                leaseData.leaseUID = rs.getInt("leaseUID");
                leaseData.transId = rs.getInt("transId");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return leaseData;
    }

    public ArrayList<NccDhcpLeaseData> getLeases() throws NccDhcpException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpLeases");

            if (rs != null) {
                ArrayList<NccDhcpLeaseData> leases = new ArrayList<>();

                try {
                    while (rs.next()) {
                        NccDhcpLeaseData leaseData = fillLeaseData(rs);

                        if (leaseData != null) {
                            leases.add(leaseData);
                        }
                    }

                    return leases;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccDhcpException("SQL error: " + e.getMessage());
        }

        return null;
    }

    public NccDhcpLeaseData getLeases(Integer id) throws NccDhcpException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpLeases WHERE id=" + id);

            if (rs != null) {
                try {
                    if (rs.next()) {
                        NccDhcpLeaseData leaseData = fillLeaseData(rs);

                        if (leaseData != null) {
                            return leaseData;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccDhcpException("SQL error: " + e.getMessage());
        }

        return null;
    }

    public NccDhcpLeaseData allocateLease(Integer uid, NccPoolData poolData, String clientMAC, String remoteID, String circuitID, Long RelayAgent, Integer transId) throws NccDhcpException {

        try {
            ArrayList<NccDhcpLeaseData> leases = getLeases();

            for (Long ip = poolData.poolStart; ip <= poolData.poolEnd; ip++) {

                Long allocated = ip;

                for (NccDhcpLeaseData lease : leases) {
                    if (lease.leaseIP.equals(ip)) {
                        allocated = 0L;
                        break;
                    } else allocated = ip;
                }

                if (allocated > 0) {
                    NccDhcpLeaseData newLease = new NccDhcpLeaseData();
                    Long leaseStart = System.currentTimeMillis() / 1000L;
                    Long leaseExpire = leaseStart + poolData.poolLeaseTime;

                    try {
                        ArrayList<Integer> id = query.updateQuery("INSERT INTO nccDhcpLeases (" +
                                "leaseStart, " +
                                "leaseExpire, " +
                                "leaseIP, " +
                                "leaseRouter, " +
                                "leaseNetmask, " +
                                "leaseDNS1, " +
                                "leaseDNS2, " +
                                "leaseNextServer, " +
                                "leaseClientMAC, " +
                                "leaseRemoteID, " +
                                "leaseCircuitID, " +
                                "leaseRelayAgent, " +
                                "leaseStatus, " +
                                "leaseUID, " +
                                "leasePool, " +
                                "transId) VALUES (" +
                                leaseStart + ", " +
                                leaseExpire + ", " +
                                allocated + ", " +
                                poolData.poolRouter + ", " +
                                poolData.poolNetmask + ", " +
                                poolData.poolDNS1 + ", " +
                                poolData.poolDNS2 + ", " +
                                poolData.poolNextServer + ", " +
                                "'" + clientMAC + "', " +
                                "'" + remoteID + "', " +
                                "'" + circuitID + "', " +
                                RelayAgent + ", " +
                                "0, " +
                                uid + ", " +
                                poolData.id + ", " +
                                "0" +
                                ")");

                        if (id.get(0) > 0) {
                            newLease.id = id.get(0);
                            newLease.leaseStart = leaseStart;
                            newLease.leaseExpire = leaseExpire;
                            newLease.leaseIP = allocated;
                            newLease.leaseRouter = poolData.poolRouter;
                            newLease.leaseNetmask = poolData.poolNetmask;
                            if (poolData.poolDNS1 > 0) {
                                newLease.leaseDNS1 = poolData.poolDNS1;
                            } else newLease.leaseDNS1 = null;
                            if (poolData.poolDNS2 > 0) {
                                newLease.leaseDNS2 = poolData.poolDNS2;
                            } else newLease.leaseDNS2 = null;
                            if (poolData.poolNextServer > 0) {
                                newLease.leaseNextServer = poolData.poolNextServer;
                            } else newLease.leaseNextServer = null;
                            newLease.leaseClientMAC = clientMAC;
                            newLease.leaseRemoteID = remoteID;
                            newLease.leaseCircuitID = circuitID;
                            newLease.leaseRelayAgent = RelayAgent;
                            newLease.leasePool = poolData.id;
                            newLease.transId = transId;

                            return newLease;
                        }

                    } catch (NccQueryException e) {
                        e.printStackTrace();
                    }
                }
            }

            throw new NccDhcpException("No free addresses in pool: " + poolData.poolName);

        } catch (NccDhcpException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccDhcpLeaseData getLeaseByUid(Integer uid) throws NccDhcpException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpLeases WHERE leaseUID=" + uid);

            try {
                if (rs.next()) {
                    NccDhcpLeaseData leaseData = fillLeaseData(rs);

                    if (leaseData != null) {
                        return leaseData;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccDhcpException("SQL error: " + e.getMessage());
        }

        return null;
    }

    public NccDhcpLeaseData getLeaseByIP(Long ip) throws NccDhcpException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpLeases WHERE leaseIP=" + ip);

            if (rs != null) {
                try {
                    if (rs.next()) {
                        NccDhcpLeaseData leaseData = fillLeaseData(rs);

                        if (leaseData != null) {
                            return leaseData;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccDhcpException("SQL error: " + e.getMessage());
        }

        return null;
    }

    public NccDhcpLeaseData getLeaseByMAC(Long relayAgent, String circuitID, String mac, Integer transId) {
        CachedRowSetImpl rs;
        String relayAgentWhere = "";
        String circuitIDWhere = "";

        if (relayAgent > 0) {
            relayAgentWhere = " AND leaseRelayAgent=" + relayAgent;
        }

        if (!circuitID.equals("")) {
            relayAgentWhere = " AND leaseCircuitID='" + circuitID + "'";
        }

        try {
            rs = query.selectQuery("SELECT * FROM nccDhcpLeases WHERE leaseClientMAC='" + mac + "'" + relayAgentWhere + circuitIDWhere);

            if (rs != null) {
                try {
                    if (rs.next()) {
                        NccDhcpLeaseData leaseData = fillLeaseData(rs);

                        if (leaseData != null) {
                            return leaseData;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccDhcpLeaseData acceptLease(Long clientIP, String clientMAC, String remoteID, String circuitID, Integer transId) {

        CachedRowSetImpl rs;

        // TODO: 4/20/16 not needed to determine id of lease
        try {
            String condition = "leaseClientMAC='" + clientMAC + "' ";

            if (!remoteID.equals("")) condition += "AND leaseRemoteID='" + remoteID + "' ";
            if (!circuitID.equals("")) condition += "AND leaseCircuitID='" + circuitID + "' ";

            rs = query.selectQuery("SELECT id FROM nccDhcpLeases WHERE " +
                    "leaseIP=" + clientIP + " AND " +
                    condition);

            if (rs != null) {
                try {
                    if (rs.next()) {
                        try {
                            Integer id = rs.getInt("id");
                            NccDhcpLeaseData lease = getLeases(id);

                            if (lease != null) {
                                NccPoolData poolData = new NccPools().getPool(lease.leasePool);

                                Long leaseStart = System.currentTimeMillis() / 1000L;
                                Long leaseExpire = leaseStart + poolData.poolLeaseTime;
                                Integer interim = poolData.poolLeaseTime + Math.round(poolData.poolLeaseTime / 3);

                                query.updateQuery("UPDATE nccDhcpLeases SET leaseStatus=1, leaseStart=UNIX_TIMESTAMP(NOW()), leaseExpire=UNIX_TIMESTAMP(NOW())+" + interim + " WHERE id=" + id);
                                return lease;
                            }
                            return null;
                        } catch (NccDhcpException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void renewLease(NccDhcpLeaseData leaseData) {

        NccPoolData poolData = new NccPools().getPool(leaseData.leasePool);
        Integer interim = poolData.poolLeaseTime + Math.round(poolData.poolLeaseTime / 3);

        try {
            query.updateQuery("UPDATE nccDhcpLeases SET " +
                    "leaseStatus=1, " +
                    "leaseExpire=UNIX_TIMESTAMP(NOW())+" + interim + " " +
                    "WHERE " +
                    "leaseRelayAgent=" + leaseData.leaseRelayAgent + " AND " +
                    "leaseClientMAC='" + leaseData.leaseClientMAC + "'");
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public void releaseLease() {

    }

    public void cleanupLeases() {
        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM nccDhcpLeases WHERE leaseExpire<UNIX_TIMESTAMP(NOW())");
            if (ids != null) for (Integer id : ids) {
                logger.debug("Lease expired: " + id);
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }
}
