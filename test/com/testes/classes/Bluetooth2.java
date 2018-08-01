/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.testes.classes;

import com.mimmarcelo.classes.IBluetooth;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author Marcelo Júnior
 */
public class Bluetooth2 extends TestCase{
    @Test
    public void criarObjetoDeConexao(){
        com.mimmarcelo.classes.Bluetooth2 b = new com.mimmarcelo.classes.Bluetooth2(new IBluetooth() {
            @Override
            public void recebeMensagem(String msg) {
                System.out.println("Enviado: "+msg);
            }

            @Override
            public void recebeStatus(String status) {
                System.out.println("Status: "+status);
            }
        });
        assertEquals(b.isFuncionando(), true);
    }
}
