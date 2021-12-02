package com.example.mockup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivityFragment extends Fragment {

    private ArrayList<Node> nodeList = null;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private NodeDBDAO dao;
    private String fragmentType = "null";

    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("fragmentType", fragmentType);
        //outState.putSerializable("nodeList",nodeList);
        Log.d("fragLog","Fragment " + fragmentType.toString() + ": savedInstanceState called");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null){
            fragmentType = savedInstanceState.getString("fragmentType");
            //nodeList = (ArrayList<Node>) savedInstanceState.getSerializable("nodeList");
        }

        dao = new NodeDBDAO(getContext());
        switch (fragmentType) {
            case "Favourites":
                nodeList = Node.getBookmarkedNodes(dao);
                break;
            case "All":
                nodeList = Node.getAllNodes(dao);
                break;
            case "Today":
                nodeList = Node.getTodayNodes(dao);
                break;
            default:
                break;
        }

        recyclerAdapter = new RecyclerAdapter(nodeList);
        recyclerView.setAdapter(recyclerAdapter);

        Log.d("fragLog","Fragment " + fragmentType + ": onActivityCreated called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main_fragment, container, false);

        fragmentType = getArguments().getString("fragmentType", "Today");

        recyclerView = (RecyclerView) view.findViewById(R.id.home_fragment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        Log.d("fragLog","Fragment " + fragmentType + ": onCreateView called");
        return view;
    }


    public boolean hasError(Node node){
        return hasError(node,"none");
    };

    public boolean hasError(Node node, String tag){
        boolean hasError;
        if(nodeList == null){
            hasError = true;
            Log.d("fragLogError","Fragment " + fragmentType.toString() + ", Tag "+ tag +": nodeList empty");
        }else if(node == null){
            hasError = true;
            Log.d("fragLogError","Fragment " + fragmentType.toString() + ", Tag "+ tag +": node empty");
        }else {
            hasError = false;
        }
        return hasError;
    }

    public void insertNode(Node node){
        if(!hasError(node,"insert")){
            nodeList.add(node);
            recyclerAdapter.updateNodeList(nodeList);
        }
    }
    public void deleteNode(Node node){
        if(!hasError(node,"delete")){
            ArrayList<Node> nodeList2 = new ArrayList<Node>();
            for (Node n : nodeList) {
                if (!n.getNode_id().equals(node.getNode_id())) {
                    nodeList2.add(n);
                }
            }
            nodeList = nodeList2;
            recyclerAdapter.updateNodeList(nodeList);
        }
    }

    public void editNode(Node node){
        if(!hasError(node,"edit")){
            ArrayList<Node> nodeList2 = new ArrayList<Node>();
            for (Node n : nodeList) {
                if (!n.getNode_id().equals(node.getNode_id())) {
                    nodeList2.add(n);
                } else {
                    nodeList2.add(node);
                }
            }
            nodeList = nodeList2;
            recyclerAdapter.updateNodeList(nodeList);
        }
    }

}
