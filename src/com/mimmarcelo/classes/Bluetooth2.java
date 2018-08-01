/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mimmarcelo.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public final class Bluetooth2 implements DiscoveryListener{
    
    //CONSTANTES
    private final Object ESPERA = new Object();
    
    //PROPRIEDADES
    private String urlDeConexao;
    private IBluetooth tela;
    private DiscoveryAgent antena;
    private StreamConnection conexao;
    private OutputStream output;
    private RemoteDevice dispositivoSelecionado;
    private List<RemoteDevice> listaDeDispositivos;

    public Bluetooth2(IBluetooth tela) {
        this.tela = tela;
        this.tela.recebeStatus("Carregando componentes do Bluetooth");
        listaDeDispositivos = new ArrayList<>();
        try {
            antena = LocalDevice.getLocalDevice().getDiscoveryAgent();
            atualizarListaDeDispositivos();
            this.tela.recebeStatus("Componentes carregados com sucesso");
        } catch (BluetoothStateException ex) {
            this.tela.recebeStatus(ex.getMessage());
            this.tela.recebeStatus("Erro, a antena do dispositivo não foi encontrada.");
            this.tela.recebeStatus("Verifique se o Bluetooth está ligado!");
        }
    }

    public boolean isFuncionando() {
        return (antena != null);
    }
    
    public List<String> getListaDeDispositivos() {
        this.tela.recebeStatus("Obtendo a lista de dispositivos");
        List<String> lista = new ArrayList<>();

        try {
            for(RemoteDevice r: this.listaDeDispositivos){
                lista.add(r.getFriendlyName(false).trim());
            }
        }catch(IOException ex){
            this.tela.recebeStatus("Erro ao obter a lista de dispositivos:\n"+ex.getMessage());
        }
        return lista;
    }

    public RemoteDevice getDispositivoSelecionado() {
        try {
            this.tela.recebeStatus("Obtendo dispositivo: "+dispositivoSelecionado.getFriendlyName(false));
        } catch (IOException ex) {
            this.tela.recebeStatus("Erro ao tentar obter o dispositivo selecionado");
        }
        return dispositivoSelecionado;
    }

    public boolean setDispositivoSelecionado(int indice) {
        this.dispositivoSelecionado = listaDeDispositivos.get(indice);
        try {
            this.tela.recebeStatus("Dispositivo: "+dispositivoSelecionado.getFriendlyName(false)+" selecionado com sucesso");
        } catch (IOException ex) {
            this.tela.recebeStatus("Erro ao tentar selecionar o dispositivo");
            return false;
        }        
        return true;
    }
    
    public boolean atualizarListaDeDispositivos(){
        //this.tela.recebeStatus("Carregando dispositivos pareados");
        listaDeDispositivos.clear();
        try{
        listaDeDispositivos = Arrays.asList(antena.retrieveDevices(DiscoveryAgent.PREKNOWN));
        this.tela.recebeStatus("Lista de dispositivos carregada com sucesso");
        }
        catch(NullPointerException ex){
            this.tela.recebeStatus("Clique em atualizar lista!");
        }
        return true;
    }
    
    public boolean atualizarListaDeDispositivos(boolean apenasPareados){
        if(apenasPareados) return atualizarListaDeDispositivos();
        
        listaDeDispositivos.clear();
        synchronized(ESPERA){
            try{
                if(antena.startInquiry(DiscoveryAgent.GIAC, this)){
                    ESPERA.wait();
                    
                }
            } catch(BluetoothStateException ex){
                this.tela.recebeStatus("Erro no bluetooth:\n"+ex.getMessage());
                return false;
            } catch(InterruptedException ex){
                this.tela.recebeStatus("Busca interrompida:\n"+ex.getMessage());
                return false;
            }
        }
        return true;
    }
    
    public void esperarMensagens(){
        InputStream in = null;
        try {
            this.tela.recebeStatus("Preparando para receber mensagens de '"+dispositivoSelecionado.getFriendlyName(false)+"'");
            in = conexao.openInputStream();
            BufferedReader receptor = new BufferedReader(new InputStreamReader(in));
            this.tela.recebeStatus("Pronto para receber mensagens de '"+dispositivoSelecionado.getFriendlyName(false)+"'");
            String msg;
            while((msg = receptor.readLine()) != null){
                this.tela.recebeStatus("Recebido: '"+msg+"'");
                this.tela.recebeMensagem(msg);
            }
        } catch (IOException ex) {
            this.tela.recebeStatus("Erro, dispositivo indisponível:\n"+ex.getMessage());
        } finally {
            try {
                if(in != null) in.close();
            } catch (IOException ex) {
                this.tela.recebeStatus("Erro, conexão interrompida:\n"+ex.getMessage());
            }
        }
    }
    
    public boolean enviarMensagem(String mensagem){
        try{
            if(output == null){
                output = conexao.openOutputStream();
            }
            this.tela.recebeStatus("Preparando para enviar mensagem para '"+dispositivoSelecionado.getFriendlyName(false)+"'");
            byte[] msg = mensagem.getBytes();
            output.write(msg);
            output.flush();
        } catch (IOException ex) {
            this.tela.recebeStatus("Erro, dispositivo indisponível:\n"+ex.getMessage());
        }
        return true;
    }
    
    public boolean conectar(){        
        try {
            this.tela.recebeStatus("Estabelecendo conexão com: "+dispositivoSelecionado.getFriendlyName(false));
            UUID[] uuid = new UUID[1];
            uuid[0] = new UUID("1101", true);
            antena.searchServices(null, uuid, dispositivoSelecionado, this);
            
            synchronized(ESPERA){
                ESPERA.wait();
            }
            
            if(urlDeConexao == null){
                this.tela.recebeStatus("O dispositivo selecionado não suporta o serviço de conexão solicitado:\n"+uuid[0].toString());
                return false;
            }
            else{
                conexao = (StreamConnection)Connector.open(urlDeConexao);
                this.tela.recebeStatus("Conectado!");
            }
        } catch (BluetoothStateException ex) {
            this.tela.recebeStatus("Erro ao tentar estabelecer conexão:\n"+ex.getMessage());
            return false;
        } catch (IOException ex) {
            this.tela.recebeStatus("Erro ao tentar obter o dispositivo selecionado");
            return false;
        } catch (InterruptedException ex) {
            this.tela.recebeStatus("Processo de conexão interrompido:\n"+ex.getMessage());
            return false;
        }
        return true;
    }
    
    public boolean desconectar(){
        try{
            conexao.close();
        } catch(IOException ex){
            this.tela.recebeStatus("Erro na tentativa de desconexão:\n"+ex.getMessage());
            return false;
        }
        return true;
    }
    
    @Override
    public void deviceDiscovered(RemoteDevice dispositivoEncontrado, DeviceClass dc) {
        if(!listaDeDispositivos.contains(dispositivoEncontrado)){
            listaDeDispositivos.add(dispositivoEncontrado);
        }
    }

    @Override
    public void servicesDiscovered(int i, ServiceRecord[] srs) {
        if(srs!=null && srs.length>0){
            urlDeConexao=srs[0].getConnectionURL(0, false);
        }
        synchronized(ESPERA){
            ESPERA.notifyAll();
        }
    }

    @Override
    public void serviceSearchCompleted(int i, int i1) {
    }

    @Override
    public void inquiryCompleted(int i) {
        synchronized(ESPERA){
            ESPERA.notifyAll();
        }
    }

}
