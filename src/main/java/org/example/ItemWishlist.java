package org.example;

public class ItemWishlist {
    private int id;
    private String nome;
    private double preco;
    private String prioridade;
    private boolean comprado;
    private String link;
    private int categoriaId;


    public ItemWishlist(String nome, double preco, String prioridade, int categoriaId) {
        this.nome = nome;
        this.preco = preco;
        this.prioridade = prioridade;
        this.categoriaId = categoriaId;
        this.comprado = false;
    }

    public void marcarComoComprado() {
        this.comprado = true;
    }
    public String getNome(){
        return nome;
    }
    public double getPreco(){
        return preco;
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

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public void setComprado(boolean comprado) {
        this.comprado = comprado;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public boolean isComprado() {
        return comprado;
    }

}

