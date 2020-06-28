package com.blockchain.block;

import com.blockchain.model.Block;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class BlockTest {
    @Test
    public void testLombok(){
        val block = new Block();
        String hash = block.getHash();
        System.out.println(hash);
        assertThrows(NullPointerException.class, () -> block.setNonce(null));
    }
}
