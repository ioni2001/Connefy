package com.example.finalproject.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Networking{

    private Iterable<Friendship> friendships;
    private Long[][] friendshipMatrix;
    private List<Long> nodes;
    private Long size;

    public Networking(Iterable<Friendship> friendships, Long size, List<Long> nodes){
        this.friendships = friendships;
        this.size = size;
        this.nodes = nodes;
        this.friendshipMatrix = new Long[105][105];
        for(int i = 0; i < 105; i++)
            for(int j = 0; j < 105; j++)
                friendshipMatrix[i][j] = 0L;
    }

    /**
     * Populate the friendship matrix with 0 if the friendship exists, or 1 otherwise
     * */

    public void setFriendships(){
        for(Friendship friendship:friendships){
            friendshipMatrix[Math.toIntExact(friendship.getTuple().getLeft())][Math.toIntExact(friendship.getTuple().getRight())] = 1L;
            friendshipMatrix[Math.toIntExact(friendship.getTuple().getRight())][Math.toIntExact(friendship.getTuple().getLeft())] = 1L;
        }
    }

    public void DFS(boolean[] visited, int node){
        visited[node] = true;
        for(Long nodeCurent:nodes){
            if(friendshipMatrix[node][Math.toIntExact(nodeCurent)] == 1L && !visited[Math.toIntExact(nodeCurent)])
                DFS(visited, Math.toIntExact(nodeCurent));
        }
    }
    public void DFS2(boolean[] visited, int node, List<Long>intermidiate){
        visited[node] = true;
        for(Long nodeCurent:nodes){
            if(friendshipMatrix[node][Math.toIntExact(nodeCurent)] == 1L && !visited[Math.toIntExact(nodeCurent)]) {
                intermidiate.add(nodeCurent);
                DFS2(visited, Math.toIntExact(nodeCurent), intermidiate);
            }
        }
    }

    /**
     * Calculates the number of comunities using a DFS algorithm
     * @return integer, number of comunities
     * */

    public int numberOfCommunities(){
        boolean visited[] = new boolean[Math.toIntExact(this.size)];
        for(Long node:nodes)
            visited[Math.toIntExact(node)] = false;
        int res = 0;
        for(Long node:nodes)
            if(!visited[Math.toIntExact(node)]){
                res++;
                DFS(visited, Math.toIntExact(node));
            }
        return res;
    }

    /**
     * Calculates the most sociable community
     * @return List of long that represents the ids of the users in the most sociable community
     * */

    public List<Long> mostSociableCommunity(){
        boolean visited[] = new boolean[Math.toIntExact(this.size)];
        List<Long> rez = new ArrayList<>();
        for(Long node:nodes)
            visited[Math.toIntExact(node)] = false;
        int Max = 0;
        for(Long node:nodes)
            if(!visited[Math.toIntExact(node)]){
                List<Long> intermediate = new ArrayList<>();
                intermediate.add(node);
                DFS2(visited, Math.toIntExact(node), intermediate);
                if(Max < intermediate.size()) {
                    Max = intermediate.size();
                    rez = List.copyOf(intermediate);
                }
            }
        return rez;
    }
}
