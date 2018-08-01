/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mimmarcelo.classes;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

/**
 *
 * @author Marcelo Júnior
 */
public class Bluetooth implements DiscoveryListener{

    //CONSTANTES
    private final Object EVENTO_DE_ESPERA = new Object();
    
    private static ArrayList<RemoteDevice> dispositivos;
    private static ArrayList<String> nomeDispositivos;
    private static RemoteDevice dispositivoSelecionado;
    StreamConnection streamConnection;
    private static String urlDeConexao = null;

    public Bluetooth() {
        dispositivos = new ArrayList<>();
        nomeDispositivos = new ArrayList<>();
    }
    
    public ArrayList<RemoteDevice> getDispositivos(){
        if(dispositivos.isEmpty()){
            dispositivos = new ArrayList<>();
            RemoteDevice[] rdList = null;
            try {
                rdList = LocalDevice.getLocalDevice().getDiscoveryAgent().retrieveDevices(DiscoveryAgent.PREKNOWN);
            } catch (BluetoothStateException ex) {
                Logger.getLogger(Bluetooth.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(RemoteDevice d: rdList){
                dispositivos.add(d);
            }
        }
        return dispositivos;
    }
    
    public ArrayList<String> getNomeDispositivos(){
        if(nomeDispositivos == null){
            nomeDispositivos = new ArrayList<>();
        }
        for(RemoteDevice a: dispositivos){
            try {
                nomeDispositivos.add(a.getFriendlyName(false).trim());
            } catch (IOException ex) {
                Logger.getLogger(Bluetooth.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return nomeDispositivos;
    }
    
    public static RemoteDevice getDispositivoSelecionado(){
        return dispositivoSelecionado;
    }

    public static void setDispositivoSelecionado(int indice){
        dispositivoSelecionado = dispositivos.get(indice);
    }
    
    public ArrayList<RemoteDevice> buscarDispositivos(){
        dispositivos.clear();
        
        synchronized(EVENTO_DE_ESPERA){
            try {
                boolean iniciado = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this);
                if(iniciado){
                    EVENTO_DE_ESPERA.wait();
                }
            } catch (BluetoothStateException ex) {
                Logger.getLogger(Bluetooth.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Bluetooth.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dispositivos;
    }
    
    public void receberMensagens(){
        InputStream inStream = null;
        try {
            //LÊ RESPOSTA
            inStream = streamConnection.openInputStream();
            BufferedReader bReader2 = new BufferedReader(new InputStreamReader(inStream));
            String lineRead = null;
            while((lineRead = bReader2.readLine()) != null){
                System.out.println(lineRead);
                Robot robo = new Robot();
                if(lineRead.endsWith("b")){
                    robo.keyPress(KeyEvent.VK_DOWN);
                }
                else{
                    robo.keyPress(KeyEvent.VK_UP);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Bluetooth.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AWTException ex) {
            Logger.getLogger(Bluetooth.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Bluetooth.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    public String conectar(){
        System.out.println("Estabelecendo conexão...");
        try {
            UUID[] uuidSet = new UUID[1];
            uuidSet[0] = new UUID("1101", true);
            LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(null, uuidSet, getDispositivoSelecionado(), this);
            synchronized(EVENTO_DE_ESPERA){
                EVENTO_DE_ESPERA.wait();
            }
            if(urlDeConexao==null){
               return "O dispositivo não suporta o serviço SPP simples!";
                //System.exit(0);
            }
            
            //CONECTA O SERVIDOR (SMARTPHONE) E ENVIA UMA LINHA DE TEXTO
            streamConnection = (StreamConnection)Connector.open(urlDeConexao);
        }catch (BluetoothStateException ex) {
            Logger.getLogger(Bluetooth.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println(Bluetooth.class.getName()+": "+ ex.getMessage());
            return "Erro ao estabelecer a conexão";
        } catch (InterruptedException ex) {
            Logger.getLogger(Bluetooth.class.getName()).log(Level.SEVERE, null, ex);
        }
        receberMensagens();
        return "Conectado!";
    }
    
    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass dc) {
        if(!dispositivos.contains(btDevice)){
            dispositivos.add(btDevice);
        }
    }

    @Override
    public void servicesDiscovered(int i, ServiceRecord[] srs) {
        if(srs!=null && srs.length>0){
            for(ServiceRecord servico: srs){
                System.out.println("Serviço "+(i+1)+": "+servico.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
            }
            urlDeConexao=srs[0].getConnectionURL(0,false);
        }
        synchronized(EVENTO_DE_ESPERA){
            EVENTO_DE_ESPERA.notifyAll();
        }
    }

    @Override
    public void serviceSearchCompleted(int i, int i1) {
    }

    @Override
    public void inquiryCompleted(int i) {
        synchronized(EVENTO_DE_ESPERA){
            EVENTO_DE_ESPERA.notifyAll();
        }
    }
    
}
