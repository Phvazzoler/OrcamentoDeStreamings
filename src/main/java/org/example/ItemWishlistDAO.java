package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemWishlistDAO {

    // só os itens que ainda não foram comprados, prioridade ALTA primeiro
    public List<ItemWishlist> listarPendentes() throws SQLException {
        List<ItemWishlist> lista = new ArrayList<>();
        String sql = "SELECT * FROM wishlist_itens "
                + "WHERE comprado = FALSE ORDER BY prioridade DESC";

        try (Connection conexao = Conexao.getConexao();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                ItemWishlist item = new ItemWishlist(
                        resultado.getString("nome"),
                        resultado.getDouble("preco"),
                        resultado.getString("prioridade"),
                        resultado.getInt("categoria_id"));
                item.setId(resultado.getInt("id"));
                item.setComprado(resultado.getBoolean("comprado"));
                item.setLink(resultado.getString("link"));
                lista.add(item);
            }
        }
        return lista;
    }

    public void inserir(ItemWishlist item) throws SQLException {
        String sql = "INSERT INTO wishlist_itens "
                + "(nome, preco, prioridade, categoria_id) VALUES (?, ?, ?, ?)";

        try (Connection conexao = Conexao.getConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, item.getNome());
            comando.setDouble(2, item.getPreco());
            comando.setString(3, item.getPrioridade());
            comando.setInt(4, item.getCategoriaId());
            comando.executeUpdate();
        }
    }

    public void marcarComprado(int id) throws SQLException {
        String sql = "UPDATE wishlist_itens SET comprado = TRUE WHERE id = ?";

        try (Connection conexao = Conexao.getConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }
}