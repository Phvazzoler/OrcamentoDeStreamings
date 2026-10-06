package org.example;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
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

            // dao.inserir(new Assinatura("HBO Max", 34.90, "MENSAL", 8, 1));
            // dao.desativar(5);
            // dao.desativar(5);
            // dao.deletar(5);

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

        ItemWishlistDAO wishDao = new ItemWishlistDAO();
//        wishDao.inserir(new ItemWishlist("Hollow Knight", 46.99, "BAIXA", 2));
        double totalWishlist = 0;
        for (ItemWishlist i : wishDao.listarPendentes()) {

            System.out.println(i.getNome() + " - " + i.getPreco() + " - " + i.getPrioridade());
            totalWishlist += i.getPreco();
        }
        System.out.printf("Total da wishlist: %.2f%n", totalWishlist);

        }

    }
