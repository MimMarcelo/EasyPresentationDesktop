/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mimmarcelo.classes;

/**
 *
 * @author Marcelo Júnior
 */
public interface IBluetooth {
    public void recebeMensagem(String msg);
    
    public void recebeStatus(String status);
}
