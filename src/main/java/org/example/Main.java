package org.example;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        AssinaturaDAO assinaturaDAO = new AssinaturaDAO();
        double total = 0;

        for (Assinatura a : assinaturaDAO.listar()) {
            System.out.println(a.getNome() + " - " + a.getValorMensal());
            if (a.isAtiva()) {
                total += a.getValorMensal();
            }
        }
        System.out.printf("Total mensal: %.2f%n", total);

        double limite = new LimiteDAO().buscarLimite();
        if (total > limite) {
            System.out.printf("ALERTA! Gasto %.2f passou do limite %.2f%n", total, limite);
        } else {
            System.out.printf("Dentro do limite (%.2f de %.2f)%n", total, limite);
        }

        for (ItemWishlist item : new ItemWishlistDAO().listarPendentes()) {
            System.out.println(item.getNome() + " - " + item.getPreco() + " - " + item.getPrioridade());
        }
    }
}