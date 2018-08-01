/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mimmarcelo.classes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author Marcelo Júnior
 */
public class M {
    public static class cor{
        public static final Color FUNDO_PADRAO = Color.WHITE;
        public static final Color FUNDO_SECUNDARIO = new Color(35, 35, 110);
        public static final Color TEXTO_SECUNDARIO = Color.WHITE;
    }
    
    public static class tamanho{
        public static final int TELA_ALTURA = 390;
        public static final int TELA_LARGURA = 600;
        public static final int LISTA_ALTURA = 200;
        public static final int LISTA_LARGURA = 200;
        public static final int LISTA_SCROLL_ALTURA = LISTA_ALTURA+22;
        public static final int LISTA_SCROLL_LARGURA = LISTA_LARGURA+22;
        public static final int PAINEL_ALTURA = 140;
        public static final int PAINEL_HISTORICO_ALTURA = 130;
        public static final int PAINEL_LARGURA = 340;
        public static final int TAMANHO_FONTE_TITULO = 20;
        public static final int TAMANHO_FONTE_NORMAL = 18;
    }
    
    public static class img{
        public static final Image LOGOMARCA = new ImageIcon(M.class.getResource("/com/mimmarcelo/img/logo150.png")).getImage();
    }
    
    public static class txt{
        public static final String EASY_PRESENTATION = "Easy Presentation";
        public static final String DISPOSITIVOS_PAREADOS = "Dispositivos pareados";
        public static final String DISPOSITIVO_SELECIONADO = "Dispositivo selecionado";
        public static final String HISTORICO = "Histórico";
        public static final String NOME = "Nome:";
        public static final String MAC = "MAC:";
        public static final String STATUS = "Status:";
        public static final String CONECTAR = "Conectar";
        public static final String NENHUM_DISPOSITIVO_SELECIONADO = "Nenhum dispositivo selecionado...";
        public static final String ATUALIZAR_LISTA = "Atualizar lista";
        public static final String EASY_PRESENTATION_INICIADO = "Easy Presentation iniciado";
        public static final String RETICENCIAS = "...";
        public static final String NOME_FONTE = "Tahoma";
        public static final String DESCONECTAR = "Desconectar";
        public static final String CONEXAO_ESTABELECIDA = "Conexão estabelecida";
        public static final String CONEXAO_ENCERRADA = "Conexão encerrada";
        public static final String LISTA_DE_DISPOSITIVOS_ATUALIZADA = "Lista de dispositivos atualizada";
        public static final String PRONTO_PARA_CONEXAO = "Pronto para conexão";
        public static final String ERRO_AO_TENTAR_CONEXAO = "Erro ao tentar conexão";
        public static final String ERRO_AO_TENTAR_DESCONEXAO = "Erro ao tentar desconexão";
        public static final String ERRO_AO_TENTAR_ATUALIZAR_LISTA = "Erro ao tentar atualizar lista";
    }
    
    public static class fonte{
        public static final Font TITULO = new Font(txt.NOME_FONTE, Font.BOLD, tamanho.TAMANHO_FONTE_TITULO);
        public static final Font NORMAL = new Font(txt.NOME_FONTE, Font.PLAIN, tamanho.TAMANHO_FONTE_NORMAL);
    }
}
