package io.block.api.model;

import com.google.gson.annotations.SerializedName;

public class AccountBalance {
    public String network;

    @SerializedName("available_balance")
    public String availableBalance;

    @SerializedName("pending_received_balance")
    public String pendingReceivedBalance;
}
