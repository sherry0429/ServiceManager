package servicemanager.model;

import java.util.ArrayList;

/**
 * Author:TianYouPan
 * Time:2017/6/2
 * Description:
 */
public class TestParams {

    public static int thrift_connections = 0;
    public static int thrift_call = 0;
    public static int nodes_number = 0;
    public static int services_number = 0;
    public static int groups_number = 0;
    public static String[] services_list = null;
    public static String[] node_list = null;

    public static int getGroups_number() {
        return groups_number;
    }

    public static int getNodes_number() {
        return nodes_number;
    }

    public static int getServices_number() {
        return services_number;
    }

    public static int getThrift_call() {
        return thrift_call;
    }

    public static int getThrift_connections() {
        return thrift_connections;
    }

    public static void setGroups_number(int groups_number) {
        TestParams.groups_number = groups_number;
    }

    public static void setNodes_number(int nodes_number) {
        TestParams.nodes_number = nodes_number;
    }

    public static void setServices_number(int services_number) {
        TestParams.services_number = services_number;
    }

    public static void setThrift_call(int thrift_call) {
        TestParams.thrift_call = thrift_call;
    }

    public static void setThrift_connections(int thrift_connections) {
        TestParams.thrift_connections = thrift_connections;
    }

    public static void setServices_list(String[] services_list) {
        TestParams.services_list = services_list;
    }

    public static void setNode_list(String[] node_list) {
        TestParams.node_list = node_list;
    }

    public static String[] getNode_list() {
        return node_list;
    }

    public static String[] getServices_list() {
        return services_list;
    }
}
