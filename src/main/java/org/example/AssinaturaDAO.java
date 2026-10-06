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

        try (Connection c = Conexao.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while (rs.next()) {
                Assinatura a = new Assinatura(
                        rs.getString("nome"),
                        rs.getDouble("valor"),
                        rs.getString("ciclo_cobranca"),
                        rs.getInt("dia_cobranca"),
                        rs.getInt("categoria_id")
                );
                a.setId(rs.getInt("id"));
                a.setAtiva(rs.getBoolean("ativa"));
                lista.add(a);


            }
        }
        return lista;
    }
    public void inserir(Assinatura a) throws SQLException {
        String sql = "INSERT INTO assinaturas "
                + "(nome, valor, ciclo_cobranca, dia_cobranca, categoria_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection c = Conexao.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, a.getNome());
            ps.setDouble(2, a.getValor());
            ps.setString(3, a.getCicloCobranca());
            ps.setInt(4, a.getDiaCobranca());
            ps.setInt(5, a.getCategoriaId());
            ps.executeUpdate();
        }
    }
    public void desativar(int id) throws SQLException {
        String sql = "UPDATE assinaturas SET ativa = FALSE WHERE id = ?";

        try (Connection c = Conexao.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM assinaturas WHERE id = ?";

        try (Connection c = Conexao.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

}
