package io.block.api.model;


import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("user_id")
    public int userID;

    public String address;
    public String label;

    @SerializedName("available_balance")
    public String availableBalance;

    @SerializedName("pending_received_balance")
    public String pendingReceivedBalance;
}
