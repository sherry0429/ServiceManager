namespace java servicemanager.thrift
namespace py thrift
service LoadBalanceInterface {
    string requestServiceSituation(1: string requestType);
}
