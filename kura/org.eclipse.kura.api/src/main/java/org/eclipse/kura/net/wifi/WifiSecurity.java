/*******************************************************************************
 * Copyright (c) 2011, 2020 Eurotech and/or its affiliates and others
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *  Eurotech
 ******************************************************************************/
package org.eclipse.kura.net.wifi;

/**
 * Flags describing the security capabilities of an access point.
 */
public enum WifiSecurity {

    /** None */
    NONE(0x0),
    /** Supports pairwise 40-bit WEP encryption. */
    PAIR_WEP40(0x1),
    /** Supports pairwise 104-bit WEP encryption. */
    PAIR_WEP104(0x2),
    /** Supports pairwise TKIP encryption. */
    PAIR_TKIP(0x4),
    /** Supports pairwise CCMP encryption. */
    PAIR_CCMP(0x8),
    /** Supports a group 40-bit WEP cipher. */
    GROUP_WEP40(0x10),
    /** Supports a group 104-bit WEP cipher. */
    GROUP_WEP104(0x20),
    /** Supports a group TKIP cipher. */
    GROUP_TKIP(0x40),
    /** Supports a group CCMP cipher. */
    GROUP_CCMP(0x80),
    /** Supports PSK key management. */
    KEY_MGMT_PSK(0x100),
    /** Supports 802.1x key management. */
    KEY_MGMT_802_1X(0x200),
    /** Supports no encryption. */
    SECURITY_NONE(0x400),
    /** Supports WEP encryption. */
    SECURITY_WEP(0x800),
    /** Supports WPA encryption. */
    SECURITY_WPA(0x1000),
    /** Supports WPA2 encryption. */
    SECURITY_WPA2(0x2000),
    /** Supports WPA and WPA2 encryption. */
    SECURITY_WPA_WPA2(0x4000);

    private int code;

    private WifiSecurity(int code) {
        this.code = code;
    }

    public static WifiSecurity parseCode(int code) {
        for (WifiSecurity mode : WifiSecurity.values()) {
            if (mode.code == code) {
                return mode;
            }
        }

        return null;
    }

    public static int getCode(WifiSecurity security) {
        for (WifiSecurity mode : WifiSecurity.values()) {
            if (mode == security) {
                return mode.code;
            }
        }

        return -1;
    }
}
