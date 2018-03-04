package net.suntrans.suntransyanshi.bean;

/**
 * Created by Looney on 2017/3/28.
 */

public class Config_EWM {
    public String wifi_mode;
    public String wifi_ssid;
    public String security_mode;
    public String wifi_key;

    public String wifi_ssid1;
    public String security_mode1;
    public String wifi_key1;

    public String wifi_ssid2;


    public String security_mode2;
    public String wifi_key2;

    public String wifi_ssid3;
    public String security_mode3;
    public String wifi_key3;

    public String wifi_ssid4;
    public String security_mode4;
    public String wifi_key4;


    public String uap_ssid;
    public String uap_secmode;
    public String uap_key;
    public String dhcp_enalbe;

    public String local_ip_addr;
    public String netmask;
    public String gateway_ip_addr;
    public String dns_server;
    public String mstype;
    public String remote_server_mode;
    public String remote_dns;
    public String rport;
    public String lport;
    public String estype;
    public String esaddr;
    public String esrport;
    public String eslport;
    public String baudrate;
    public String parity;
    public String data_length;
    public String stop_bits;
    public String cts_rts_enalbe;
    public String dma_buffer_size;
    public String uart_trans_mode;
    public String device_num;
    public String ps_mode;
    public String tx_power;
    public String keepalive_num;
    public String keepalive_time;
    public String socks_type;
    public String socks_addr;
    public String socks_port;

    public String socks_user;
    public String socks_pass;

    public String socks_1;
    public String socks_2;
    public String web_user;
    public String web_pass;
    public String cld_id;
    public String cld_key;
    public String cld_apikey;
    public String device_name;
    public String roam_val;
    public String udp_enable;
    public String ccode;

    public String reset;


    @Override
    public String toString() {
        return "Config_EWM{" +
                "wifi_mode='" + wifi_mode + '\'' +
                ", wifi_ssid='" + wifi_ssid + '\'' +
                ", security_mode='" + security_mode + '\'' +
                ", wifi_key='" + wifi_key + '\'' +
                ", wifi_ssid1='" + wifi_ssid1 + '\'' +
                ", security_mode1='" + security_mode1 + '\'' +
                ", wifi_key1='" + wifi_key1 + '\'' +
                ", wifi_ssid2='" + wifi_ssid2 + '\'' +
                ", security_mode2='" + security_mode2 + '\'' +
                ", wifi_key2='" + wifi_key2 + '\'' +
                ", wifi_ssid3='" + wifi_ssid3 + '\'' +
                ", security_mode3='" + security_mode3 + '\'' +
                ", wifi_key3='" + wifi_key3 + '\'' +
                ", wifi_ssid4='" + wifi_ssid4 + '\'' +
                ", security_mode4='" + security_mode4 + '\'' +
                ", wifi_key4='" + wifi_key4 + '\'' +
                ", uap_ssid='" + uap_ssid + '\'' +
                ", uap_secmode='" + uap_secmode + '\'' +
                ", uap_key='" + uap_key + '\'' +
                ", dhcp_enalbe='" + dhcp_enalbe + '\'' +
                ", local_ip_addr='" + local_ip_addr + '\'' +
                ", netmask='" + netmask + '\'' +
                ", gateway_ip_addr='" + gateway_ip_addr + '\'' +
                ", dns_server='" + dns_server + '\'' +
                ", mstype='" + mstype + '\'' +
                ", remote_server_mode='" + remote_server_mode + '\'' +
                ", remote_dns='" + remote_dns + '\'' +
                ", rport='" + rport + '\'' +
                ", lport='" + lport + '\'' +
                ", estype='" + estype + '\'' +
                ", esaddr='" + esaddr + '\'' +
                ", esrport='" + esrport + '\'' +
                ", eslport='" + eslport + '\'' +
                ", baudrate='" + baudrate + '\'' +
                ", parity='" + parity + '\'' +
                ", data_length='" + data_length + '\'' +
                ", stop_bits='" + stop_bits + '\'' +
                ", cts_rts_enalbe='" + cts_rts_enalbe + '\'' +
                ", dma_buffer_size='" + dma_buffer_size + '\'' +
                ", uart_trans_mode='" + uart_trans_mode + '\'' +
                ", device_num='" + device_num + '\'' +
                ", ps_mode='" + ps_mode + '\'' +
                ", tx_power='" + tx_power + '\'' +
                ", keepalive_num='" + keepalive_num + '\'' +
                ", keepalive_time='" + keepalive_time + '\'' +
                ", socks_type='" + socks_type + '\'' +
                ", socks_addr='" + socks_addr + '\'' +
                ", socks_port='" + socks_port + '\'' +
                ", socks_user='" + socks_user + '\'' +
                ", socks_pass='" + socks_pass + '\'' +
                ", socks_1='" + socks_1 + '\'' +
                ", socks_2='" + socks_2 + '\'' +
                ", web_user='" + web_user + '\'' +
                ", web_pass='" + web_pass + '\'' +
                ", cld_id='" + cld_id + '\'' +
                ", cld_key='" + cld_key + '\'' +
                ", cld_apikey='" + cld_apikey + '\'' +
                ", device_name='" + device_name + '\'' +
                ", roam_val='" + roam_val + '\'' +
                ", udp_enable='" + udp_enable + '\'' +
                ", ccode='" + ccode + '\'' +
                ", reset='" + reset + '\'' +
                '}';
    }

    public Config_EWM() {
        this.reset = "Reset";
    }

    public String getWifi_mode() {
        return wifi_mode;
    }

    public void setWifi_mode(String wifi_mode) {
        this.wifi_mode = wifi_mode;
    }

    public String getWifi_ssid() {
        return wifi_ssid;
    }

    public void setWifi_ssid(String wifi_ssid) {
        this.wifi_ssid = wifi_ssid;
    }

    public String getSecurity_mode() {
        return security_mode;
    }

    public void setSecurity_mode(String security_mode) {
        this.security_mode = security_mode;
    }

    public String getWifi_key() {
        return wifi_key;
    }

    public void setWifi_key(String wifi_key) {
        this.wifi_key = wifi_key;
    }

    public String getWifi_ssid1() {
        return wifi_ssid1;
    }

    public void setWifi_ssid1(String wifi_ssid1) {
        this.wifi_ssid1 = wifi_ssid1;
    }

    public String getSecurity_mode1() {
        return security_mode1;
    }

    public void setSecurity_mode1(String security_mode1) {
        this.security_mode1 = security_mode1;
    }

    public String getWifi_key1() {
        return wifi_key1;
    }

    public void setWifi_key1(String wifi_key1) {
        this.wifi_key1 = wifi_key1;
    }

    public String getWifi_ssid2() {
        return wifi_ssid2;
    }

    public void setWifi_ssid2(String wifi_ssid2) {
        this.wifi_ssid2 = wifi_ssid2;
    }

    public String getSecurity_mode2() {
        return security_mode2;
    }

    public void setSecurity_mode2(String security_mode2) {
        this.security_mode2 = security_mode2;
    }

    public String getWifi_key2() {
        return wifi_key2;
    }

    public void setWifi_key2(String wifi_key2) {
        this.wifi_key2 = wifi_key2;
    }

    public String getWifi_ssid3() {
        return wifi_ssid3;
    }

    public void setWifi_ssid3(String wifi_ssid3) {
        this.wifi_ssid3 = wifi_ssid3;
    }

    public String getSecurity_mode3() {
        return security_mode3;
    }

    public void setSecurity_mode3(String security_mode3) {
        this.security_mode3 = security_mode3;
    }

    public String getWifi_key3() {
        return wifi_key3;
    }

    public void setWifi_key3(String wifi_key3) {
        this.wifi_key3 = wifi_key3;
    }

    public String getWifi_ssid4() {
        return wifi_ssid4;
    }

    public void setWifi_ssid4(String wifi_ssid4) {
        this.wifi_ssid4 = wifi_ssid4;
    }

    public String getSecurity_mode4() {
        return security_mode4;
    }

    public void setSecurity_mode4(String security_mode4) {
        this.security_mode4 = security_mode4;
    }

    public String getWifi_key4() {
        return wifi_key4;
    }

    public void setWifi_key4(String wifi_key4) {
        this.wifi_key4 = wifi_key4;
    }

    public String getUap_ssid() {
        return uap_ssid;
    }

    public void setUap_ssid(String uap_ssid) {
        this.uap_ssid = uap_ssid;
    }

    public String getUap_secmode() {
        return uap_secmode;
    }

    public void setUap_secmode(String uap_secmode) {
        this.uap_secmode = uap_secmode;
    }

    public String getUap_key() {
        return uap_key;
    }

    public void setUap_key(String uap_key) {
        this.uap_key = uap_key;
    }

    public String getDhcp_enalbe() {
        return dhcp_enalbe;
    }

    public void setDhcp_enalbe(String dhcp_enalbe) {
        this.dhcp_enalbe = dhcp_enalbe;
    }

    public String getLocal_ip_addr() {
        return local_ip_addr;
    }

    public void setLocal_ip_addr(String local_ip_addr) {
        this.local_ip_addr = local_ip_addr;
    }

    public String getNetmask() {
        return netmask;
    }

    public void setNetmask(String netmask) {
        this.netmask = netmask;
    }

    public String getGateway_ip_addr() {
        return gateway_ip_addr;
    }

    public void setGateway_ip_addr(String gateway_ip_addr) {
        this.gateway_ip_addr = gateway_ip_addr;
    }

    public String getDns_server() {
        return dns_server;
    }

    public void setDns_server(String dns_server) {
        this.dns_server = dns_server;
    }

    public String getMstype() {
        return mstype;
    }

    public void setMstype(String mstype) {
        this.mstype = mstype;
    }

    public String getRemote_server_mode() {
        return remote_server_mode;
    }

    public void setRemote_server_mode(String remote_server_mode) {
        this.remote_server_mode = remote_server_mode;
    }

    public String getRemote_dns() {
        return remote_dns;
    }

    public void setRemote_dns(String remote_dns) {
        this.remote_dns = remote_dns;
    }

    public String getRport() {
        return rport;
    }

    public void setRport(String rport) {
        this.rport = rport;
    }

    public String getLport() {
        return lport;
    }

    public void setLport(String lport) {
        this.lport = lport;
    }

    public String getEstype() {
        return estype;
    }

    public void setEstype(String estype) {
        this.estype = estype;
    }

    public String getEsaddr() {
        return esaddr;
    }

    public void setEsaddr(String esaddr) {
        this.esaddr = esaddr;
    }

    public String getEsrport() {
        return esrport;
    }

    public void setEsrport(String esrport) {
        this.esrport = esrport;
    }

    public String getEslport() {
        return eslport;
    }

    public void setEslport(String eslport) {
        this.eslport = eslport;
    }

    public String getBaudrate() {
        return baudrate;
    }

    public void setBaudrate(String baudrate) {
        this.baudrate = baudrate;
    }

    public String getParity() {
        return parity;
    }

    public void setParity(String parity) {
        this.parity = parity;
    }

    public String getData_length() {
        return data_length;
    }

    public void setData_length(String data_length) {
        this.data_length = data_length;
    }

    public String getStop_bits() {
        return stop_bits;
    }

    public void setStop_bits(String stop_bits) {
        this.stop_bits = stop_bits;
    }

    public String getCts_rts_enalbe() {
        return cts_rts_enalbe;
    }

    public void setCts_rts_enalbe(String cts_rts_enalbe) {
        this.cts_rts_enalbe = cts_rts_enalbe;
    }

    public String getDma_buffer_size() {
        return dma_buffer_size;
    }

    public void setDma_buffer_size(String dma_buffer_size) {
        this.dma_buffer_size = dma_buffer_size;
    }

    public String getUart_trans_mode() {
        return uart_trans_mode;
    }

    public void setUart_trans_mode(String uart_trans_mode) {
        this.uart_trans_mode = uart_trans_mode;
    }

    public String getDevice_num() {
        return device_num;
    }

    public void setDevice_num(String device_num) {
        this.device_num = device_num;
    }

    public String getPs_mode() {
        return ps_mode;
    }

    public void setPs_mode(String ps_mode) {
        this.ps_mode = ps_mode;
    }

    public String getTx_power() {
        return tx_power;
    }

    public void setTx_power(String tx_power) {
        this.tx_power = tx_power;
    }

    public String getKeepalive_num() {
        return keepalive_num;
    }

    public void setKeepalive_num(String keepalive_num) {
        this.keepalive_num = keepalive_num;
    }

    public String getKeepalive_time() {
        return keepalive_time;
    }

    public void setKeepalive_time(String keepalive_time) {
        this.keepalive_time = keepalive_time;
    }

    public String getSocks_type() {
        return socks_type;
    }

    public void setSocks_type(String socks_type) {
        this.socks_type = socks_type;
    }

    public String getSocks_addr() {
        return socks_addr;
    }

    public void setSocks_addr(String socks_addr) {
        this.socks_addr = socks_addr;
    }

    public String getSocks_port() {
        return socks_port;
    }

    public void setSocks_port(String socks_port) {
        this.socks_port = socks_port;
    }

    public String getSocks_user() {
        return socks_user;
    }

    public void setSocks_user(String socks_user) {
        this.socks_user = socks_user;
    }

    public String getSocks_pass() {
        return socks_pass;
    }

    public void setSocks_pass(String socks_pass) {
        this.socks_pass = socks_pass;
    }

    public String getSocks_1() {
        return socks_1;
    }

    public void setSocks_1(String socks_1) {
        this.socks_1 = socks_1;
    }

    public String getSocks_2() {
        return socks_2;
    }

    public void setSocks_2(String socks_2) {
        this.socks_2 = socks_2;
    }

    public String getWeb_user() {
        return web_user;
    }

    public void setWeb_user(String web_user) {
        this.web_user = web_user;
    }

    public String getWeb_pass() {
        return web_pass;
    }

    public void setWeb_pass(String web_pass) {
        this.web_pass = web_pass;
    }

    public String getCld_id() {
        return cld_id;
    }

    public void setCld_id(String cld_id) {
        this.cld_id = cld_id;
    }

    public String getCld_key() {
        return cld_key;
    }

    public void setCld_key(String cld_key) {
        this.cld_key = cld_key;
    }

    public String getCld_apikey() {
        return cld_apikey;
    }

    public void setCld_apikey(String cld_apikey) {
        this.cld_apikey = cld_apikey;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getRoam_val() {
        return roam_val;
    }

    public void setRoam_val(String roam_val) {
        this.roam_val = roam_val;
    }

    public String getUdp_enable() {
        return udp_enable;
    }

    public void setUdp_enable(String udp_enable) {
        this.udp_enable = udp_enable;
    }

    public String getCcode() {
        return ccode;
    }

    public void setCcode(String ccode) {
        this.ccode = ccode;
    }

    public String getReset() {
        return reset;
    }

    public void setReset(String reset) {
        this.reset = reset;
    }
}
