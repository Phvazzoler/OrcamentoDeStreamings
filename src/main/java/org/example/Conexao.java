package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/orcamento_streamings";
    private static final String USUARIO = "root";
    private static final String SENHA = System.getenv("DB_SENHA");

    public static Connection getConexao() throws SQLException {
        if (SENHA == null) {
            throw new SQLException("A variável de ambiente DB_SENHA não foi definida");
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}