package io.infinitypools.snapshotter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetAddress {

    private String address;
    private String quantity;
    private String assetId;

    public AssetAddress(com.bloxbean.cardano.client.backend.model.AssetAddress assetAddress, String assetId) {
        this.address = assetAddress.getAddress();
        this.quantity = assetAddress.getQuantity();
        this.assetId = assetId;
    }
}
