package com.triplet.tictactoe;

public class RankingData {
    private int rank;
    private String name;
    private int totalScore;

    public RankingData(int rank, String name, int totalScore) {
        this.rank = rank;
        this.name = name;
        this.totalScore = totalScore;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}