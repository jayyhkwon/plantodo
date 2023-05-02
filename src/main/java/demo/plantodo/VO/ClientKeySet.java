package demo.plantodo.VO;

import lombok.Getter;

@Getter
public class ClientKeySet {
    private long clientId;
    private String lockKey;
    private String lockVal;

    public ClientKeySet(long clientId, String lockKey, String lockVal) {
        this.clientId = clientId;
        this.lockKey = lockKey;
        this.lockVal = lockVal;
    }
}
