package no.ntnu.bachelor.voicepick.pojos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseInviteCode extends TokenObject {
    private final Long warehouseId;

    public WarehouseInviteCode(Long warehouseId, String token) {
        super(token);

        this.warehouseId = warehouseId;
    }
}
