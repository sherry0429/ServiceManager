//this Service center is client,appapi is server.
service LoadBalanceInterface {
    string requestServiceSituation(1: string requestType);
}
