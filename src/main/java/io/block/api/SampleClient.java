package io.block.api;

import io.block.api.client.WalletAPI;
import io.block.api.com.HttpPostCommunicator;
import io.block.api.client.WalletClient;
import io.block.api.model.AccountBalance;
import io.block.api.model.Price;
import io.block.api.model.Prices;
import io.block.api.model.Withdrawal;
import io.block.api.utils.BlockIOException;

import java.util.Date;

public class SampleClient {
    public static void main(String [ ] args)
    {
        WalletAPI api = new WalletClient("!!! YOUR API KEY HERE !!!", new HttpPostCommunicator());

        try {
            System.out.println();

            if (!api.validateKey()) {
                System.out.println("Could not validate api key");
                System.exit(1);
            }

            AccountBalance balance = api.getAccountBalance();
            System.out.println("Balance for account " + balance.network
                    + ": Confirmed: " + balance.availableBalance
                    + " Pending: " + balance.pendingReceivedBalance);
            System.out.println();

            Prices prices = api.getPrices("");
            for (Price price : prices.prices) {
                System.out.println("Price for " + prices.network
                                + " in " + price.priceBase
                                + " on " + price.exchange
                                + " is " + price.price
                                + " as of " + new Date(price.time * 1000).toString());
            }
            System.out.println();

            Withdrawal withdrawal = api.withdrawToAddress("A7SvospFu3jG84LX2rTUuTFNHs7efZna3Y", 50d, "!!! YOUR SECRET PIN HERE !!!");
            System.out.println("Withdrawal done. Transaction ID: " + withdrawal.txid
                + " Amount withdrawn: " + withdrawal.amountWithdrawn
                + " Amount sent: " + withdrawal.amountSent
                + " Network fee: " + withdrawal.networkFee
                + " Block.io fee: " + withdrawal.blockIOFee);
            System.out.println();
        } catch (BlockIOException e) {
            e.printStackTrace();
        }
    }
}
