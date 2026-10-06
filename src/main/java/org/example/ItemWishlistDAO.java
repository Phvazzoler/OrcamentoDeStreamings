package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemWishlistDAO {

    public List<ItemWishlist> listarPendentes() throws SQLException {
        List<ItemWishlist> lista = new ArrayList<>();
        String sql = "SELECT * FROM wishlist_itens "
                + "WHERE comprado = FALSE ORDER BY prioridade DESC";

        try (Connection c = Conexao.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ItemWishlist i = new ItemWishlist(
                        rs.getString("nome"),
                        rs.getDouble("preco"),
                        rs.getString("prioridade"),
                        rs.getInt("categoria_id")
                );
                i.setId(rs.getInt("id"));
                i.setComprado(rs.getBoolean("comprado"));
                i.setLink(rs.getString("link"));
                lista.add(i);
            }
        }
        return lista;
    }

    public void inserir(ItemWishlist i) throws SQLException {
        String sql = "INSERT INTO wishlist_itens "
                + "(nome, preco, prioridade, categoria_id) VALUES (?, ?, ?, ?)";

        try (Connection c = Conexao.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, i.getNome());
            ps.setDouble(2, i.getPreco());
            ps.setString(3, i.getPrioridade());
            ps.setInt(4, i.getCategoriaId());
            ps.executeUpdate();
        }
    }
}