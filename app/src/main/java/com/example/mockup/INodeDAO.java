package com.example.mockup;

import java.util.ArrayList;
import java.util.Hashtable;

public interface INodeDAO {
    public void insertNode(Node node);
    public void saveNode(Node node);
    public void deleteNode(Node node);
    public int getMaxID();
    public ArrayList<Hashtable<String,String>> getTodayNodes();
    public ArrayList<Hashtable<String,String>> getBookmarkedNodes();
    public ArrayList<Hashtable<String,String>> getAllNodes();
    public void insertXMLData(String xmlData);
    public void clearDatabase();
}
