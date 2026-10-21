package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LimiteDAO {

    // pega o último limite cadastrado
    public double buscarLimite() throws SQLException {
        String sql = "SELECT valor_limite FROM limite_orcamento ORDER BY id DESC LIMIT 1";

        try (Connection conexao = Conexao.getConexao();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            if (resultado.next()) {
                return resultado.getDouble("valor_limite");
            }
        }
        return 0;
    }
}