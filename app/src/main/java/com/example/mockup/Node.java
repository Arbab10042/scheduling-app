package com.example.mockup;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

public class Node implements Serializable {
    private Integer node_id;    //auto increment in database
    private String title;
    private String time;    //Monday-10:10
    private Integer current_episode;
    private Integer total_episodes;
    private Integer status;  //1 -> Watching, 2 -> On-Hold, 3 -> Plan To Watch
    private Integer bookmarked; // 0-> no, 1-> yes

    private transient INodeDAO dao = null;

    public Node(INodeDAO dao){
        this.dao = dao;
    }

    public void setDAO(INodeDAO dao){
        this.dao = dao;
    }

    public void save(){
        if(dao != null){
            dao.saveNode(this);
        }else{
            Log.d("MockError","DAO not in Node.");
        }
    }

    public void load(Hashtable<String,String> obj){
        makeEntry(Integer.parseInt(obj.get("node_id")),obj.get("title"),obj.get("time"),Integer.parseInt(obj.get("current_episode")),
                Integer.parseInt(obj.get("total_episodes")),Integer.parseInt(obj.get("status")),Integer.parseInt(obj.get("bookmarked")));
    }

    public Node(Integer node_id, String title, String time, Integer current_episode, Integer total_episodes, Integer status, Integer bookmarked) {
        makeEntry(node_id,title,time,current_episode,total_episodes,status,bookmarked);
    }

    public void makeEntry(Integer node_id, String title, String time, Integer current_episode, Integer total_episodes, Integer status, Integer bookmarked){
        this.node_id = node_id;
        this.title = title;
        this.time = time;
        this.current_episode = current_episode;
        this.total_episodes = total_episodes;
        this.status = status;
        this.bookmarked = bookmarked;
    }


    public Integer getNode_id() {
        return node_id;
    }

    public void setNode_id(Integer node_id) {
        this.node_id = node_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public String getTimeFormat() {
        String[] split = getTime().split("-", 2);
        return split[0] + " at " + split[1];
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getCurrent_episode() {
        return current_episode;
    }

    public void setCurrent_episode(Integer current_episode) {
        this.current_episode = current_episode;
    }

    public Integer getTotal_episodes() {
        return total_episodes;
    }

    public void setTotal_episodes(Integer total_episodes) {
        this.total_episodes = total_episodes;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusFormat() {
        if(status == 1){
            return "Watching";
        }else if(status == 2){
            return "On-Hold";
        }else if(status == 3){
            return "Plan To Watch";
        }else {
            return "N/A";
        }
    }

    public String getEpisodesCountFormat(){
        return "Episode " + getCurrent_episode().toString() + "/" + getTotal_episodes().toString();
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(Integer bookmarked) {
        this.bookmarked = bookmarked;
    }


    public void setTimeHoursMinutes(String s){
        String[] split = getTime().split("-", 2);
        setTime(split[0] + "-" + s);
    }

    public void setTimeDay(String s){
        String[] split = getTime().split("-", 2);
        setTime(s + "-" + split[1]);
    }

    public String getTimeDay(){
        String[] split = getTime().split("-", 2);
        return split[0];
    }

    public static ArrayList<Node> getAllNodes(INodeDAO dao){
        ArrayList<Node> nodes = new ArrayList<Node>();
        if(dao != null){
            ArrayList<Hashtable<String,String>> objects = dao.getAllNodes();
            for(Hashtable<String,String> obj : objects){
                Node node = new Node(dao);
                node.load(obj);
                nodes.add(node);
            }
        }
        return nodes;
    }
    public static ArrayList<Node> getTodayNodes(INodeDAO dao){
        ArrayList<Node> nodes = new ArrayList<Node>();
        if(dao != null){
            ArrayList<Hashtable<String,String>> objects = dao.getTodayNodes();
            for(Hashtable<String,String> obj : objects){
                Node node = new Node(dao);
                node.load(obj);
                nodes.add(node);
            }
        }
        return nodes;
    }
    public static ArrayList<Node> getBookmarkedNodes(INodeDAO dao){
        ArrayList<Node> nodes = new ArrayList<Node>();
        if(dao != null){
            ArrayList<Hashtable<String,String>> objects = dao.getBookmarkedNodes();
            for(Hashtable<String,String> obj : objects){
                Node node = new Node(dao);
                node.load(obj);
                nodes.add(node);
            }
        }
        return nodes;
    }
}
