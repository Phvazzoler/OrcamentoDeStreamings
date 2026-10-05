package org.example;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Assinatura crunchyroll = new Assinatura("Crunchyroll", 239.88, "ANUAL", 20, 1);
        System.out.println(crunchyroll.getValorMensal());

        ItemWishlist eldenRing = new ItemWishlist("Elden Ring", 199.90, "ALTA", 2);
        eldenRing.marcarComoComprado();
        System.out.println(eldenRing.isComprado());

        try (Connection c = Conexao.getConnection()) {
            System.out.println("Conectado!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AssinaturaDAO dao = new AssinaturaDAO();
        try {
//            dao.inserir(new Assinatura("HBO Max", 34.90, "MENSAL", 8, 1));
            double total = 0;
            for (Assinatura a : dao.listar()) {
                System.out.println(a.getNome() + " - " + a.getValorMensal());
                if (a.isAtiva()) {
                    total += a.getValorMensal();
                }
            }
            System.out.printf("Total mensal: %.2f%n", total);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}