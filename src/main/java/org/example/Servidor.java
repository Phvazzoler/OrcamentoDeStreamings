package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;

public class Servidor {


    static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }


    static void juntar(StringBuilder lista, String item) {
        if (lista.length() > 0) {
            lista.append(",");
        }
        lista.append(item);
    }


    static Map<String, String> parametros(String consulta) {
        Map<String, String> mapa = new HashMap<>();
        if (consulta == null) {
            return mapa;
        }
        for (String par : consulta.split("&")) {
            String[] partes = par.split("=", 2);
            String chave = URLDecoder.decode(partes[0], StandardCharsets.UTF_8);
            String valor = partes.length > 1
                    ? URLDecoder.decode(partes[1], StandardCharsets.UTF_8) : "";
            mapa.put(chave, valor);
        }
        return mapa;
    }


    static void responder(HttpExchange requisicao, int codigo) throws IOException {
        requisicao.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        requisicao.sendResponseHeaders(codigo, -1);
    }

    public static void main(String[] args) throws Exception {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);


        servidor.createContext("/api/resumo", requisicao -> {
            try {
                double total = 0;
                StringBuilder assinaturas = new StringBuilder();
                StringBuilder categorias = new StringBuilder();
                StringBuilder desejos = new StringBuilder();

                for (Assinatura a : new AssinaturaDAO().listar()) {
                    if (!a.isAtiva()) {
                        continue;
                    }
                    total += a.getValorMensal();
                    juntar(assinaturas, String.format(Locale.US,
                            "{\"id\":%d,\"nome\":\"%s\",\"valorMensal\":%.2f,\"ciclo\":\"%s\",\"dia\":%d,\"categoriaId\":%d}",
                            a.getId(), escapar(a.getNome()), a.getValorMensal(),
                            escapar(a.getCicloCobranca()), a.getDiaCobranca(), a.getCategoriaId()));
                }

                for (Categoria c : new CategoriaDAO().listar()) {
                    juntar(categorias, String.format(Locale.US,
                            "{\"id\":%d,\"nome\":\"%s\"}", c.getId(), escapar(c.getNome())));
                }

                for (ItemWishlist item : new ItemWishlistDAO().listarPendentes()) {
                    juntar(desejos, String.format(Locale.US,
                            "{\"id\":%d,\"nome\":\"%s\",\"preco\":%.2f,\"prioridade\":\"%s\",\"categoriaId\":%d}",
                            item.getId(), escapar(item.getNome()), item.getPreco(),
                            escapar(item.getPrioridade()), item.getCategoriaId()));
                }

                double limite = new LimiteDAO().buscarLimite();
                String json = String.format(Locale.US,
                        "{\"totalMensal\":%.2f,\"limite\":%.2f,\"estourou\":%b,"
                                + "\"categorias\":[%s],\"assinaturas\":[%s],\"wishlist\":[%s]}",
                        total, limite, total > limite, categorias, assinaturas, desejos);

                byte[] corpo = json.getBytes(StandardCharsets.UTF_8);
                requisicao.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
                requisicao.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                requisicao.sendResponseHeaders(200, corpo.length);
                try (OutputStream saida = requisicao.getResponseBody()) {
                    saida.write(corpo);
                }
            } catch (Exception e) {
                e.printStackTrace();
                requisicao.sendResponseHeaders(500, -1);
            }
        });


        servidor.createContext("/api/assinaturas", requisicao -> {
            try {
                if (!requisicao.getRequestMethod().equals("POST")) {
                    responder(requisicao, 405);
                    return;
                }
                Map<String, String> dados = parametros(requisicao.getRequestURI().getRawQuery());
                Assinatura a = new Assinatura(
                        dados.get("nome"),
                        Double.parseDouble(dados.get("valor")),
                        dados.get("ciclo"),
                        Integer.parseInt(dados.get("dia")),
                        Integer.parseInt(dados.get("categoriaId")));
                new AssinaturaDAO().inserir(a);
                responder(requisicao, 200);
            } catch (Exception e) {
                e.printStackTrace();
                responder(requisicao, 400);
            }
        });


        servidor.createContext("/api/desativar", requisicao -> {
            try {
                if (!requisicao.getRequestMethod().equals("POST")) {
                    responder(requisicao, 405);
                    return;
                }
                Map<String, String> dados = parametros(requisicao.getRequestURI().getRawQuery());
                new AssinaturaDAO().desativar(Integer.parseInt(dados.get("id")));
                responder(requisicao, 200);
            } catch (Exception e) {
                e.printStackTrace();
                responder(requisicao, 400);
            }
        });


        servidor.createContext("/api/wishlist", requisicao -> {
            try {
                if (!requisicao.getRequestMethod().equals("POST")) {
                    responder(requisicao, 405);
                    return;
                }
                Map<String, String> dados = parametros(requisicao.getRequestURI().getRawQuery());
                ItemWishlist item = new ItemWishlist(
                        dados.get("nome"),
                        Double.parseDouble(dados.get("preco")),
                        dados.get("prioridade"),
                        Integer.parseInt(dados.get("categoriaId")));
                new ItemWishlistDAO().inserir(item);
                responder(requisicao, 200);
            } catch (Exception e) {
                e.printStackTrace();
                responder(requisicao, 400);
            }
        });


        servidor.createContext("/api/comprar", requisicao -> {
            try {
                if (!requisicao.getRequestMethod().equals("POST")) {
                    responder(requisicao, 405);
                    return;
                }
                Map<String, String> dados = parametros(requisicao.getRequestURI().getRawQuery());
                new ItemWishlistDAO().marcarComprado(Integer.parseInt(dados.get("id")));
                responder(requisicao, 200);
            } catch (Exception e) {
                e.printStackTrace();
                responder(requisicao, 400);
            }
        });


        servidor.createContext("/", requisicao -> {
            try {
                if (!requisicao.getRequestMethod().equals("GET")) {
                    requisicao.sendResponseHeaders(405, -1);
                    requisicao.close();
                    return;
                }

                Path pastaFront = Path.of("FRONT")
                        .toAbsolutePath()
                        .normalize();

                String caminho = requisicao.getRequestURI().getPath();

                if (caminho.equals("/")) {
                    caminho = "/index.html";
                }

                Path arquivo = pastaFront.resolve(caminho.substring(1))
                        .normalize();


                if (!arquivo.startsWith(pastaFront) ||
                        !Files.exists(arquivo) ||
                        Files.isDirectory(arquivo)) {

                    requisicao.sendResponseHeaders(404, -1);
                    requisicao.close();
                    return;
                }

                byte[] conteudo = Files.readAllBytes(arquivo);

                String tipo = "application/octet-stream";

                if (caminho.endsWith(".html")) {
                    tipo = "text/html; charset=UTF-8";
                } else if (caminho.endsWith(".css")) {
                    tipo = "text/css; charset=UTF-8";
                } else if (caminho.endsWith(".js")) {
                    tipo = "application/javascript; charset=UTF-8";
                } else if (caminho.endsWith(".png")) {
                    tipo = "image/png";
                } else if (caminho.endsWith(".jpg") ||
                        caminho.endsWith(".jpeg")) {
                    tipo = "image/jpeg";
                } else if (caminho.endsWith(".svg")) {
                    tipo = "image/svg+xml";
                }

                requisicao.getResponseHeaders()
                        .set("Content-Type", tipo);

                requisicao.sendResponseHeaders(200, conteudo.length);

                try (OutputStream saida = requisicao.getResponseBody()) {
                    saida.write(conteudo);
                }

            } catch (Exception e) {
                e.printStackTrace();

                try {
                    requisicao.sendResponseHeaders(500, -1);
                } catch (IOException erro) {
                    erro.printStackTrace();
                }

                requisicao.close();
            }
        });


        servidor.start();
        System.out.println("Servidor rodando em http://localhost:8080");
    }
}