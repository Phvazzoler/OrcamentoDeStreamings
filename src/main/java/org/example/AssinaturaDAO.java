package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssinaturaDAO {

    public List<Assinatura> listar() throws SQLException {
        List<Assinatura> lista = new ArrayList<>();
        String sql = "SELECT * FROM assinaturas";

        try (Connection conexao = Conexao.getConexao();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                Assinatura a = new Assinatura(
                        resultado.getString("nome"),
                        resultado.getDouble("valor"),
                        resultado.getString("ciclo_cobranca"),
                        resultado.getInt("dia_cobranca"),
                        resultado.getInt("categoria_id"));
                a.setId(resultado.getInt("id"));
                a.setAtiva(resultado.getBoolean("ativa"));
                lista.add(a);
            }
        }
        return lista;
    }

    public void inserir(Assinatura a) throws SQLException {
        String sql = "INSERT INTO assinaturas "
                + "(nome, valor, ciclo_cobranca, dia_cobranca, categoria_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = Conexao.getConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, a.getNome());
            comando.setDouble(2, a.getValor());
            comando.setString(3, a.getCicloCobranca());
            comando.setInt(4, a.getDiaCobranca());
            comando.setInt(5, a.getCategoriaId());
            comando.executeUpdate();
        }
    }

    public void desativar(int id) throws SQLException {
        String sql = "UPDATE assinaturas SET ativa = FALSE WHERE id = ?";

        try (Connection conexao = Conexao.getConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM assinaturas WHERE id = ?";

        try (Connection conexao = Conexao.getConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            comando.executeUpdate();
        }
    }
}