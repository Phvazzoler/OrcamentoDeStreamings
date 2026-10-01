package org.example;

public class Assinatura {
    private int id ;
    private String nome;
    private double valor;
    private String cicloCobranca;
    private int diaCobranca;
    private boolean ativa;
    private int categoriaId;

    public Assinatura(String nome, double valor, String cicloCobranca, int diaCobranca, int categoriaId) {
        this.nome = nome;
        this.valor = valor;
        this.cicloCobranca = cicloCobranca;
        this.diaCobranca = diaCobranca;
        this.categoriaId = categoriaId;
        this.ativa = true;
    }
    public double getValorMensal() {
        return cicloCobranca.equals("Anual") ? valor / 12 : valor;
    }
    public String getNome() {
        return nome;
    }
    public double getValor(){
        return valor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getCicloCobranca() {
        return cicloCobranca;
    }

    public void setCicloCobranca(String cicloCobranca) {
        this.cicloCobranca = cicloCobranca;
    }

    public int getDiaCobranca() {
        return diaCobranca;
    }

    public void setDiaCobranca(int diaCobranca) {
        this.diaCobranca = diaCobranca;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }
}
